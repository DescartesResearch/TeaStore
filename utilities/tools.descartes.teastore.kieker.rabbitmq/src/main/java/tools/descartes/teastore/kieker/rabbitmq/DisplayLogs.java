package tools.descartes.teastore.kieker.rabbitmq;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kieker.common.record.IMonitoringRecord;

/**
 * Servlet that shows logs currently stored in memory.
 * 
 * @author Simon
 *
 */
@WebServlet("/displaylogs")
public class DisplayLogs extends HttpServlet {
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
    for (IMonitoringRecord record : MemoryLogStorage.getRecords()) {
      writer.println(record);
    }
  }
}
