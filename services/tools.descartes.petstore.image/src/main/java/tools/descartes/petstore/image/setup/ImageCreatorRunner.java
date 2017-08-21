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
package tools.descartes.petstore.image.setup;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.image.ImageDB;
import tools.descartes.petstore.image.StoreImage;

public class ImageCreatorRunner implements Runnable {
	
	public static final long PAUSE_TIME = 10;
	
	private List<Long> productIDs = new ArrayList<>();
	private int nrOfImagesToGenerate = 0;
	private int imagesCreated = 0;
	private long timeBeforeGeneration = 0;
	private long timeAfterGeneration = 0;
	private ImageDB imgDB = new ImageDB();
	private Path workingDir;
	private boolean isRunning = false;
	private ImageCreator imgCreator;
	private ImageSize imgSize;
	private boolean isPaused = false;
	private boolean stopped = false;
	
	public ImageCreatorRunner(List<Long> productIDs, Path workingDir) {
		this(productIDs, workingDir, ImageCreator.STD_NR_OF_SHAPES_PER_IMAGE);
	}
	
	public ImageCreatorRunner(List<Long> productIDs, Path workingDir, int nrOfShapesPerImage) {
		this(productIDs, workingDir, nrOfShapesPerImage, ImageCreator.STD_SEED, ImageSize.STD_IMAGE_SIZE);
	}
	
	public ImageCreatorRunner(List<Long> productIDs, Path workingDir, int nrOfShapesPerImage, long creatorSeed) {
		this(productIDs, workingDir, nrOfShapesPerImage, creatorSeed, ImageSize.STD_IMAGE_SIZE);
	}
	
	public ImageCreatorRunner(List<Long> productIDs, Path workingDir, int nrOfShapesPerImage, long creatorSeed,
			ImageSize imageSize) {
		this(productIDs, workingDir, null, nrOfShapesPerImage, creatorSeed, imageSize);
	}
	
	public ImageCreatorRunner(List<Long> productIDs, Path workingDir, ImageDB preInitDB, int nrOfShapesPerImage, 
			long creatorSeed, ImageSize imageSize) {
		this(productIDs, workingDir, preInitDB, nrOfShapesPerImage, creatorSeed, imageSize, productIDs.size());
	}
	
	public ImageCreatorRunner(List<Long> productIDs, Path workingDir, ImageDB preInitDB, int nrOfShapesPerImage, 
			long creatorSeed, ImageSize imageSize, int nrOfImagesToGenerate) {
		if (productIDs == null) {
			throw new NullPointerException("List of product IDs is null.");
		}
		if (workingDir == null) {
			throw new NullPointerException("Image storage directory is null.");
		}
		if (imageSize == null) {
			throw new NullPointerException("Image size is null.");
		}
		if (nrOfImagesToGenerate < 1) {
			throw new IllegalArgumentException("Image creation cannot be limited to a value below one.");
		}
		
		this.productIDs = new ArrayList<>(productIDs);
		this.workingDir = workingDir;
		this.imgCreator = new ImageCreator(nrOfShapesPerImage, creatorSeed);
		this.imgSize = imageSize;
		this.nrOfImagesToGenerate = nrOfImagesToGenerate;
		if (preInitDB != null) {
			imgDB = preInitDB;
		}
	}
	
	@Override
	public void run() {
		isRunning = true;
		timeBeforeGeneration = System.currentTimeMillis();
		
		for (long product : productIDs) {
			if (stopped) {
				timeAfterGeneration = System.currentTimeMillis();
				isRunning = false;
				return;
			}
			
			while (isPaused) {
				try {
					Thread.sleep(PAUSE_TIME);
					if (stopped) {
						timeAfterGeneration = System.currentTimeMillis();
						isRunning = false;
						return;
					}
				} catch (InterruptedException e) {
					
				}
			}
			
			long imgID = ImageIDFactory.ID.getNextImageID();
			
			// All products must be added to the database
			imgDB.setImageMapping(product, imgID, imgSize);

			// Do not create more images if the maximum number is reached
			if (imagesCreated >= nrOfImagesToGenerate) {
				continue;
			}
			
			// Resolve path and create a new image
			Path imgFile = workingDir.resolve(String.valueOf(imgID));
			
			BufferedImage img = imgCreator.createImage(imgSize);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			
			try {
				ImageIO.write(img, StoreImage.STORE_IMAGE_FORMAT, stream);
				Files.write(imgFile, Base64.getEncoder().encode(stream.toByteArray()), 
						StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			} catch (IOException ioException) {
				imagesCreated++;
				timeAfterGeneration = System.currentTimeMillis();
				isRunning = false;
				
				return;
			}
			
			imagesCreated++;
		}
		
		timeAfterGeneration = System.currentTimeMillis();
		isRunning = false;
	}

	public boolean isRunning() {
		return isRunning;
	}
	
	public long getRuntime() {
		return timeAfterGeneration - timeBeforeGeneration;
	}
	
	public long getAvgCreationTime() {
		if (imagesCreated == 0) {
			return 0;
		}
		if (isRunning) {
			return (System.currentTimeMillis() - timeBeforeGeneration) / imagesCreated;
		} else {
			return (timeAfterGeneration - timeBeforeGeneration) / imagesCreated;
		}
	}
	
	public ImageDB getImageDB() {
		return imgDB;
	}
	
	public long getNrOfImagesCreated() {
		return imagesCreated;
	}
	
	public void pause() {
		isPaused = true;
	}
	
	public void resume() {
		isPaused = false;
	}
	
	public void stopCreation() {
		stopped = true;
	}
	
}
