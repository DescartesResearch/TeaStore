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

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.image.setup.ImageCreatorRunner;
import tools.descartes.petstore.image.setup.ImageIDFactory;
import tools.descartes.petstore.image.storage.IDataStorage;

public class ImageProvider {
	
	public static final String IMAGE_NOT_FOUND = "notFound";
	
	private static ImageProvider instance = new ImageProvider();
	private ImageDB db;
	private IDataStorage<StoreImage> storage;
	private ImageCreatorRunner imgCreatorRunner;
	
	private ImageProvider() {

	}
	
	public static ImageProvider getInstance() {
		return instance;
	}
	
	public void setImageDB(ImageDB imgDB) {
		db = imgDB;
	}
	
	public void setStorage(IDataStorage<StoreImage> imgStorage) {
		storage = imgStorage;
	}
	
	public void setImageCreatorRunner(ImageCreatorRunner runner) {
		imgCreatorRunner = runner;
	}

	private void waitForImageCreator() {
		if (imgCreatorRunner.isRunning()) {
			imgCreatorRunner.pause();
			try {
				Thread.sleep(imgCreatorRunner.getAvgCreationTime());
			} catch (InterruptedException e) {
				
			}
		}
	}

	public Map<Long, String> getProductImages(Map<Long, ImageSize> images) {
		waitForImageCreator();
		
		Map<Long, String> result = new HashMap<>();
		for (Map.Entry<Long, ImageSize> entry : images.entrySet()) {
			String imgStr = getImageFor(new ImageDBKey(entry.getKey()), entry.getValue());
			if (imgStr == null) {
				continue;
			}
			result.put(entry.getKey(), imgStr);
		}
	
		imgCreatorRunner.resume();
		return result;
	}
	
	public Map<String, String> getWebUIImages(Map<String, ImageSize> images) {
		waitForImageCreator();
		
		Map<String, String> result = new HashMap<>();
		for (Map.Entry<String, ImageSize> entry : images.entrySet()) {
			String imgStr = getImageFor(new ImageDBKey(entry.getKey()), entry.getValue());
			if (imgStr == null) {
				continue;
			}
			result.put(entry.getKey(), imgStr);
		}
		
		imgCreatorRunner.resume();
		return result;
	}
	
	private StoreImage scaleAndRegisterImg(BufferedImage image, ImageDBKey key, ImageSize size) {
		StoreImage storedImg = new StoreImage(ImageIDFactory.getInstance().getNextImageID(), 
				ImageScaler.scale(image, size), size);
		db.setImageMapping(key, storedImg.getId(), size);
		storage.saveData(storedImg);
		return storedImg;
	}
	
	private String getImageFor(ImageDBKey key, ImageSize size) {
		if (db == null || storage == null)
			return null;
		if (key == null || size == null)
			return null;
		if (!key.isProductKey() && (key.getWebUIName() == null || key.getWebUIName().isEmpty()))
			return null;
		
		// Try to retrieve image from disk or from cache
		long storedImgID = db.getImageID(key, size);
		StoreImage storedImg = storage.loadData(storedImgID);
		
		// If we dont have the image in the right size, get the biggest one and scale it
		if (storedImg == null) {
			storedImg = storage.loadData(db.getImageID(key, ImageSize.getBiggestSize()));
			if (storedImg != null) {
				storedImg = scaleAndRegisterImg(storedImg.getImage(), key, size);
			} else {
				storedImg = storage.loadData(db.getImageID(IMAGE_NOT_FOUND, size));
				if (storedImg == null) {
					storedImg = storage.loadData(db.getImageID(IMAGE_NOT_FOUND, ImageSize.getBiggestSize()));
					if (storedImg == null)
						return null;
					storedImg = scaleAndRegisterImg(storedImg.getImage(), new ImageDBKey(IMAGE_NOT_FOUND), size);
				}
			}
		}
		
		//return new Tupel(key, storedImg.toString());
		return storedImg.toString();
	}
	
}
