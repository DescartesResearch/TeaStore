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
package tools.descartes.petsupplystore.image;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.entities.ImageSize;

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
	private final Logger log = LoggerFactory.getLogger(ImageDB.class);
	
	// Locking
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	/**
	 * Standard constructor creating a new and empty image database.
	 */
	public ImageDB() {
		
	}
	
	/**
	 * Copy constructor making a shallow copy of the given image database. If the database to copy is null, a 
	 * {@link java.lang.NullPointerException} will be thrown.
	 * @param copy Image database to copy.
	 */
	public ImageDB(ImageDB copy) {
		if (copy == null) {
			log.error("The supplied image database to copy is null.");
			throw new NullPointerException("The supplied image database to copy is null.");
		}
		
		this.products = new HashMap<>(copy.products);
		this.webui = new HashMap<>(copy.webui);
		this.sizes = new HashMap<>(copy.sizes);
	}

	/**
	 * Checks whether a given image key (product ID or name) is available in the given size. If the given image key 
	 * is null, a {@link java.lang.NullPointerException} will be thrown.
	 * @param imageKey Image key to check for
	 * @param imageSize Image size to check for
	 * @return True if the image was found in the correct size, otherwise false
	 */
	public boolean hasImageID(ImageDBKey imageKey, ImageSize imageSize) {
		if (imageKey == null) {
			log.error("The supplied image key is null.");
			throw new NullPointerException("The supplied image key is null.");
		}
		
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
	 * cannot be found or is not available in the given size, 0 will be returned. If the image key 
	 * is null, a {@link java.lang.NullPointerException} will be thrown.
	 * @param imageKey Image key to find
	 * @param imageSize Image size to find
	 * @return The image ID if the image with the size was found, otherwise 0
	 */
	public long getImageID(ImageDBKey imageKey, ImageSize imageSize) {
		if (imageKey == null) {
			log.error("The supplied image key is null.");
			throw new NullPointerException("The supplied image key is null.");
		}
		
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
		Optional<Map.Entry<Long, ImageSize>> img = null;
		lock.readLock().lock();
		try {
			img = db.getOrDefault(key, new HashMap<>()).entrySet().stream()
					.filter(t -> t.getValue().equals(imageSize))
					.findFirst();
		} finally {
			lock.readLock().unlock();
		}
		
		if (img.isPresent()) {
			return img.get().getKey();
		}
		
		return 0;
	}
	
	/**
	 * Returns the image size for a given image ID or null if it could not be found.
	 * @param imageID The image ID to get the image size for
	 * @return The image size or null if the ID could not be found
	 */
	public ImageSize getImageSize(long imageID) {
		ImageSize result = null;
		lock.readLock().lock();
		try {
			result = sizes.getOrDefault(imageID, null);
		} finally {
			lock.readLock().unlock();
		}
		return result;
	}
	
	/**
	 * Creates a new mapping between, an image key (either product ID or name), the unique image ID and the size of
	 * the image. If the image key or image size is null, a {@link java.lang.NullPointerException} will be thrown.
	 * @param imageKey The image key, either product ID or image name
	 * @param imageID The unique image ID
	 * @param imageSize The size of the image
	 */
	public void setImageMapping(ImageDBKey imageKey, long imageID, ImageSize imageSize) {
		if (imageKey == null) {
			log.error("The supplied image key is null.");
			throw new NullPointerException("The supplied image key is null.");
		}
		
		if (imageKey.isProductKey()) {
			setImageMapping(imageKey.getProductID(), imageID, imageSize);
		} else {
			setImageMapping(imageKey.getWebUIName(), imageID, imageSize);
		}
	}
	
	/**
	 * Creates a new mapping between, a product ID, the unique image ID and the size of
	 * the image. If the image size is null, a {@link java.lang.NullPointerException} will be thrown.
	 * @param productID The product ID
	 * @param imageID The unique image ID
	 * @param imageSize The size of the image
	 */	
	public void setImageMapping(long productID, long imageID, ImageSize imageSize) {
		map(productID, imageID, imageSize, products);
	}
	
	/**
	 * Creates a new mapping between, an image name, the unique image ID and the size of
	 * the image. If the image name or size is null, a {@link java.lang.NullPointerExcpetion} will be thrown.
	 * @param name The image name
	 * @param imageID The unique image ID
	 * @param imageSize The size of the image
	 */	
	public void setImageMapping(String name, long imageID, ImageSize imageSize) {
		if (name == null) {
			log.error("The supplied image name is null.");
			throw new NullPointerException("The supplied image name is null.");
		}
		
		map(name, imageID, imageSize, webui);
	}

	// Actually creates the image mapping
	private <K> void map(K key, long imageID, ImageSize imageSize, HashMap<K, Map<Long, ImageSize>> db) {
		if (imageSize == null) {
			log.error("Supplied image size is null.");
			throw new NullPointerException("Supplied image size is null.");
		}
	
		// In case the product ID or image name is not known, we create a new map to store the mapping
		Map<Long, ImageSize> images = new HashMap<>();
		
		lock.writeLock().lock();
		try {
			if (db.containsKey(key)) {
				images = db.get(key);
			}
			
			// Add the new mapping to the internal map and put it back into the correct database (map)
			images.put(imageID, imageSize);
			db.put(key, images);
			sizes.put(imageID, imageSize);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void removeImageMapping(long imageID) {
		lock.writeLock().lock();
		try {
			unmap(imageID, webui);
			unmap(imageID, products);
			sizes.remove(imageID);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	private <K> void unmap(long imageID, HashMap<K, Map<Long, ImageSize>> db) {
		Map.Entry<String, Map<Long, ImageSize>> img = webui.entrySet().stream()
				.filter(entry -> entry.getValue().containsKey(imageID))
				.findFirst().orElse(null);
		if (img != null) {
			webui.remove(img.getKey());
		}
	}

//	public static class TestRunner implements Runnable {
//
//		long id;
//		String name;
//		ImageDB db;
//		int nr;
//		
//		public TestRunner(int nr, long id, String name, ImageDB db) {
//			this.id = id;
//			this.name = name;
//			this.db = db;
//			this.nr = nr;
//		}
//		
//		@Override
//		public void run() {
//			ThreadLocalRandom tlr = ThreadLocalRandom.current();
//			int repeat = 100;
//			for (int i = 0; i < repeat; i++) {
//			ImageSize size = ImageSize.STD_IMAGE_SIZE;
//			switch(tlr.nextInt(6)) {
//			case 0: size = ImageSize.FULL; break;
//			case 1: size = ImageSize.ICON; break;
//			case 2: size = ImageSize.LOGO; break;
//			case 3: size = ImageSize.MAIN_IMAGE; break;
//			case 4: size = ImageSize.PORTRAIT; break;
//			case 5: size = ImageSize.PREVIEW; break;
//			}
//			
//			switch(tlr.nextInt(8)) {
//			case 0: System.out.println(nr + "/" + Thread.currentThread().getName() + ": hasID_ID");db.hasImageID(id, size); break;
//			case 1: System.out.println(nr + "/" + Thread.currentThread().getName() + ": hasID_NAME");db.hasImageID(name, size); break;
//			case 2: System.out.println(nr + "/" + Thread.currentThread().getName() + ": getID_ID");db.getImageID(id, size); break;
//			case 3: System.out.println(nr + "/" + Thread.currentThread().getName() + ": getID_NAME");db.getImageID(name, size); break;
//			case 4: System.out.println(nr + "/" + Thread.currentThread().getName() + ": getSize");db.getImageSize(tlr.nextLong()); break;
//			case 5: System.out.println(nr + "/" + Thread.currentThread().getName() + ": set_ID");db.setImageMapping(id, tlr.nextLong(), size); break;
//			case 6: System.out.println(nr + "/" + Thread.currentThread().getName() + ": set_NAME");db.setImageMapping(name, tlr.nextLong(), size); break;
//			case 7: System.out.println(nr + "/" + Thread.currentThread().getName() + ": remove");db.removeImageMapping(id); break;
//			}}
//		}
//	}
	
//	public static void main(String[] args) {
//		ImageDB db = new ImageDB();
//		Random r = new Random(800);
//		// Fill image db
//		int nrOfEntries = 100000;
//		for (int i = 0; i < nrOfEntries; i++) {
//			ImageSize size = ImageSize.STD_IMAGE_SIZE;
//			switch(r.nextInt(6)) {
//			case 0: size = ImageSize.FULL; break;
//			case 1: size = ImageSize.ICON; break;
//			case 2: size = ImageSize.LOGO; break;
//			case 3: size = ImageSize.MAIN_IMAGE; break;
//			case 4: size = ImageSize.PORTRAIT; break;
//			case 5: size = ImageSize.PREVIEW; break;
//			}	
//			switch(r.nextInt(2)) {
//			case 0: db.setImageMapping(r.nextLong(), r.nextLong(), size); break;
//			case 1: db.setImageMapping(Stream.generate(() -> r.nextInt(255))
//				.limit(100)
//				.map(ii -> (char)ii.intValue())
//				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), r.nextLong(), size); break;
//			}
//		}
//		System.out.println("Filled image db");
//		
//		// Create runnables
//		int runnables = 5000;
//		ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(100);
//		for (int i = 0; i < runnables; i++) {
//			TestRunner tmp = new TestRunner(i, r.nextLong(), Stream.generate(() -> r.nextInt(255))
//					.limit(100)
//					.map(ii -> (char)ii.intValue())
//					.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), db);
//			pool.schedule(tmp, r.nextInt(5000), TimeUnit.MILLISECONDS);
//		}
//		System.out.println("All threads started");
//		try {
//			System.out.println(pool.awaitTermination(60, TimeUnit.SECONDS));
//		} catch (InterruptedException e) {
//			// NOTODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(pool.shutdownNow().size());
//		System.out.println("All threads finished");
//	}
}
