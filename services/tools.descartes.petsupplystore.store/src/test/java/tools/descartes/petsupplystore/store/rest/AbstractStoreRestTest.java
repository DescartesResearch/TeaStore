package tools.descartes.petsupplystore.store.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.store.rest.StoreCartREST;
import tools.descartes.petsupplystore.store.rest.StoreCategoriesREST;
import tools.descartes.petsupplystore.store.rest.StoreProductREST;
import tools.descartes.petsupplystore.store.rest.StoreUserActionsREST;
import tools.descartes.petsupplystore.store.rest.StoreUserREST;



/**
 * Abstract base for testing of the stores rest functionality.
 * @author Simon
 *
 */
public abstract class AbstractStoreRestTest {
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(18080);
	
	private Tomcat storeTomcat;
	private String testWorkingDir = System.getProperty("java.io.tmpdir");
	/**
	 * Sets up a store.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		storeTomcat = new Tomcat();
		storeTomcat.setPort(3000);
		storeTomcat.setBaseDir(testWorkingDir);
		storeTomcat.enableNaming();
		Context context3 = storeTomcat.addWebapp("/tools.descartes.petsupplystore.store", testWorkingDir);
		ContextEnvironment registryURL3 = new ContextEnvironment();
		registryURL3.setDescription("");
		registryURL3.setOverride(false);
		registryURL3.setType("java.lang.String");
		registryURL3.setName("registryURL");
		registryURL3.setValue("http://localhost:18080/tools.descartes.petsupplystore.registry/rest/services/");
		context3.getNamingResources().addEnvironment(registryURL3);
		ContextEnvironment servicePort3 = new ContextEnvironment();
		servicePort3.setDescription("");
		servicePort3.setOverride(false);
		servicePort3.setType("java.lang.String");
	    servicePort3.setName("servicePort");
	    servicePort3.setValue("3000");
		context3.getNamingResources().addEnvironment(servicePort3);
		ResourceConfig restServletConfig3 = new ResourceConfig();
		restServletConfig3.register(StoreCartREST.class);
		restServletConfig3.register(StoreCategoriesREST.class);
		restServletConfig3.register(StoreProductREST.class);
		restServletConfig3.register(StoreUserActionsREST.class);
		restServletConfig3.register(StoreUserREST.class);
		ServletContainer restServlet3 = new ServletContainer(restServletConfig3);
		storeTomcat.addServlet("/tools.descartes.petsupplystore.store", "restServlet", restServlet3);
		context3.addServletMappingDecoded("/rest/*", "restServlet");
		context3.addApplicationListener(EmptyStoreStartup.class.getName());

		// Mock registry
		List<String> strings = new LinkedList<String>();
		strings.add("localhost:18080");
		String json = new ObjectMapper().writeValueAsString(strings);
		List<String> strings2 = new LinkedList<String>();
		strings2.add("localhost:3000");
		String json2 = new ObjectMapper().writeValueAsString(strings2);
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.IMAGE.getServiceName() + "/"))
						.willReturn(okJson(json)));
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.STORE.getServiceName() + "/"))
						.willReturn(okJson(json2)));
		wireMockRule.stubFor(WireMock.put(WireMock.urlMatching(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.STORE.getServiceName() + "/.*"))
						.willReturn(okJson(json2)));
		wireMockRule.stubFor(WireMock.delete(WireMock.urlMatching(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.STORE.getServiceName() + "/.*"))
						.willReturn(okJson(json2)));
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
		
		storeTomcat.start();
	}

	/**
	 * Dismantles the embedded Tomcat.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@After
	public void dismantle() throws Throwable {
		if (storeTomcat.getServer() != null && storeTomcat.getServer().getState() != LifecycleState.DESTROYED) {
	        if (storeTomcat.getServer().getState() != LifecycleState.STOPPED) {
	        	storeTomcat.stop();
	        }
	        storeTomcat.destroy();
	    }
	}
	
	protected void mockValidPostRestCall(Object input, String path) {
		try {
			wireMockRule
					.stubFor(post(urlEqualTo(path)).willReturn(okJson(new ObjectMapper().writeValueAsString(input))));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	protected void mockInValidGetRestCall(Status status, String path) {
		wireMockRule.stubFor(get(urlEqualTo(path)).willReturn(WireMock.status(status.getStatusCode())));
	}

	protected void mockValidGetRestCall(Object input, String path) {
		try {
			wireMockRule
					.stubFor(get(urlEqualTo(path)).willReturn(okJson(new ObjectMapper().writeValueAsString(input))));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
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
		mockValidGetRestCall(categories, "/tools.descartes.petsupplystore.store/rest/categories");
	}

	protected void mockProduct106() {
		Product p = new Product();
		p.setCategoryId(1);
		p.setDescription("desc");
		p.setId(106);
		p.setListPriceInCents(99);
		p.setName("a product");
		mockValidGetRestCall(p, "/tools.descartes.petsupplystore.persistence/rest/products/106");
	}
	
	protected void mockInvalidProduct() {
		mockInValidGetRestCall(Response.Status.NOT_FOUND, "/tools.descartes.petsupplystore.persistence/rest/products/-1");
	}

	protected void mockProduct107() {
		Product p = new Product();
		p.setCategoryId(2);
		p.setDescription("desc");
		p.setId(107);
		p.setListPriceInCents(99);
		p.setName("another product");
		mockValidGetRestCall(p, "/tools.descartes.petsupplystore.persistence/rest/products/107");
	}
}
