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
package tools.descartes.teastore.image;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.entities.ImageSize;
import tools.descartes.teastore.entities.ImageSizePreset;
import tools.descartes.teastore.image.setup.ImageIDFactory;
import tools.descartes.teastore.image.storage.IDataStorage;

/**
 * The actual image provider class containing the mapping between products, web interface static images, the 
 * cache and underlying storage. Only one instance of an image provider can exist.
 * @author Norbert Schmitt
 */
public enum ImageProvider {

  /**
   * Instance of the image provider.
   */
  IP;

  /**
   * Standard image identifier if a product or web interface image cannot be found in the cache and storage.
   */
  public static final String IMAGE_NOT_FOUND = "notFound";

  private ImageDB db;
  private IDataStorage<StoreImage> storage;
  private Logger log = LoggerFactory.getLogger(ImageProvider.class);

  private ImageProvider() {

  }

  /**
   * Assign the image provider the mapping between products and web interface static images.
   * @param imgDB Image database, mapping between products and web interface static images.
   */
  public void setImageDB(ImageDB imgDB) {
	if (imgDB != null) {
	  db = imgDB;
	}
  }

  /**
   * Assign the storage containing all available images. This can either be a cache or the actual hard drive storage.
   * @param imgStorage Image storage containing all available images.
   */
  public void setStorage(IDataStorage<StoreImage> imgStorage) {
	if (imgStorage != null) {
      storage = imgStorage;
	}
  }

  /**
   * Searches and returns the requested product images in the requested sizes. If an image can not be found, the 
   * standard "not found" image is returned. If an image is found in the incorrect size, the largest size of this image 
   * will be scaled and the scaled version will be moved to storage and returned.
   * @param images Map of product IDs and image sizes to search for.
   * @return Map between product IDs and base64 encoded image data as string.
   */
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

  /**
   * Searches and returns the requested web interface images in the requested sizes. If an image can not be found, the 
   * standard "not found" image is returned. If an image is found in the incorrect size, the largest size of this image 
   * will be scaled and the scaled version will be moved to storage and returned.
   * @param images Map of product IDs and image sizes to search for.
   * @return Map between product IDs and base64 encoded image data as string.
   */
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

    ImageSize stdSize = ImageSizePreset.STD_IMAGE_SIZE;
    StoreImage storedImg = null;

    // Try to retrieve image from disk or from cache
    long imgID = db.getImageID(key, size);
    if (imgID != 0) {
      storedImg = storage.loadData(imgID);
    }

    // If we dont have the image in the right size, get the biggest one and scale it
    if (storedImg == null) {
      storedImg = storage.loadData(db.getImageID(key, stdSize));
      if (storedImg != null) {
        storedImg = scaleAndRegisterImg(storedImg.getImage(), key, size);
      } else {
        storedImg = storage.loadData(db.getImageID(IMAGE_NOT_FOUND, size));
        if (storedImg == null) {
          storedImg = storage.loadData(db.getImageID(IMAGE_NOT_FOUND, stdSize));
          if (storedImg == null) {
            return null;
          }
          storedImg = scaleAndRegisterImg(storedImg.getImage(), new ImageDBKey(IMAGE_NOT_FOUND),
              size);
        }
      }
    }

    return storedImg.toString();
  }

}
