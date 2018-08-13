package tools.descartes.teastore.kieker.rabbitmq;

import kieker.analysis.AnalysisController;
import kieker.analysis.IAnalysisController;
import kieker.analysis.plugin.reader.amqp.AmqpReader;
import kieker.analysis.plugin.reader.amqp.ChunkingAmqpReader;
import kieker.analysis.plugin.reader.newio.RawDataReaderPlugin;
import kieker.common.configuration.Configuration;

/**
 * daemon that retireves the logs.
 * @author Simon
 *
 */
public class LogReaderDaemon implements Runnable {

  private static final String URI = "amqp://admin:nimda@127.0.0.1";
  private static final String QUEUENAME = "kieker";

  @Override
  public void run() {
    final IAnalysisController analysisInstance = new AnalysisController();
    try {
      Configuration configuration = new Configuration();

      configuration.setProperty(RawDataReaderPlugin.CONFIG_PROPERTY_READER,
          "kieker.analysis.plugin.reader.amqp.ChunkingAmqpReader");
      configuration.setProperty(RawDataReaderPlugin.CONFIG_PROPERTY_DESERIALIZER,
          "kieker.analysis.plugin.reader.newio.deserializer.BinaryDeserializer");

      configuration.setProperty(ChunkingAmqpReader.CONFIG_PROPERTY_URI, URI);
      configuration.setProperty(ChunkingAmqpReader.CONFIG_PROPERTY_HEARTBEAT, 0);
      configuration.setProperty(ChunkingAmqpReader.CONFIG_PROPERTY_QUEUENAME, QUEUENAME);

      RawDataReaderPlugin reader = new RawDataReaderPlugin(configuration, analysisInstance);
      final LogConsumer consumer = new LogConsumer(new Configuration(), analysisInstance);

      analysisInstance.connect(reader, AmqpReader.OUTPUT_PORT_NAME_RECORDS, consumer,
          LogConsumer.INPUT_PORT_NAME);
      analysisInstance.run();
    } catch (Exception e) {
      System.out.println("AMQP Reader was interupted, probably due to reset");
    } finally {
      analysisInstance.terminate();
    }
  }

}
