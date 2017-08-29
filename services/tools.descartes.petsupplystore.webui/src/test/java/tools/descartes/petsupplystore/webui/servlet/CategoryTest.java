package tools.descartes.petsupplystore.webui.servlet;

import javax.servlet.Servlet;
import org.junit.Assert;
import org.junit.Test;

import tools.descartes.petsupplystore.entities.message.SessionBlob;

public class CategoryTest extends AbstractUiTest {

	@Test
	public void testCategories() {
		mockCategories(3);
		mockValidPostRestCall(new SessionBlob(), "/tools.descartes.petsupplystore.store/rest/useractions/isloggedin");
		String html = doGet();
		Assert.assertEquals("Test the number of shown categories", 3, countString("Category ", html));
		mockCategories(0);
		html = doGet();
		Assert.assertEquals("Test the number of shown categories", 0, countString("Category ", html));
		
	}
	
	

	@Override
	protected Servlet getServlet() {
		return new IndexServlet();
	}

}
