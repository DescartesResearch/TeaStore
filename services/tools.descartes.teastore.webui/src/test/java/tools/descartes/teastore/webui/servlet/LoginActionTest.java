package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.webui.servlet.LoginActionServlet;

public class LoginActionTest extends AbstractUiTest {

	@Test
	public void testLoginAction() throws IOException, ServletException, InterruptedException {
		mockCategories(1);
		mockValidPostRestCall(null, "/tools.descartes.teastore.auth/rest/useractions/isloggedin");

		SessionBlob blob = new SessionBlob();
		blob.setSID("1");
		mockValidPostRestCall(blob,
				"/tools.descartes.teastore.auth/rest/useractions/login?name=user&password=password");

		String html = doPost("username=user&password=password");
		Assert.assertEquals("After sucessful login redirect home", "TeaStore Home", getWebSiteTitle(html));
		

		blob = new SessionBlob();
		mockValidPostRestCall(blob,
				"/tools.descartes.teastore.auth/rest/useractions/login?name=user&password=wrong");
		html = doPost("username=user&password=wrong");
		Assert.assertEquals("After failed login redirect login", "TeaStore Login", getWebSiteTitle(html));

	}

	@Override
	protected Servlet getServlet() {
		return new LoginActionServlet();
	}

	

}
