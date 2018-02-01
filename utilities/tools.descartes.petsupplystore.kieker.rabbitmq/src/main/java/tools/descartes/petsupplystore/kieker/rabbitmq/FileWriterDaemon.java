package tools.descartes.petsupplystore.kieker.rabbitmq;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.writer.filesystem.AsciiFileWriter;

public class FileWriterDaemon implements Runnable {

	@Override
	public void run() {
		Logger logger = Logger.getLogger("FileWriterDaemon");
		logger.setLevel(Level.INFO);
		Set<String> knownMonitoringTypes = new HashSet<String>();
		int id = 0;
		new File("apache-tomcat-8.5.24/kieker").mkdir();
		new File("apache-tomcat-8.5.24/kieker").mkdirs();
		Configuration configuration = new Configuration();

		configuration.setProperty(AsciiFileWriter.CONFIG_PATH, "apache-tomcat-8.5.24/kieker");
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXENTRIESINFILE, "-1");
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXLOGSIZE, "-1");
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXLOGFILES, "-1");
		configuration.setProperty(AsciiFileWriter.CONFIG_FLUSH, "true");

		AsciiFileWriter writer = new AsciiFileWriter(configuration);
		try {
			while (true) {
				for (IMonitoringRecord record : MemoryLogStorage.getRecords()) {
					if (!knownMonitoringTypes.contains(record.getClass().getName())) {
						logger.info("NEW RecordType!");
						knownMonitoringTypes.add(record.getClass().getName());
						writer.onNewRegistryEntry(record.getClass().getName(), id);
						id++;
					}
					writer.writeMonitoringRecord(record);
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Throwable t) {
			logger.error("Error!", t);
		}
	}
}
