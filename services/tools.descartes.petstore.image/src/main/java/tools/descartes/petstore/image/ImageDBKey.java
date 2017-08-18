package tools.descartes.petstore.image;

public class ImageDBKey {

	private final long productID;
	private final String webuiName;
	private final boolean isProductKey;
	
	public ImageDBKey(long productID) {
		this.productID = productID;
		webuiName = null;
		isProductKey = true;
	}
	
	public ImageDBKey(String webuiName) {
		this.webuiName = webuiName;
		productID = 0;
		isProductKey = false;
	}
	
	public boolean isProductKey() {
		return isProductKey;
	}
	
	public long getProductID() {
		return productID;
	}
	
	public String getWebUIName() {
		return webuiName;
	}

}
