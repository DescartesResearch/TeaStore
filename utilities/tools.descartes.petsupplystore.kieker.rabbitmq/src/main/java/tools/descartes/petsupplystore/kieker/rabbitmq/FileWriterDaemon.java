package tools.descartes.petsupplystore.kieker.rabbitmq;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.writer.filesystem.AsciiFileWriter;

public class FileWriterDaemon implements Runnable {

	@Override
	public void run() {
		new File("logs").mkdir();
		new File("logs").mkdirs();
		Configuration configuration = new Configuration();

		configuration.setProperty(AsciiFileWriter.CONFIG_PATH, "logs");
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXENTRIESINFILE, "-1");
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXLOGSIZE, "-1");
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXLOGFILES, "-1");
		configuration.setProperty(AsciiFileWriter.CONFIG_FLUSH, "true");

		AsciiFileWriter writer = new AsciiFileWriter(configuration);
		try {
			while (true) {
				for (IMonitoringRecord record : MemoryLogStorage.getRecords())
					writer.writeMonitoringRecord(record);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Throwable t) {
			Logger logger = Logger.getLogger("FileWriterDaemon");
			logger.setLevel(Level.INFO);
			logger.error("Error!", t);
			}
	}
}
