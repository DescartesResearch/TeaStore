package tools.descartes.petsupplystore.persistence.repository;

import java.util.HashMap;

/**
 * Class for managing the testing EMF singleton.
 * Replaces the {@link EMFManager} in tests.
 * @author Jóakim von Kistowski
 *
 */
public final class EMFManagerInitializer {
	
	private static final String DRIVER_PROPERTY = "javax.persistence.jdbc.driver";
	private static final String DRIVER_VALUE = "org.hsqldb.jdbcDriver";
	private static final String JDBC_URL_PROPERTY = "javax.persistence.jdbc.url";
	private static final String JDBC_URL_VALUE = "jdbc:hsqldb:mem:test";
	private static final String USER_PROPERTY = "javax.persistence.jdbc.user";
	private static final String USER_VALUE = "sa";
	private static final String PASSWORD_PROPERTY = "javax.persistence.jdbc.password";
	private static final String PASSWORD_VALUE = "";
	private static final String DDL_PROPERTY = "eclipselink.ddl-generation";
	private static final String DDL_VALUE = "drop-and-create-tables";
	private static final String DDL_OUTPUT_PROPERTY = "eclipselink.ddl-generation.output-mode";
	private static final String DDL_OUTPUT_VALUE = "database";
	
	private EMFManagerInitializer() {
		
	}
	
	/**
	 * Initialize the testing entity manager factory.
	 */
	public static void initializeEMF() {
			HashMap<String, String> persistenceProperties = createPersistencePropertiesForTesting();
			EMFManager.configureEMFWithProperties(persistenceProperties);
	}
	
	
	private static HashMap<String, String> createPersistencePropertiesForTesting() {
		HashMap<String, String> persistenceProperties = new HashMap<String, String>();
		persistenceProperties.put(DRIVER_PROPERTY, DRIVER_VALUE);
		persistenceProperties.put(JDBC_URL_PROPERTY, JDBC_URL_VALUE);
		persistenceProperties.put(USER_PROPERTY, USER_VALUE);
		persistenceProperties.put(PASSWORD_PROPERTY, PASSWORD_VALUE);
		persistenceProperties.put(DDL_PROPERTY, DDL_VALUE);
		persistenceProperties.put(DDL_OUTPUT_PROPERTY, DDL_OUTPUT_VALUE);
		return persistenceProperties;
	}
}
