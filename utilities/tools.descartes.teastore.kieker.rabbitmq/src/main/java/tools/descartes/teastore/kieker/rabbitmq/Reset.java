package tools.descartes.teastore.kieker.rabbitmq;

import java.io.File;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to reset the logs.
 * @author Simon
 *
 */
@WebServlet("/reset")
public class Reset extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogReaderStartup.stopFileWriter();

		MemoryLogStorage.clearMemoryStorage();
		deleteFolder(new File("apache-tomcat-8.5.24/webapps/logs"), "kieker");

		LogReaderStartup.startFileWriter();
	}

	private void deleteFolder(File folder, String prefix) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.getPath().contains(prefix)) {
					if (f.isDirectory()) {
						deleteFolder(f);
					} else {
						f.delete();
					}
				}
			}
		}
	}

	private void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}
}
