package tools.descartes.petsupplystore.webui.servlet;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.Before;
import org.junit.Rule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import tools.descartes.petsupplystore.registryclient.Service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

public abstract class AbstractUiTest {
	@Rule
	public WireMockRule wireMockRule = new WireMockRule();

	private static final String CONTEXT = "/test";
	private Tomcat webUITomcat;
	private String testWorkingDir = System.getProperty("java.io.tmpdir");

	@Before
	public void setup() throws ServletException, LifecycleException, InterruptedException, JsonProcessingException {
		webUITomcat = new Tomcat();
		webUITomcat.setPort(3000);
		webUITomcat.setBaseDir(testWorkingDir);
		Context context = webUITomcat.addWebapp(CONTEXT, System.getProperty("user.dir") + "\\src\\main\\webapp");
		webUITomcat.addServlet(CONTEXT, "servlet", getServlet());
		context.addServletMappingDecoded("/test", "servlet");
		webUITomcat.start();
		
		// Mock registry
		List<String> strings = new LinkedList<String>();
		strings.add("localhost:8080");
		String json = new ObjectMapper().writeValueAsString(strings);
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.IMAGE.getServiceName() + "/"))
						.willReturn(okJson(json)));
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.STORE.getServiceName() + "/"))
						.willReturn(okJson(json)));
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.PERSISTENCE.getServiceName() + "/"))
						.willReturn(okJson(json)));
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.RECOMMENDER.getServiceName() + "/"))
						.willReturn(okJson(json)));
		
		// Mock images 
		HashMap<String, String> img = new HashMap<>();
		img.put("andreBauer", "andreBauer");
		img.put("johannesGrohmann", "johannesGrohmann");
		img.put("joakimKistowski", "joakimKistowski");
		img.put("simonEismann", "simonEismann");
		img.put("norbertSchmitt", "norbertSchmitt");
		img.put("descartesLogo", "descartesLogo");
		img.put("icon", "icon");
		mockValidPostRestCall(img, "/tools.descartes.petsupplystore.image/rest/image/getWebImages");
	}

	protected void mockValidPostRestCall(Object input, String path) {
		try {
			wireMockRule
					.stubFor(post(urlEqualTo(path)).willReturn(okJson(new ObjectMapper().writeValueAsString(input))));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	protected void mockValidGetRestCall(Object input, String path) {
		try {
			wireMockRule
					.stubFor(get(urlEqualTo(path)).willReturn(okJson(new ObjectMapper().writeValueAsString(input))));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	protected String getResultingHTML() {
		try {
			URL obj;
			BufferedReader in = null;
			String url = "http://localhost:3000/test/test";
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected abstract Servlet getServlet();
}