package tools.descartes.petsupplystore.webui.servlet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import tools.descartes.petsupplystore.entities.message.SessionBlob;

public class LoginActionTest extends AbstractUiTest {

	@Test
	public void TestHeaderLogging() throws IOException, ServletException, InterruptedException {
		mockCategories(1);
		mockValidPostRestCall(null, "/tools.descartes.petsupplystore.store/rest/useractions/isloggedin");

		SessionBlob blob = new SessionBlob();
		blob.setSID("1");
		mockValidPostRestCall(blob,
				"/tools.descartes.petsupplystore.store/rest/useractions/login?name=user&password=password");

		String html = post("username=user&password=password");
		Assert.assertEquals("After sucessful login redirect home", "Pet Supply Store Home", getWebSiteTitle(html));
		

		blob = new SessionBlob();
		mockValidPostRestCall(blob,
				"/tools.descartes.petsupplystore.store/rest/useractions/login?name=user&password=wrong");
		html = post("username=user&password=wrong");
		Assert.assertEquals("After failed login redirect login", "Pet Supply Store Login", getWebSiteTitle(html));

	}

	@Override
	protected Servlet getServlet() {
		return new LoginActionServlet();
	}

	public String post(String query) throws IOException {
		URL url = new URL("http://localhost:3000/test/test");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		connection.setRequestProperty("Content-Length", "" + Integer.toString(query.getBytes().length));
		connection.setRequestProperty("Content-Language", "en-US");

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		// Send request
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(query);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine + "\n");
		}
		in.close();
		return response.toString();
	}

}
