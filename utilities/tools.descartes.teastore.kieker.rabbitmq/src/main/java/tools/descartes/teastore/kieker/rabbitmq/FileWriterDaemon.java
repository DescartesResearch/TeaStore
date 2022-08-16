package tools.descartes.teastore.kieker.rabbitmq;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.writer.filesystem.FileWriter;

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

		configuration.setProperty(FileWriter.CONFIG_PATH, "apache-tomcat-8.5.24/webapps/logs");
		configuration.setProperty(FileWriter.CONFIG_MAXENTRIESINFILE, "-1");
		configuration.setProperty(FileWriter.CONFIG_MAXLOGSIZE, "-1");
		configuration.setProperty(FileWriter.CONFIG_MAXLOGFILES, "-1");
		configuration.setProperty(FileWriter.CONFIG_FLUSH, "true");

		FileWriter writer;
		try {
			writer = new FileWriter(configuration);
		} catch (IOException e1) {
			throw new IllegalStateException(e1.getMessage());
		}
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
