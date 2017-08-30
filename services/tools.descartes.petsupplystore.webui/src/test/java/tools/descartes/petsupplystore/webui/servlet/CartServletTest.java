package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.entities.message.SessionBlob;

public class CartServletTest extends AbstractUiTest {

	@Test
	public void testCartAction() throws IOException, ServletException, InterruptedException {
		mockCategories(1);
		mockValidPostRestCall(new ArrayList<Product>(), "/tools.descartes.petsupplystore.store/rest/products/ads");
		mockValidPostRestCall(null, "/tools.descartes.petsupplystore.store/rest/useractions/isloggedin");
		mockValidPostRestCall(new HashMap<Long, String>(),
				"/tools.descartes.petsupplystore.image/rest/image/getProductImages");
		mockValidGetRestCall(new Product(),
				"/tools.descartes.petsupplystore.store/rest/products/0"); 
		

		String html = doGet();
		Assert.assertEquals("There are no order, thus there should be no \"Proceed to Checkout\" button", 0,
				countString("Proceed to Checkout", html));

		SessionBlob blob = new SessionBlob();
		blob.setSID("1");
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		orderItems.add(new OrderItem());
		blob.setOrderItems(orderItems);
		mockValidPostRestCall(blob, "/tools.descartes.petsupplystore.store/rest/useractions/isloggedin");
		ObjectMapper o = new ObjectMapper();
		String value = URLEncoder.encode(o.writeValueAsString(blob), "UTF-8");
		html = doGet("", "sessionBlob", value);
		Assert.assertEquals("There is one order, thus there should be the \"Proceed to Checkout\" button", 1,
				countString("Proceed to Checkout", html));

	}

	@Override
	protected Servlet getServlet() {
		return new CartServlet();
	}

}
