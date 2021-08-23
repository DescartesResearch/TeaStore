package tools.descartes.teastore.kieker.rabbitmq;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that shows logs currently stored in memory.
 * 
 * @author Simon
 *
 */
@WebServlet("/index")
public class IndexServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  /**
   * {@inheritDoc}
   * 
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    PrintWriter writer = response.getWriter();
    response.setCharacterEncoding("utf8");
    writer.println("<h1>Kieker logs</h1>");
    writer.println("<ul>");
    writer.println("<li><a href=\"/logs/\">Get log files</a></li>");
    writer.println("<li><a href=\"/logs/displaylogs\">Display logs currently stored in memory</a></li>");
    writer.println("<li><a href=\"/logs/reset\">Reset logs</a></li>");
    writer.println("</ul>");
  }
}
