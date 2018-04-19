package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.webui.servlet.CartActionServlet;

public class CartActionTest extends AbstractUiTest {

	@Test
	public void testCartAction() throws IOException, ServletException, InterruptedException {
		mockCategories(1);
		mockValidPostRestCall(null,  "/tools.descartes.teastore.auth/rest/useractions/isloggedin");
		
		String html = doPost("proceedtoCheckout=");
		Assert.assertEquals("User is not logged in, thus redirect to login", "TeaStore Login", getWebSiteTitle(html));
		
		SessionBlob blob = new SessionBlob();
		blob.setSID("1");
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		orderItems.add(new OrderItem());
		blob.setOrderItems(orderItems);
		mockValidPostRestCall(blob,  "/tools.descartes.teastore.auth/rest/useractions/isloggedin");
		ObjectMapper o = new ObjectMapper();
		String value = URLEncoder.encode(o.writeValueAsString(blob), "UTF-8");
		
		
		html = doPost("proceedtoCheckout=", "sessionBlob", value);
		Assert.assertEquals("User should be redirect to order", "TeaStore Order", getWebSiteTitle(html));


	}

	@Override
	protected Servlet getServlet() {
		return new CartActionServlet();
	}

	

}
