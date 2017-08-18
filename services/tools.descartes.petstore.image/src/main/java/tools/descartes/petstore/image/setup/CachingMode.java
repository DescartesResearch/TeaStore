package tools.descartes.petstore.image.setup;

import java.util.Arrays;

public enum CachingMode {

	FIFO("FIFO"),
	LIFO("LIFO"),
	RR("RR"),
	LFU("LFU"),
	LRU("LRU"),
	MRU("MRU"),
	NONE("Disabled");
	
	public static final CachingMode STD_CACHING_MODE = LFU;
	
	private final String strRepresentation;
	
	private CachingMode(String strRepresentation) {
		this.strRepresentation = strRepresentation;
	}
	
	public String getStrRepresentation() {
		return new String(strRepresentation);
	}
	
	public static CachingMode getCachingModeFromString(String strCachingMode) {
		return Arrays.asList(CachingMode.values()).stream()
				.filter(mode -> mode.strRepresentation.equals(strCachingMode))
				.findFirst()
				.orElse(STD_CACHING_MODE);
	}
}
