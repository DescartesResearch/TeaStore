package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Test;

import tools.descartes.petsupplystore.entities.message.SessionBlob;

public class AboutUsTest extends AbstractUiTest {

	
	
	
	@Override
	protected Servlet getServlet() {
		return new AboutUsServlet();
	}

}
