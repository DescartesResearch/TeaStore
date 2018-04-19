package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.webui.servlet.ProductServlet;

public class ProductServletTest extends AbstractUiTest {

	@Test
	public void testCartAction() throws IOException, ServletException, InterruptedException {
		mockCategories(1);
		ArrayList<Long> ads = new ArrayList<Long>();
		mockValidPostRestCall(ads, "/tools.descartes.teastore.recommender/rest/recommend");
		mockValidPostRestCall(null, "/tools.descartes.teastore.auth/rest/useractions/isloggedin");
		mockValidPostRestCall(new HashMap<Long, String>(),
				"/tools.descartes.teastore.image/rest/image/getProductImages");
		mockValidGetRestCall(new Product(),
				"/tools.descartes.teastore.persistence/rest/products/1"); 
		

		String html = doGet();
		
		Assert.assertEquals("No product id should redirect to home", "TeaStore Home",
				getWebSiteTitle(html));
		
		html = doGet("?id=1");
		// -1 as we have the normal product
		Assert.assertEquals("There should be " + 0 + " ads", 0, countString("productid", html)-1);
		Assert.assertEquals("There should be no advertisment", 0, countString("advertismenttitle", html));
		
		List<Integer> numberads = Arrays.asList(0, 1, 3, 5, 20);
		for(int ad : numberads) {
			ads = new ArrayList<Long>();
			for(int i = 0; i < ad; i++) {
				ads.add((long) i);
				mockValidGetRestCall(new Product(), "/tools.descartes.teastore.persistence/rest/products/" + i);
			}
			mockValidPostRestCall(ads, "/tools.descartes.teastore.recommender/rest/recommend");
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
