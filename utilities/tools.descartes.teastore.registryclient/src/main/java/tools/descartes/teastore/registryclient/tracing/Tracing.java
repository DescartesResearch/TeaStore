package tools.descartes.teastore.registryclient.tracing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.HttpHeaders;

import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.propagation.B3TextMapCodec;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Scope;
import io.opentracing.Span;
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
    return new JaegerTracer.Builder(service).withSampler(new ConstSampler(true)).withZipkinSharedRpcSpan()
        .registerInjector(Format.Builtin.HTTP_HEADERS, new B3TextMapCodec.Builder().build())
        .registerExtractor(Format.Builtin.HTTP_HEADERS, new B3TextMapCodec.Builder().build()).build();
  }

  /**
   * This function is used to inject the current span context into the request to
   * be made.
   *
   * @param requestBuilder The requestBuilder object that gets injected
   */
  public static void inject(Invocation.Builder requestBuilder) {
    Span activeSpan = GlobalTracer.get().activeSpan();
    if (activeSpan != null) {
      GlobalTracer.get().inject(activeSpan.context(), Format.Builtin.HTTP_HEADERS,
          Tracing.requestBuilderCarrier(requestBuilder));
    }
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
    return buildSpanFromHeaders(headers, request.getRequestURI());
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
    return buildSpanFromHeaders(headers, "op");
  }

  /**
   * Helper method to extract and build the active span out of Map containing the
   * processed headers.
   *
   * @param headers is the Map of the processed headers
   * @param operationName is the operation name of the span (can be either URL or URI)
   * @return Scope containing the extracted span marked as active. Can be used
   *         with try-with-resource construct
   */
  private static Scope buildSpanFromHeaders(Map<String, String> headers, String operationName) {
    Tracer.SpanBuilder spanBuilder = GlobalTracer.get().buildSpan(operationName);
    try {
      SpanContext parentSpanCtx = GlobalTracer.get().extract(Format.Builtin.HTTP_HEADERS,
              new TextMapExtractAdapter(headers));
      if (parentSpanCtx != null) {
        spanBuilder = spanBuilder.asChildOf(parentSpanCtx);
      }
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
    return spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT).startActive(true);
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
        builder.header(key, value);
      }
    };
  }
}