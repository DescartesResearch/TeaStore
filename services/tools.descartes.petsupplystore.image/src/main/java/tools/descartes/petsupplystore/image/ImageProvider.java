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

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.image.setup.ImageIDFactory;
import tools.descartes.petsupplystore.image.storage.IDataStorage;

public enum ImageProvider {

	IP;
	
	public static final String IMAGE_NOT_FOUND = "notFound";
	
	private ImageDB db;
	private IDataStorage<StoreImage> storage;
	private Logger log = LoggerFactory.getLogger(ImageProvider.class);
	
	private ImageProvider() {

	}
	
	public void setImageDB(ImageDB imgDB) {
		db = imgDB;
	}
	
	public void setStorage(IDataStorage<StoreImage> imgStorage) {
		storage = imgStorage;
	}

	public Map<Long, String> getProductImages(Map<Long, ImageSize> images) {
		Map<Long, String> result = new HashMap<>();
		for (Map.Entry<Long, ImageSize> entry : images.entrySet()) {
			String imgStr = getImageFor(new ImageDBKey(entry.getKey()), entry.getValue());
			if (imgStr == null) {
				continue;
			}
			result.put(entry.getKey(), imgStr);
		}
		return result;
	}
	
	public Map<String, String> getWebUIImages(Map<String, ImageSize> images) {
		Map<String, String> result = new HashMap<>();
		for (Map.Entry<String, ImageSize> entry : images.entrySet()) {
			String imgStr = getImageFor(new ImageDBKey(entry.getKey()), entry.getValue());
			if (imgStr == null) {
				continue;
			}
			result.put(entry.getKey(), imgStr);
		}
		return result;
	}
	
	private StoreImage scaleAndRegisterImg(BufferedImage image, ImageDBKey key, ImageSize size) {
		StoreImage storedImg = new StoreImage(ImageIDFactory.ID.getNextImageID(), 
				ImageScaler.scale(image, size), size);
		db.setImageMapping(key, storedImg.getId(), size);
		storage.saveData(storedImg);
		return storedImg;
	}
	
	private String getImageFor(ImageDBKey key, ImageSize size) {
		if (db == null || storage == null) {
			log.warn("Image provider not correctly initialized. Missing image database and storage.");
			return null;
		}
		if (key == null || size == null) {
			log.info("Supplied image key or size are null.");
			return null;
		}
		if (!key.isProductKey() && (key.getWebUIName() == null || key.getWebUIName().isEmpty())) {
			log.info("Supplied image key invalid. Is neither web image nor product image.");
			return null;
		}
	
		ImageSize biggest = ImageSize.getBiggestSize();
		StoreImage storedImg = null;
		
		// Try to retrieve image from disk or from cache
		long imgID = db.getImageID(key, size);
		if (imgID != 0) {
			storedImg = storage.loadData(db.getImageID(key, size));
		}
		
		// If we dont have the image in the right size, get the biggest one and scale it
		if (storedImg == null) {
			storedImg = storage.loadData(db.getImageID(key, biggest));
			if (storedImg != null) {
				storedImg = scaleAndRegisterImg(storedImg.getImage(), key, size);
			} else {
				storedImg = storage.loadData(db.getImageID(IMAGE_NOT_FOUND, size));
				if (storedImg == null) {
					storedImg = storage.loadData(db.getImageID(IMAGE_NOT_FOUND, biggest));
					if (storedImg == null) {
						return null;
					}
					storedImg = scaleAndRegisterImg(storedImg.getImage(), new ImageDBKey(IMAGE_NOT_FOUND), size);
				}
			}
		}
		
		return storedImg.toString();
	}
	
}
