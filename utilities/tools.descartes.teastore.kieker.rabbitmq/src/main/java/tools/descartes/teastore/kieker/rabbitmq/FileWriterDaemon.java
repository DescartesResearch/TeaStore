package tools.descartes.teastore.kieker.rabbitmq;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.writer.filesystem.AsciiFileWriter;

/**
 * Daemon that writes the logs to HDD.
 * @author Simon
 *
 */
public class FileWriterDaemon implements Runnable {

	@Override
	public void run() {
		Logger logger = Logger.getLogger("FileWriterDaemon");
		logger.setLevel(Level.INFO);
		new File("apache-tomcat-8.5.24/webapps/logs").mkdir();
		new File("apache-tomcat-8.5.24/webapps/logs").mkdirs();
		Configuration configuration = new Configuration();

		configuration.setProperty(AsciiFileWriter.CONFIG_PATH, "apache-tomcat-8.5.24/webapps/logs");
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXENTRIESINFILE, "-1");
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXLOGSIZE, "-1");
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXLOGFILES, "-1");
		configuration.setProperty(AsciiFileWriter.CONFIG_FLUSH, "true");
		configuration.setProperty(AsciiFileWriter.CONFIG_FLUSH_MAPFILE, "true");

		AsciiFileWriter writer = new AsciiFileWriter(configuration);
		while (true) {
			for (IMonitoringRecord record : MemoryLogStorage.getRecords()) {
				writer.writeMonitoringRecord(record);
			}
			MemoryLogStorage.clearMemoryStorage();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
