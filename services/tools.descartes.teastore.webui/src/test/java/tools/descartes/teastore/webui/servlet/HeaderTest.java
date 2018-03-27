package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.webui.servlet.IndexServlet;

public class HeaderTest extends AbstractUiTest {

	@Test
	public void testHeaderLogging() throws IOException, ServletException, InterruptedException {
		mockCategories(1);
		mockValidPostRestCall(null, "/tools.descartes.teastore.auth/rest/useractions/isloggedin");
		String html = doGet();
		
		Assert.assertEquals("Test if header shows correct illustration: no Profile", 0,
				countString("glyphicon-user", html));
		Assert.assertEquals("Test if header shows correct illustration: Login", 1, countString("Sign", html));
		//
		
		SessionBlob blob = new SessionBlob();
		blob.setSID("1");
		mockValidPostRestCall(blob, "/tools.descartes.teastore.auth/rest/useractions/isloggedin");
		html = doGet();
		Assert.assertEquals("Test if header shows correct illustration: Profile", 1,
				countString("glyphicon-user", html));
		Assert.assertEquals("Test if header shows correct illustration: Log out", 1, countString("Logout", html));
		
	}

	@Override
	protected Servlet getServlet() {
		return new IndexServlet();
	}

}
