package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.webui.servlet.CartServlet;

public class CartServletTest extends AbstractUiTest {

	@Test
	public void testCartAction() throws IOException, ServletException, InterruptedException {
		mockCategories(1);
		mockValidPostRestCall(new ArrayList<Product>(), "/tools.descartes.teastore.recommender/rest/recommend");
		mockValidPostRestCall(null, "/tools.descartes.teastore.auth/rest/useractions/isloggedin");
		mockValidPostRestCall(new HashMap<Long, String>(),
				"/tools.descartes.teastore.image/rest/image/getProductImages");
		mockValidGetRestCall(new Product(), "/tools.descartes.teastore.persistence/rest/products/0");

		String html = doGet();
		Assert.assertEquals("There are no order, thus there should be no \"Proceed to Checkout\" button", 0,
				countString("Proceed to Checkout", html));

		SessionBlob blob = new SessionBlob();
		blob.setSID("1");
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		orderItems.add(new OrderItem());
		blob.setOrderItems(orderItems);
		mockValidPostRestCall(blob, "/tools.descartes.teastore.auth/rest/useractions/isloggedin");
		ObjectMapper o = new ObjectMapper();
		String value = URLEncoder.encode(o.writeValueAsString(blob), "UTF-8");
		html = doGet("", "sessionBlob", value);
		Assert.assertEquals("There is one order, thus there should be the \"Proceed to Checkout\" button", 1,
				countString("Proceed to Checkout", html));

		List<Integer> categories = Arrays.asList(0, 1, 3, 5, 20);
		for (int category : categories) {
			blob = new SessionBlob();
			blob.setSID("1");
			orderItems = new ArrayList<OrderItem>();
			for (int i = 0; i < category; i++) {
				orderItems.add(new OrderItem());
			}
			blob.setOrderItems(orderItems);
			mockValidPostRestCall(blob, "/tools.descartes.teastore.auth/rest/useractions/isloggedin");
			o = new ObjectMapper();
			value = URLEncoder.encode(o.writeValueAsString(blob), "UTF-8");
			html = doGet("", "sessionBlob", value);
			Assert.assertEquals("There should be " + category + " order", category, countString("orderitem", html));
		}

	}

	@Override
	protected Servlet getServlet() {
		return new CartServlet();
	}

}
