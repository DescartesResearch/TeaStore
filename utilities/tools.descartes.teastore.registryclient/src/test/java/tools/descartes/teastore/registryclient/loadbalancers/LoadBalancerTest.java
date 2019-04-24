/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.registryclient.loadbalancers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.Before;
import org.junit.Test;

import com.netflix.loadbalancer.Server;

import kieker.monitoring.core.controller.MonitoringController;

import org.junit.Assert;

import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.rest.NonBalancedCRUDOperations;
import tools.descartes.teastore.registryclient.test.NotFoundServlet;
import tools.descartes.teastore.registryclient.test.SlowTimeoutingServlet;
import tools.descartes.teastore.registryclient.test.TestServlet;
import tools.descartes.teastore.registryclient.test.TimeoutStatusServlet;
import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.Product;

/**
 * Test the load balancer.
 * @author Joakim von Kistowski
 *
 */
public class LoadBalancerTest {
	
	private static final int NUM_SERVERS = 4;
	private static final Service SERVICE = Service.AUTH;
	private static final String CONTEXT = "/" + SERVICE.getServiceName();
	private static final String ENDPOINT = "products";
	private static final String NOT_FOUND_ENDPOINT = "categories";
	private static final String TIMEOUT_STATUS_ENDPOINT = "statustimeouts";
	private static final String TIMEOUTING_ENDPOINT = "actualtimeouts";
	
	private List<Tomcat> testTomcats;
	private String testWorkingDir = System.getProperty("java.io.tmpdir");
	
	private void setupAndAddTestTomcat(int i) {
		Tomcat testTomcat = new Tomcat();
		testTomcat.setPort(0);
		testTomcat.setBaseDir(testWorkingDir);
		Context context;
		try {
			context = testTomcat.addWebapp(CONTEXT, testWorkingDir);
			testTomcat.getEngine().setName("Catalina" + i);
			TestServlet testServlet = new TestServlet();
			testServlet.setId(i);
			testTomcat.addServlet(CONTEXT, "notFoundServlet", new NotFoundServlet());
			testTomcat.addServlet(CONTEXT, "timeoutStatusServlet", new TimeoutStatusServlet());
			testTomcat.addServlet(CONTEXT, "timeoutingServlet", new SlowTimeoutingServlet());
			testTomcat.addServlet(CONTEXT, "restServlet", testServlet);
			context.addServletMappingDecoded("/rest/" + ENDPOINT, "restServlet");
			context.addServletMappingDecoded("/rest/" + ENDPOINT + "/*", "restServlet");
			context.addServletMappingDecoded("/rest/" + NOT_FOUND_ENDPOINT, "notFoundServlet");
			context.addServletMappingDecoded("/rest/" + NOT_FOUND_ENDPOINT + "/*", "notFoundServlet");
			context.addServletMappingDecoded("/rest/" + TIMEOUT_STATUS_ENDPOINT, "timeoutStatusServlet");
			context.addServletMappingDecoded("/rest/" + TIMEOUT_STATUS_ENDPOINT + "/*", "timeoutStatusServlet");
			context.addServletMappingDecoded("/rest/" + TIMEOUTING_ENDPOINT, "timeoutStatusServlet");
			context.addServletMappingDecoded("/rest/" + TIMEOUTING_ENDPOINT + "/*", "timeoutStatusServlet");
			testTomcats.add(testTomcat);
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Setup the test by deploying embedded tomcats and adding the endpoints.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		testTomcats = new ArrayList<>(NUM_SERVERS + 1);
		IntStream.range(0, NUM_SERVERS).forEach(i -> setupAndAddTestTomcat(i));
		MonitoringController.getInstance().disableMonitoring();		
	}
	
	/**
	 * Run the test.
	 * @throws Throwable Throws on failure.
	 */
	@Test
	public void testEndpoint() throws Throwable {
		testTomcats.forEach(t -> {
			try {
				t.start();
			} catch (LifecycleException e) {
				Assert.fail("failed starting a tomcat.");
			}
		});
		List<Server> servers = tomcatsToServers();
		ServiceLoadBalancer.getServiceLoadBalancer(Service.AUTH, servers);
		
		//send to all servers
		ArrayList<Long> ids = new ArrayList<>();
		for (int i = 0; i < NUM_SERVERS * 4; i++) {
			long id = ServiceLoadBalancer.loadBalanceRESTOperation(SERVICE,
					ENDPOINT, Product.class, client -> NonBalancedCRUDOperations.getEntity(client, 0)).getId();
			ids.add(id);
		}
		Assert.assertEquals(NUM_SERVERS * 4, ids.size());
		//send error call that receives a 404 response to all servers
		int notFoundExceptionCount = 0;
		for (int i = 0; i < NUM_SERVERS * 4; i++) {
			try {
				ServiceLoadBalancer.loadBalanceRESTOperation(SERVICE, NOT_FOUND_ENDPOINT,
						Category.class, client -> NonBalancedCRUDOperations.getEntity(client, 0)).getId();
			} catch (NotFoundException e) {
				notFoundExceptionCount++;
			}
		}
		Assert.assertEquals(NUM_SERVERS * 4, notFoundExceptionCount);
		//send error call that receives a 408 response to all servers
		int timoutExceptionCount = 0;
		for (int i = 0; i < NUM_SERVERS * 4; i++) {
			try {
				ServiceLoadBalancer.loadBalanceRESTOperation(SERVICE, TIMEOUT_STATUS_ENDPOINT,
						Category.class, client -> NonBalancedCRUDOperations.getEntity(client, 0)).getId();
			} catch (LoadBalancerTimeoutException e) {
				timoutExceptionCount++;
			}
		}
		Assert.assertEquals(NUM_SERVERS * 4, timoutExceptionCount);
		//send multicast
		List<Product> products = ServiceLoadBalancer.multicastRESTOperation(SERVICE, ENDPOINT, Product.class,
				client -> NonBalancedCRUDOperations.getEntity(client, 0));
		Assert.assertEquals(NUM_SERVERS, products.size());
		
		//stop one server, don't notify load balancer
		testTomcats.get(1).stop();
		ids.clear();
		for (int i = 0; i < NUM_SERVERS * 4; i++) {
			long id = ServiceLoadBalancer.loadBalanceRESTOperation(SERVICE,
					ENDPOINT, Product.class, client -> NonBalancedCRUDOperations.getEntity(client, 0)).getId();
			ids.add(id);
		}
		Assert.assertEquals(NUM_SERVERS * 4, ids.size());
		Assert.assertFalse(ids.contains(1L));
		for (long i = 0; i < NUM_SERVERS; i++) {
			if (i != 1) {
				Assert.assertTrue(ids.contains(i));
			}
		}
		//send multicast
		products = ServiceLoadBalancer.multicastRESTOperation(SERVICE, ENDPOINT, Product.class,
				client -> NonBalancedCRUDOperations.getEntity(client, 0));
		Assert.assertEquals(NUM_SERVERS, products.size());
		Assert.assertTrue(products.contains(null));
		
		//stop another server, don't notify load balancer
		testTomcats.get(2).stop();
		ids.clear();
		for (int i = 0; i < NUM_SERVERS * 4; i++) {
			long id = ServiceLoadBalancer.loadBalanceRESTOperation(SERVICE,
					ENDPOINT, Product.class, client -> NonBalancedCRUDOperations.getEntity(client, 0)).getId();
			ids.add(id);
		}
		Assert.assertEquals(NUM_SERVERS * 4, ids.size());
		Assert.assertFalse(ids.contains(1L));
		Assert.assertFalse(ids.contains(2L));
		for (long i = 0; i < NUM_SERVERS; i++) {
			if (i != 1 && i != 2) {
				Assert.assertTrue(ids.contains(i));
			}
		}
		
		//add a new server, notify load balancer on all changes
		setupAndAddTestTomcat(NUM_SERVERS);
		try {
			testTomcats.get(NUM_SERVERS).start();
		} catch (LifecycleException e) {
			Assert.fail("failed starting a tomcat.");
		}
		servers.remove(2);
		servers.remove(1);
		servers.add(new Server("localhost", getPort(testTomcats.get(NUM_SERVERS))));
		ServiceLoadBalancer.updateLoadBalancersForService(SERVICE, servers);
		
		//send requests to only remaining and new server using load balancer
		ids.clear();
		for (int i = 0; i < NUM_SERVERS * 4; i++) {
			long id = ServiceLoadBalancer.loadBalanceRESTOperation(SERVICE,
					ENDPOINT, Product.class, client -> NonBalancedCRUDOperations.getEntity(client, 0)).getId();
			ids.add(id);
		}
		Assert.assertEquals(NUM_SERVERS * 4, ids.size());
		Assert.assertFalse(ids.contains(1L));
		Assert.assertFalse(ids.contains(2L));
		for (long i = 0; i <= NUM_SERVERS; i++) {
			if (i != 1 && i != 2) {
				Assert.assertTrue(ids.contains(i));
			}
		}
		
		//send request to super slow servlet and expect the rst client and load balancer to time out
		boolean isTimeout = false;
		try {
			ServiceLoadBalancer.loadBalanceRESTOperation(SERVICE, TIMEOUTING_ENDPOINT,
					Product.class, client -> NonBalancedCRUDOperations.getEntity(client, 0)).getId();
		} catch (LoadBalancerTimeoutException e) {
			isTimeout = true;
		}
		Assert.assertTrue(isTimeout);
	}
	
	private List<Server> tomcatsToServers() {
		LinkedList<Server> servers = new LinkedList<>();
		for (Tomcat t : testTomcats) {
			servers.add(new Server("localhost", getPort(t)));
		}
		return servers;
	}
	
	private int getPort(Tomcat tomcat) {
		return tomcat.getConnector().getLocalPort();
	}
	
}
