package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.webui.servlet.CategoryServlet;

public class PaginationTest extends AbstractUiTest {

	@Test
	public void testPagination() throws IOException, ServletException, InterruptedException {
		mockCategories(1);
		mockValidPostRestCall(null, "/tools.descartes.teastore.auth/rest/useractions/isloggedin");
		mockValidGetRestCall(new Category(), "/tools.descartes.teastore.persistence/rest/categories/0");
		mockValidGetRestCall(100, "/tools.descartes.teastore.persistence/rest/products/count/0");
		List<Product> products = new LinkedList<Product>();
		mockValidPostRestCall(new HashMap<Long, String>(),
				"/tools.descartes.teastore.image/rest/image/getProductImages");
		mockValidGetRestCall(products,
				"/tools.descartes.teastore.persistence/rest/products/category/0?start=0&max=20");
		mockValidGetRestCall(products,
				"/tools.descartes.teastore.persistence/rest/products/category/0?start=20&max=20");
		mockValidGetRestCall(products,
				"/tools.descartes.teastore.persistence/rest/products/category/0?start=40&max=20");
		mockValidGetRestCall(products,
				"/tools.descartes.teastore.persistence/rest/products/category/0?start=80&max=20");

		String html = doGet("?category=0&page=1");

		String[] pagination = getPagination(html);

		Assert.assertArrayEquals("Test the pagination on page 1: 100 products and 20 per page",
				new String[] { "1", "2", "3", "4", "5", "next" }, pagination);

		html = doGet("?category=0&page=3");

		pagination = getPagination(html);

		Assert.assertArrayEquals("Test the paginationon page 3: 100 products and 20 per page",
				new String[] { "previous", "1", "2", "3", "4", "5", "next" }, pagination);

		html = doGet("?category=0&page=5");

		pagination = getPagination(html);

		Assert.assertArrayEquals("Test the paginationon page 5: 100 products and 20 per page",
				new String[] { "previous", "1", "2", "3", "4", "5" }, pagination);

		html = doGet("?category=0&page=6");
		
		pagination = getPagination(html);
		
		Assert.assertArrayEquals("Page 6 should redirect to page 1 by 100 products and 20 per page",
				new String[] { "1", "2", "3", "4", "5", "next" }, pagination);

	}

	

	@Override
	protected Servlet getServlet() {
		return new CategoryServlet();
	}

}
