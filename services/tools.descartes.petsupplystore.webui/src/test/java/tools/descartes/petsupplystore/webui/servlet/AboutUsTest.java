package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Test;

import tools.descartes.petsupplystore.entities.message.SessionBlob;

public class AboutUsTest extends AbstractUiTest {

	
	@Test
	public void servesWindSpeedBasedOnForecastIoResponse() throws IOException, ServletException, InterruptedException {
		mockValidPostRestCall(new SessionBlob(), "/tools.descartes.petsupplystore.store/rest/useractions/isloggedin");
		
		System.out.println(getResultingHTML());
	}
	
	@Override
	protected Servlet getServlet() {
		return new AboutUsServlet();
	}

}
