package tools.descartes.petstore.image.setup;

import java.util.Arrays;

public enum CachingRule {

	ALL("All");
	
	public static final CachingRule STD_CACHING_RULE = ALL;
	
	private final String strRepresentation;
	
	private CachingRule(String strRepresentation) {
		this.strRepresentation = strRepresentation;
	}
	
	public String getStrRepresentation() {
		return new String(strRepresentation);
	}
	
	public static CachingRule getCachingRuleFromString(String strCachingRule) {
		return Arrays.asList(CachingRule.values()).stream()
				.filter(mode -> mode.strRepresentation.equals(strCachingRule))
				.findFirst()
				.orElse(STD_CACHING_RULE);
	}
}
