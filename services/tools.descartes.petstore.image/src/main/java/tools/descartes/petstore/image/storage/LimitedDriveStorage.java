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
package tools.descartes.petstore.image.storage;

import java.nio.file.Path;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petstore.image.ImageDB;
import tools.descartes.petstore.image.StoreImage;

public class LimitedDriveStorage extends DriveStorage {

	public static final int STD_MAX_IMAGES_ON_DRIVE = 10;
	
	private ImageDB imgDB;
	private int nrOfImagesOnDrive;
	private int maxImagesOnDrive;
	private Logger log = LoggerFactory.getLogger(LimitedDriveStorage.class);
	
	public LimitedDriveStorage(Path workingDir, ImageDB imgDB, Predicate<StoreImage> storageRule) {
		this(workingDir, imgDB, storageRule, STD_MAX_IMAGES_ON_DRIVE);
	}
	
	public LimitedDriveStorage(Path workingDir, ImageDB imgDB, Predicate<StoreImage> storageRule, int maxImagesOnDrive) {
		super(workingDir, imgDB, storageRule);
		if (maxImagesOnDrive < 0) {
			log.error("Maximum number of images on drive is negative.");
			throw new IllegalArgumentException("Maximum number of images on drive is negative.");
		}
		
		this.maxImagesOnDrive = maxImagesOnDrive;
	}
	
	@Override
	public boolean dataExists(long id) {
		return imgDB.getImageSize(id) != null;
	}

	@Override
	public StoreImage loadData(long id) {
		Path imgFile = workingDir.resolve(Long.toString(id % maxImagesOnDrive));
		if (!imgFile.toFile().exists()) {
			return null;
		}
		
		return loadFromDisk(imgFile, id);
	}
	
	@Override
	public boolean saveData(StoreImage image) {
		if (nrOfImagesOnDrive >= maxImagesOnDrive) {
			return true;
		}
		nrOfImagesOnDrive++;
		return super.saveData(image);
	}
	
	public int getNrOfImagesOnDrive() {
		return nrOfImagesOnDrive;
	}
	
	public int getMaxImagesOnDrive() {
		return maxImagesOnDrive;
	}
}
