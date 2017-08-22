/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.petstore.image;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petstore.entities.ImageSize;

/**
 * Image database storing the relation between image names, product IDs and image IDs as well as the available image 
 * size.
 * @author Norbert Schmitt
 */
public class ImageDB {

	// Internal storage container to allow mapping product IDs and image names (for non-generated images) to images 
	// with different sizes
	private HashMap<Long, Map<Long, ImageSize>> products = new HashMap<>();
	private HashMap<String, Map<Long, ImageSize>> webui = new HashMap<>();
	private HashMap<Long, ImageSize> sizes = new HashMap<>();
	private Logger log = LoggerFactory.getLogger(ImageDB.class);
	
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
	
	/**
	 * Returns the mapping of image IDs to their size for a given image key (product ID or name).
	 * @param imageKey The image key identifying a range of sizes for a image content
	 * @return Mapping between image IDs and the corresponding size
	 */
	public Map<Long, ImageSize> getAllSizes(ImageDBKey imageKey) {
		if (imageKey.isProductKey()) {
			return getAllSizes(imageKey.getProductID());
		}
		return getAllSizes(imageKey.getWebUIName());
	}
	
	/**
	 * Returns the mapping of image IDs to their size for a given product ID.
	 * @param productID The product ID identifying a range of sizes for a image content
	 * @return Mapping between image IDs and the corresponding size
	 */
	public Map<Long, ImageSize> getAllSizes(long productID) {
		return products.getOrDefault(productID, new HashMap<>());
	}
	
	/**
	 * Returns the mapping of image IDs to their size for a given image name.
	 * @param name The image name identifying a range of sizes for a image content
	 * @return Mapping between image IDs and the corresponding size
	 */
	public Map<Long, ImageSize> getAllSizes(String name) {
		return webui.getOrDefault(name, new HashMap<>());
	}
	
	/**
	 * Checks whether a given image key (product ID or name) is available in the given size.
	 * @param imageKey Image key to check for
	 * @param imageSize Image size to check for
	 * @return True if the image was found in the correct size, otherwise false
	 */
	public boolean hasImageID(ImageDBKey imageKey, ImageSize imageSize) {
		if (imageKey.isProductKey()) {
			return hasImageID(imageKey.getProductID(), imageSize);
		}
		return hasImageID(imageKey.getWebUIName(), imageSize);
	}
	
	/**
	 * Checks whether a given product ID is available in the given size.
	 * @param productID Product ID to check for
	 * @param imageSize Image size to check for
	 * @return True if the image was found in the correct size, otherwise false
	 */
	public boolean hasImageID(long productID, ImageSize imageSize) {
		return findImageID(productID, imageSize, products) != 0;
	}
	
	/**
	 * Checks whether a given image name is available in the given size.
	 * @param name Image name to check for
	 * @param imageSize Image size to check for
	 * @return True if the image was found in the correct size, otherwise false
	 */
	public boolean hasImageID(String name, ImageSize imageSize) {
		return findImageID(name, imageSize, webui) != 0;
	}
	
	/**
	 * Finds and returns the image ID for the given image key (product ID or name) and size. If the image key 
	 * cannot be found or is not available in the given size, 0 will be returned.
	 * @param imageKey Image key to find
	 * @param imageSize Image size to find
	 * @return The image ID if the image with the size was found, otherwise 0
	 */
	public long getImageID(ImageDBKey imageKey, ImageSize imageSize) {
		if (imageKey.isProductKey()) {
			return getImageID(imageKey.getProductID(), imageSize);
		}
		return getImageID(imageKey.getWebUIName(), imageSize);
	}
	
	/**
	 * Finds and returns the image ID for the given product ID and size. If the product ID
	 * cannot be found or is not available in the given size, 0 will be returned.
	 * @param productID Product ID to find
	 * @param imageSize Image size to find
	 * @return The image ID if the image with the size was found, otherwise 0
	 */
	public long getImageID(long productID, ImageSize imageSize) {
		return findImageID(productID, imageSize, products);
	}
	
	/**
	 * Finds and returns the image ID for the given image name and size. If the name
	 * cannot be found or is not available in the given size, 0 will be returned.
	 * @param name Image name to find
	 * @param imageSize Image size to find
	 * @return The image ID if the image with the size was found, otherwise 0
	 */	
	public long getImageID(String name, ImageSize imageSize) {
		return findImageID(name, imageSize, webui);
	}
	
	// Does actually all the heavy lifting for the getImageID methods
	private <K> long findImageID(K key, ImageSize imageSize, HashMap<K, Map<Long, ImageSize>> db) {
		Optional<Map.Entry<Long, ImageSize>> img = db.getOrDefault(key, new HashMap<>()).entrySet().stream()
				.filter(t -> t.getValue().equals(imageSize))
				.findFirst();
		if (img.isPresent()) {
			return img.get().getKey();
		}
		return 0;
	}
	
	/**
	 * Returns the image size for a given image ID.
	 * @param imageID The image ID to get the image size for
	 * @return The image size or null if the ID could not be found
	 */
	public ImageSize getImageSize(long imageID) {
		return sizes.getOrDefault(imageID, null);
	}
	
	/**
	 * 
	 * @param imageKey
	 * @param imageID
	 * @param imageSize
	 */
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
		if (imageSize == null) {
			log.error("Supplied image size is null.");
			throw new NullPointerException("Supplied image size is null.");
		}
		
		Map<Long, ImageSize> images = new HashMap<>();
		if (db.containsKey(key)) {
			images = db.get(key);
		}
		
		images.put(imageID, imageSize);
		db.put(key, images);
		sizes.put(imageID, imageSize);
	}

}
