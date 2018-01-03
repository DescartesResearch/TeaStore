package tools.descartes.petsupplystore.kieker.rabbitmq;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kieker.analysis.AnalysisController;
import kieker.analysis.IAnalysisController;
import kieker.analysis.exception.AnalysisConfigurationException;
import kieker.analysis.plugin.filter.forward.TeeFilter;
import kieker.analysis.plugin.reader.amqp.AmqpReader;
import kieker.common.configuration.Configuration;


@WebServlet("/logs")
public class DisplayLogs extends HttpServlet{
	private static final long serialVersionUID = 1L;

	private static final String URI = "amqp://admin:nimda@127.0.0.1";
	private static final String QUEUENAME = "kieker";
	/**
	 * {@inheritDoc}
	 * @throws IOException 
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


		final IAnalysisController analysisInstance = new AnalysisController();

		final Configuration logReaderConfiguration = new Configuration();
		logReaderConfiguration.setProperty(AmqpReader.CONFIG_PROPERTY_URI, URI);
		logReaderConfiguration.setProperty(AmqpReader.CONFIG_PROPERTY_QUEUENAME, QUEUENAME);

		final AmqpReader logReader = new AmqpReader(logReaderConfiguration, analysisInstance);

		// Create and register a simple output writer.
		final TeeFilter teeFilter = new TeeFilter(new Configuration(), analysisInstance);

		try {
			analysisInstance.connect(logReader, AmqpReader.OUTPUT_PORT_NAME_RECORDS, teeFilter, TeeFilter.INPUT_PORT_NAME_EVENTS);
			analysisInstance.run();
		} catch (final AnalysisConfigurationException e) {
			e.printStackTrace();
		}
	}
}
