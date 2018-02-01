package tools.descartes.petsupplystore.kieker.rabbitmq;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kieker.common.record.IMonitoringRecord;

@WebServlet("/logs")
public class DisplayLogs extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		response.setCharacterEncoding("utf8");
		for (IMonitoringRecord record: MemoryLogStorage.getRecords())
			writer.println(record);
	}
}
