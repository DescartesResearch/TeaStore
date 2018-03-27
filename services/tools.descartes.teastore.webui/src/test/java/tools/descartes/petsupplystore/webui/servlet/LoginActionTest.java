package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import tools.descartes.petsupplystore.entities.message.SessionBlob;

public class LoginActionTest extends AbstractUiTest {

	@Test
	public void testLoginAction() throws IOException, ServletException, InterruptedException {
		mockCategories(1);
		mockValidPostRestCall(null, "/tools.descartes.petsupplystore.auth/rest/useractions/isloggedin");

		SessionBlob blob = new SessionBlob();
		blob.setSID("1");
		mockValidPostRestCall(blob,
				"/tools.descartes.petsupplystore.auth/rest/useractions/login?name=user&password=password");

		String html = doPost("username=user&password=password");
		Assert.assertEquals("After sucessful login redirect home", "Pet Supply Store Home", getWebSiteTitle(html));
		

		blob = new SessionBlob();
		mockValidPostRestCall(blob,
				"/tools.descartes.petsupplystore.auth/rest/useractions/login?name=user&password=wrong");
		html = doPost("username=user&password=wrong");
		Assert.assertEquals("After failed login redirect login", "Pet Supply Store Login", getWebSiteTitle(html));

	}

	@Override
	protected Servlet getServlet() {
		return new LoginActionServlet();
	}

	

}
