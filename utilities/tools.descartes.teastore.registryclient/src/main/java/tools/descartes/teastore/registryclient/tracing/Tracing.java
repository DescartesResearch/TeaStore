package tools.descartes.teastore.registryclient.tracing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;

import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

/**
 * Utility functions for OpenTracing integration.
 *
 * @author Long Bui
 */
public final class Tracing {
  private Tracing() {
  }

  /**
   * This function is used to create an Tracer instance to be used as the
   * GlobalTracer.
   *
   * @param service is usually the name of the service
   * @return Tracer intended to be used as GlobalTracer
   */
  public static Tracer init(String service) {
    return new JaegerTracer.Builder(service).withSampler(new ConstSampler(true)).withZipkinSharedRpcSpan().build();
  }

  /**
   * This function is used to inject the current span context into the request to
   * be made
   *
   * @param requestBuilder The requestBuilder object that gets injected
   */
  public static void inject(Invocation.Builder requestBuilder) {
    GlobalTracer.get().inject(GlobalTracer.get().activeSpan().context(), Format.Builtin.HTTP_HEADERS,
        Tracing.requestBuilderCarrier(requestBuilder));
  }

  /**
   * Overloaded function used to extract span information out of an
   * HttpServletRequest instance.
   *
   * @param request is the HttpServletRequest isntance with the potential span
   *                informations
   * @return Scope containing the extracted span marked as active. Can be used
   *         with try-with-resource construct
   */
  public static Scope extractCurrentSpan(HttpServletRequest request) {
    Map<String, String> headers = new HashMap<>();
    for (String headerName : Collections.list(request.getHeaderNames())) {
      headers.put(headerName, request.getHeader(headerName));
    }
    return buildSpanFromHeaders(headers);
  }

  /**
   * Overloaded function used to extract span information out of an HttpHeaders
   * instance.
   *
   * @param httpHeaders is the HttpHeaders instance with the potential span
   *                    informations
   * @return Scope containing the extracted span marked as active. Can be used
   *         with try-with-resource construct
   */
  public static Scope extractCurrentSpan(HttpHeaders httpHeaders) {
    Map<String, String> headers = new HashMap<>();
    for (String headerName : httpHeaders.getRequestHeaders().keySet()) {
      headers.put(headerName, httpHeaders.getRequestHeader(headerName).get(0));
    }
    return buildSpanFromHeaders(headers);
  }

  /**
   * Helper method to extract and build the active span out of Map containing the
   * processed headers.
   *
   * @param headers is the Map of the processed headers
   * @return Scope containing the extracted span marked as active. Can be used
   *         with try-with-resource construct
   */
  private static Scope buildSpanFromHeaders(Map<String, String> headers) {
    Tracer.SpanBuilder spanBuilder = GlobalTracer.get().buildSpan("op");
    try {
      SpanContext parentSpanCtx = GlobalTracer.get().extract(Format.Builtin.HTTP_HEADERS,
          new TextMapExtractAdapter(headers));
      if (parentSpanCtx != null) {
        spanBuilder = GlobalTracer.get().buildSpan("op").asChildOf(parentSpanCtx);
      }
    } catch (IllegalArgumentException e) {
    }
    return spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER).startActive(true);
  }

  /**
   * Returns a TextMap Adapter for Invocation.Builder instance.
   *
   * @param builder is the construct where the span information should be injected
   *                to
   * @return the TextMap adapter which can be used for injection
   */
  public static TextMap requestBuilderCarrier(final Invocation.Builder builder) {
    return new TextMap() {
      @Override
      public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("carrier is write-only");
      }

      @Override
      public void put(String key, String value) {
        System.out.println("Inserted header" + key + ": " + value);
        builder.header(key, value);
      }
    };
  }
}