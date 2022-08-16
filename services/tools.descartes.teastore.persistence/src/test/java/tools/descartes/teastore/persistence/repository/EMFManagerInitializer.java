package tools.descartes.teastore.persistence.repository;

import java.util.HashMap;

/**
 * Class for managing the testing EMF singleton.
 * Replaces the {@link EMFManager} in tests.
 * @author Jóakim von Kistowski
 *
 */
public final class EMFManagerInitializer {
	
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
		HashMap<String, String> persistenceProperties = EMFManager.createPersistencePropertieForInMemoryDB();
		persistenceProperties.put(DDL_PROPERTY, DDL_VALUE);
		persistenceProperties.put(DDL_OUTPUT_PROPERTY, DDL_OUTPUT_VALUE);
		return persistenceProperties;
	}
}
