package tools.descartes.petsupplystore.webui.servlet;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.Servlet;

import org.junit.Test;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.entities.message.SessionBlob;

public class IndexTest extends AbstractUiTest {
	
	@Test
	public void test() {
		List<Category> categories = new LinkedList<Category>();
		mockValidGetRestCall(categories, "/tools.descartes.petsupplystore.store/rest/categories");
		mockValidPostRestCall(new SessionBlob(), "/tools.descartes.petsupplystore.store/rest/useractions/isloggedin");
		System.out.println(getResultingHTML());
	}
	
	@Override
	protected Servlet getServlet() {
		return new IndexServlet();
	}

}
