package tools.descartes.petstore.image.setup;

import java.util.Arrays;

public enum StorageMode {
	
	DRIVE("Drive"),
	DRIVE_LIMITED("Drive-Limited");
	
	public static final StorageMode STD_STORAGE_MODE = DRIVE;
	
	private final String strRepresentation;
	
	private StorageMode(String strRepresentation) {
		this.strRepresentation = strRepresentation;
	}
	
	public String getStrRepresentation() {
		return new String(strRepresentation);
	}
	
	public static StorageMode getStorageModeFromString(String strStorageMode) {
		return Arrays.asList(StorageMode.values()).stream()
				.filter(mode -> mode.strRepresentation.equals(strStorageMode))
				.findFirst()
				.orElse(STD_STORAGE_MODE);
	}
}
