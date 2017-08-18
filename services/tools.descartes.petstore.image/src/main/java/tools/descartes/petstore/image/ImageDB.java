package tools.descartes.petstore.image;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import tools.descartes.petstore.entities.ImageSize;

/**
 * Image database storing the relation between image names, product IDs and image IDs as well as the available image 
 * size.
 * @author Norbert Schmitt
 */
public class ImageDB {
	
	private HashMap<Long, Map<Long, ImageSize>> products = new HashMap<>();
	private HashMap<String, Map<Long, ImageSize>> webui = new HashMap<>();
	private HashMap<Long, ImageSize> sizes = new HashMap<>();
	
	/**
	 * Standard constructor creating a new and empty image database.
	 */
	public ImageDB() {
		
	}
	
	/**
	 * Copy constructor making a shallow copy of the given image database.
	 * @param copy Image database to copy.
	 */
	public ImageDB(ImageDB copy) {
		this.products = new HashMap<>(copy.products);
		this.webui = new HashMap<>(copy.webui);
		this.sizes = new HashMap<>(copy.sizes);
	}
	
	public Map<Long, ImageSize> getAllSizes(ImageDBKey imageKey) {
		if (imageKey.isProductKey())
			return getAllSizes(imageKey.getProductID());
		return getAllSizes(imageKey.getWebUIName());
	}
	
	public Map<Long, ImageSize> getAllSizes(long productID) {
		return products.getOrDefault(productID, new HashMap<>());
	}
	
	public Map<Long, ImageSize> getAllSizes(String name) {
		return webui.getOrDefault(name, new HashMap<>());
	}
	
	public boolean hasImageID(ImageDBKey imageKey, ImageSize imageSize) {
		if (imageKey.isProductKey())
			return hasImageID(imageKey.getProductID(), imageSize);
		return hasImageID(imageKey.getWebUIName(), imageSize);
	}
	
	public boolean hasImageID(long productID, ImageSize imageSize) {
		return findImageID(productID, imageSize, products) != 0;
	}
	
	public boolean hasImageID(String name, ImageSize imageSize) {
		return findImageID(name, imageSize, webui) != 0;
	}
	
	public long getImageID(ImageDBKey imageKey, ImageSize imageSize) {
		if (imageKey.isProductKey())
			return getImageID(imageKey.getProductID(), imageSize);
		return getImageID(imageKey.getWebUIName(), imageSize);
	}
	
	public long getImageID(long productID, ImageSize imageSize) {
		return findImageID(productID, imageSize, products);
	}
	
	public long getImageID(String name, ImageSize imageSize) {
		return findImageID(name, imageSize, webui);
	}
	
	private <K> long findImageID(K key, ImageSize imageSize, HashMap<K, Map<Long, ImageSize>> db) {
		Optional<Map.Entry<Long, ImageSize>> img = db.getOrDefault(key, new HashMap<>()).entrySet().stream()
				.filter(t -> t.getValue().equals(imageSize))
				.findFirst();
		if (img.isPresent())
			return img.get().getKey();
		return 0;
	}
	
	public ImageSize getImageSize(long imageID) {
		return sizes.getOrDefault(imageID, null);
	}
	
	public void setImageMapping(ImageDBKey imageKey, long imageID, ImageSize imageSize) {
		if (imageKey.isProductKey()) {
			setImageMapping(imageKey.getProductID(), imageID, imageSize);
		} else {
			setImageMapping(imageKey.getWebUIName(), imageID, imageSize);
		}
	}
	
	public void setImageMapping(long productID, long imageID, ImageSize imageSize) {
		map(productID, imageID, imageSize, products);
	}
	
	public void setImageMapping(String name, long imageID, ImageSize imageSize) {
		map(name, imageID, imageSize, webui);
	}
	
	private <K> void map(K key, long imageID, ImageSize imageSize, HashMap<K, Map<Long, ImageSize>> db) {
		if (imageSize == null)
			throw new NullPointerException("Image size is null.");
		
		Map<Long, ImageSize> images = new HashMap<>();
		if (db.containsKey(key))
			images = db.get(key);
		
		images.put(imageID, imageSize);
		db.put(key, images);
		sizes.put(imageID, imageSize);
	}
	
	public void removeWebImages() {
		removeImagesFromSizeMap(webui);
		webui = new HashMap<>();
	}
	
	public void removeProductImages() {
		removeImagesFromSizeMap(products);
		products = new HashMap<>();
	}
	
	private <K> void removeImagesFromSizeMap(Map<K, Map<Long, ImageSize>> db) {
		db.entrySet().forEach(entry -> entry.getValue().entrySet().forEach(size -> sizes.remove(size.getKey())));
	}
	
	public List<Long> getAllWebImageIDs() {
		return getAllImageIDs(webui, null);
	}
	
	public List<Long> getAllWebImageIDs(ImageSize imageSize) {
		return getAllImageIDs(webui, imageSize);
	}
	
	public List<Long> getAllProductImageIDs() {
		return getAllImageIDs(products, null);
	}
	
	public List<Long> getAllProductImageIDs(ImageSize imageSize) {
		return getAllImageIDs(products, imageSize);
	}
	
	private <K> List<Long> getAllImageIDs(Map<K, Map<Long, ImageSize>> db, ImageSize imageSize) {
		return db.entrySet().stream()
				.map(entry -> entry.getValue().entrySet().stream()
						.filter(size -> imageSize == null || size.getValue().equals(imageSize))
						.map(size -> size.getKey())
						.findFirst()
						.orElse(null))
				.filter(entry -> entry != null)
				.collect(Collectors.toList());
	}

}
