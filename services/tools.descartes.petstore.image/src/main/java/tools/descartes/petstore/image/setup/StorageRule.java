package tools.descartes.petstore.image.setup;

import java.util.Arrays;

public enum StorageRule {

	ALL("All"),
	FULL_SIZE_IMG("Full-size-images");
	
	public static final StorageRule STD_STORAGE_RULE = ALL;
	
	private final String strRepresentation;
	
	private StorageRule(String strRepresentation) {
		this.strRepresentation = strRepresentation;
	}
	
	public String getStrRepresentation() {
		return new String(strRepresentation);
	}
	
	public static StorageRule getStorageRuleFromString(String strStorageRule) {
		return Arrays.asList(StorageRule.values()).stream()
				.filter(mode -> mode.strRepresentation.equals(strStorageRule))
				.findFirst()
				.orElse(STD_STORAGE_RULE);
	}
}
