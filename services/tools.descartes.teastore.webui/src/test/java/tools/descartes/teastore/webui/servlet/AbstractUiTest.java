package tools.descartes.teastore.webui.servlet;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.junit.Before;
import org.junit.Rule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.webui.servlet.IndexServlet;
import tools.descartes.teastore.webui.servlet.LoginServlet;
import tools.descartes.teastore.webui.servlet.OrderServlet;
import tools.descartes.teastore.entities.Category;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

public abstract class AbstractUiTest {
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(9001);

	private static final String CONTEXT = "/test";
	private Tomcat webUITomcat;
	private String testWorkingDir = System.getProperty("java.io.tmpdir");

	@Before
	public void setup() throws ServletException, LifecycleException, InterruptedException, JsonProcessingException {
		webUITomcat = new Tomcat();
		webUITomcat.setPort(3000);
		webUITomcat.setBaseDir(testWorkingDir);
		webUITomcat.enableNaming();
		Context context = webUITomcat.addWebapp(CONTEXT, System.getProperty("user.dir") + File.separator + "src"
				+ File.separator + "main" + File.separator + "webapp");
		ContextEnvironment registryURL = new ContextEnvironment();
		registryURL.setDescription("");
		registryURL.setOverride(false);
		registryURL.setType("java.lang.String");
		registryURL.setName("registryURL");
		registryURL.setValue("http://localhost:9001/tools.descartes.teastore.registry/rest/services/");
		context.getNamingResources().addEnvironment(registryURL);
		webUITomcat.addServlet(CONTEXT, "servlet", getServlet());
		webUITomcat.addServlet(CONTEXT, "index", new IndexServlet());
		webUITomcat.addServlet(CONTEXT, "login", new LoginServlet());
		webUITomcat.addServlet(CONTEXT, "order", new OrderServlet());
		context.addServletMappingDecoded("/test", "servlet");
		context.addServletMappingDecoded("/index", "index");
		context.addServletMappingDecoded("/login", "login");
		context.addServletMappingDecoded("/order", "order");
		context.addWelcomeFile("/index");
		webUITomcat.start();

		// Mock registry
		List<String> strings = new LinkedList<String>();
		strings.add("localhost:9001");
		String json = new ObjectMapper().writeValueAsString(strings);
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.teastore.registry/rest/services/" + Service.IMAGE.getServiceName() + "/"))
						.willReturn(okJson(json)));
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.teastore.registry/rest/services/" + Service.AUTH.getServiceName() + "/"))
						.willReturn(okJson(json)));
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.teastore.registry/rest/services/" + Service.PERSISTENCE.getServiceName() + "/"))
						.willReturn(okJson(json)));
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.teastore.registry/rest/services/" + Service.RECOMMENDER.getServiceName() + "/"))
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
		mockValidPostRestCall(img, "/tools.descartes.teastore.image/rest/image/getWebImages");
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

	protected int countString(String token, String html) {
		String[] webpage = html.split("\n");
		int count = 0;
		for (String line : webpage) {
			if (line.contains(token)) {
				count++;
			}
		}
		return count;
	}

	protected void mockCategories(int numberCategories) {
		List<Category> categories = new LinkedList<Category>();
		for (int i = 0; i < numberCategories; i++) {
			Category category = new Category();
			category.setId(i);
			category.setName("Category " + i);
			category.setDescription("Description " + i);
			categories.add(category);
		}
		mockValidGetRestCall(categories, "/tools.descartes.teastore.persistence/rest/categories");
	}

	protected String doGet() {
		return doGet("", "", "");
	}

	protected String doGet(String requestparams) {
		return doGet(requestparams, "", "");
	}

	protected String doGet(String requestparams, String cookiename, String value) {
		try {
			if (!requestparams.equals("") && !requestparams.startsWith("?")) {
				requestparams = "?" + requestparams;
			}
			URL obj;
			String url = "http://localhost:3000/test/test" + requestparams;
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			if (!cookiename.equals("") && !value.equals("")) {
				con.setRequestProperty("Cookie", cookiename + "=" + value);
			}
			
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine + "\n");
				}
			} 
			
			return response.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected String[] getPagination(String html) {
		ArrayList<String> pagination = new ArrayList<String>();
		String[] webpage = html.split("\n");
		boolean start = false;
		for (String line : webpage) {
			if (line.contains("pagination")) {
				start = true;
			}
			if (start && line.contains("page")) {
				line = line.replace("</a>", "").replace("</li>", "").substring(line.indexOf(">") + 1);
				pagination.add(line);
			}
			if (start && line.contains("</ul>")) {
				start = false;
			}
		}
		String[] page = new String[pagination.size()];
		for (int i = 0; i < page.length; i++) {
			page[i] = pagination.get(i);
		}

		return page;
	}

	protected String doPost(String query) throws IOException {
		return doPost(query, "", "");
	}

	protected String doPost(String query, String cookie, String value) throws IOException {
		URL url = new URL("http://localhost:3000/test/test");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (!cookie.equals("") && !value.equals("")) {
			connection.addRequestProperty("Cookie", cookie + "=" + value);
			connection.setInstanceFollowRedirects(false);

		}

		connection.setRequestMethod("POST");
		connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		connection.addRequestProperty("Content-Length", "" + Integer.toString(query.getBytes().length));
		connection.addRequestProperty("Content-Language", "en-US");

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		// Send request
		try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			wr.writeBytes(query);
			wr.flush();
			wr.close();
		}

		boolean redirect = false;
		int status = connection.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER)
				redirect = true;
		}

		if (redirect) {
			String newUrl = "http://" + url.getHost() + ":" + url.getPort() + connection.getHeaderField("Location");

			// get the cookie if need, for login
			String cookies = connection.getHeaderField("Set-Cookie");

			// open the new connnection again
			connection = (HttpURLConnection) new URL(newUrl).openConnection();
			connection.setRequestProperty("Cookie", cookies);
			connection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			connection.addRequestProperty("User-Agent", "Mozilla");
			connection.addRequestProperty("Referer", "google.com");

		}

		String inputLine;
		StringBuffer response = new StringBuffer();
		
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine + "\n");
			}
		}
		return response.toString();
	}

	protected String getWebSiteTitle(String html) {
		String[] webpage = html.split("\n");
		for (String line : webpage) {
			if (line.contains("title")) {
				return line.replace("<title>", "").replace("</title>", "");
			}
		}
		return "";
	}

	protected abstract Servlet getServlet();
}