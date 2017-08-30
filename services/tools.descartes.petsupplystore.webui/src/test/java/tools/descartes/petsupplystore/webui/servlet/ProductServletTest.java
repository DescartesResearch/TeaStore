package tools.descartes.petsupplystore.webui.servlet;

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

import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.entities.message.SessionBlob;

public class ProductServletTest extends AbstractUiTest {

	@Test
	public void testCartAction() throws IOException, ServletException, InterruptedException {
		mockCategories(1);
		ArrayList<Product> ads = new ArrayList<Product>();
		mockValidPostRestCall(ads, "/tools.descartes.petsupplystore.store/rest/products/ads?pid=0");
		mockValidPostRestCall(null, "/tools.descartes.petsupplystore.store/rest/useractions/isloggedin");
		mockValidPostRestCall(new HashMap<Long, String>(),
				"/tools.descartes.petsupplystore.image/rest/image/getProductImages");
		mockValidGetRestCall(new Product(),
				"/tools.descartes.petsupplystore.store/rest/products/1"); 
		

		String html = doGet();
		
		Assert.assertEquals("No product id should redirect to home", "Pet Supply Store Home",
				getWebSiteTitle(html));
		
		html = doGet("?id=1");
		// -1 as we have the normal product
		Assert.assertEquals("There should be " + 0 + " ads", 0, countString("productid", html)-1);
		Assert.assertEquals("There should be no advertisment", 0, countString("advertismenttitle", html));
		
		List<Integer> numberads = Arrays.asList(0, 1, 3, 5, 20);
		for(int ad : numberads) {
			ads = new ArrayList<Product>();
			for(int i = 0; i < ad; i++) {
				ads.add(new Product());
			}
			mockValidPostRestCall(ads, "/tools.descartes.petsupplystore.store/rest/products/ads?pid=0");
			html = doGet("?id=1");
			// the maximum of shown ads is 3
			Assert.assertEquals("There should be " + ad + " ads", Math.min(ad,3), countString("productid", html)-1);
		}
		
		

	}

	@Override
	protected Servlet getServlet() {
		return new ProductServlet();
	}

}
