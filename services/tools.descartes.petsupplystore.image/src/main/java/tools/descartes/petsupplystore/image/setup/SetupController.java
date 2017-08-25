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
package tools.descartes.petsupplystore.image.setup;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.image.ImageDB;
import tools.descartes.petsupplystore.image.ImageProvider;
import tools.descartes.petsupplystore.image.StoreImage;
import tools.descartes.petsupplystore.image.cache.FirstInFirstOut;
import tools.descartes.petsupplystore.image.cache.IDataCache;
import tools.descartes.petsupplystore.image.cache.LastInFirstOut;
import tools.descartes.petsupplystore.image.cache.LeastFrequentlyUsed;
import tools.descartes.petsupplystore.image.cache.LeastRecentlyUsed;
import tools.descartes.petsupplystore.image.cache.MostRecentlyUsed;
import tools.descartes.petsupplystore.image.cache.RandomReplacement;
import tools.descartes.petsupplystore.image.cache.rules.CacheAll;
import tools.descartes.petsupplystore.image.storage.DriveStorage;
import tools.descartes.petsupplystore.image.storage.IDataStorage;
import tools.descartes.petsupplystore.image.storage.LimitedDriveStorage;
import tools.descartes.petsupplystore.image.storage.rules.StoreAll;
import tools.descartes.petsupplystore.image.storage.rules.StoreLargeImages;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedCRUDOperations;

public enum SetupController {

	SETUP;

	private interface SetupControllerConstants {
		public final static Path STD_WORKING_DIR = Paths.get("images");
		public final static int PERSISTENCE_CREATION_WAIT_TIME = 1000;
		public final static int PERSISTENCE_CREATION_TRIES = 100;
	}
	
	private StorageRule storageRule = StorageRule.STD_STORAGE_RULE;
	private CachingRule cachingRule = CachingRule.STD_CACHING_RULE;
	private Path workingDir = SetupControllerConstants.STD_WORKING_DIR;
	private long cacheSize = IDataCache.STD_MAX_CACHE_SIZE;
	private StorageMode storageMode = StorageMode.STD_STORAGE_MODE;
	private CachingMode cachingMode = CachingMode.STD_CACHING_MODE;
	private int nrOfImagesToGenerate = 0;
	private int nrOfImagesPreExisting = 0;
	private ImageDB imgDB = new ImageDB();
	private List<StoreImage> preCacheImg = new ArrayList<>();
	private ImageCreatorRunner imgCreatorRunner;
	private Thread imgCreatorThread;
	private IDataStorage<StoreImage> storage = null;
	private Logger log = LoggerFactory.getLogger(SetupController.class);
	
	private SetupController() {
		
	}
	
	public void setWorkingDir(Path path) { 
		if (path != null) {
			workingDir = path;
		}
	}

	private List<Product> fetchProducts() {
		// We have to wait for the database that all entries are created before 
		// generating images (which queries persistence)
		boolean maxTriesReached = true;
		for (int i = 0; i < SetupControllerConstants.PERSISTENCE_CREATION_TRIES; i++) {
			Response result = ServiceLoadBalancer.loadBalanceRESTOperation(Service.PERSISTENCE, "generatedb", 
					String.class, client -> client.getService().path(client.getApplicationURI())
					.path(client.getEndpointURI()).path("finished").request().get());
			
			if (result == null ? false : Boolean.parseBoolean(result.readEntity(String.class))) {
				maxTriesReached = false;
				break;
			}
			
			try {
				Thread.sleep(SetupControllerConstants.PERSISTENCE_CREATION_WAIT_TIME);
			} catch (InterruptedException interrupted) {
				log.info("Thread interrupted while waiting for persistence to be available.", interrupted);
			}
		}
	
		List<Product> products = null;
		if (!maxTriesReached) {
			// TODO: Make it a REST instead of CRUD operation
			products = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "products", Product.class, -1, -1);
		} else {
			log.warn("Maximum tries to reach persistence service reached. No products fetched.");
		}
		return products == null ? new ArrayList<Product>() : products;
	}
	
	private List<Long> convertToIDs(List<Product> products) {
		return products.stream().map(product -> product.getId()).collect(Collectors.toList());
	}
	
	public void generateImages() {
		List<Long> productIDs = convertToIDs(fetchProducts());
		generateImages(productIDs, productIDs.size());		
	}

	public void generateImages(int nrOfImagesToGenerate) {
		generateImages(convertToIDs(fetchProducts()), nrOfImagesToGenerate);
	}
	
	public void generateImages(List<Long> productIDs, int nrOfImagesToGenerate) {
		if (nrOfImagesToGenerate <= 0) {
			return;
		}

		this.nrOfImagesToGenerate = nrOfImagesToGenerate;
		
		// Create images
		imgCreatorRunner = new ImageCreatorRunner(productIDs, workingDir, imgDB, 
				ImageCreator.STD_NR_OF_SHAPES_PER_IMAGE, ImageCreator.STD_SEED, ImageSize.STD_IMAGE_SIZE, 
				nrOfImagesToGenerate);
		imgCreatorThread = new Thread(imgCreatorRunner);
		imgCreatorThread.start();
		
		log.info("Image creator thread started. {} images to generate.", nrOfImagesToGenerate);
	}
	
	public void createWorkingDir() {
		if (!workingDir.toFile().exists()) {
			if (!workingDir.toFile().mkdir()) {
				log.error("Standard working directory \"" + workingDir.toAbsolutePath() + "\" could not be created.");
				throw new IllegalArgumentException("Standard working directory \"" 
						+ workingDir.toAbsolutePath() + "\" could not be created.");
			} else {
				log.info("Working directory {} created.", workingDir.toAbsolutePath().toString());
			}
		} else {
			log.info("Working directory {} already existed.", workingDir.toAbsolutePath().toString());
		}
	}	
	
	public void detectPreExistingImages() {
		detectPreExistingImages(imgDB);
	}
	
	public void detectPreExistingImages(ImageDB db) {
		if (db == null) {
			log.error("The supplied image database is null.");
			throw new NullPointerException("The supplied image database is null.");
		}
	
		// TODO: Rework the code piece fetching the pre-existing images until the next comment
		URL url = this.getClass().getResource("front.png");
		Path dir = null;
		String path = "";
		try {
			path = URLDecoder.decode(url.getPath(), "UTF-8");
			if (path.contains(":")) {
				path = path.substring(3);
			}
			dir = Paths.get(path).getParent();
		} catch (UnsupportedEncodingException e) {
			log.warn("The resource path \"" + path + "\" could not be decoded with UTF-8.");
			return;
		}
		// End of rework
		
		log.info("Found resource directory with existing images at {}.", dir.toAbsolutePath().toString());

		File currentDir = dir.toFile();

		if (currentDir.exists() && currentDir.isDirectory()) {
			for (File file : currentDir.listFiles()) {
				if (file.isFile() && file.getName().endsWith(StoreImage.STORE_IMAGE_FORMAT)) {
					long imageID = ImageIDFactory.ID.getNextImageID();
					
					BufferedImage buffImg = null;
					// Copy files to correct file with the image id number
					try {
						buffImg = ImageIO.read(file);
						if (buffImg == null) {
							log.warn("The file \"" + file.toPath().toAbsolutePath() + "\" could not be read.");
							continue;
						}
					} catch (IOException ioException) {
						log.warn("An IOException occured while reading the file " + file.getAbsolutePath() 
								+ " from disk.", ioException.getMessage());
					}
					
					db.setImageMapping(file.getName().substring(0, 
							file.getName().length() - StoreImage.STORE_IMAGE_FORMAT.length() - 1), 
							imageID, ImageSize.FULL);	
					StoreImage img = new StoreImage(imageID, buffImg, ImageSize.FULL);
					preCacheImg.add(img);
					
					try {
						Files.write(workingDir.resolve(String.valueOf(imageID)), img.getByteArray(), 
								StandardOpenOption.CREATE, 
								StandardOpenOption.WRITE, 
								StandardOpenOption.TRUNCATE_EXISTING);
					} catch (IOException ioException) {
						log.warn("An IOException occured while writing the image with ID " + String.valueOf(imageID)
								+ " to the file " + workingDir.resolve(String.valueOf(imageID)).toAbsolutePath()
								+ ".", ioException.getMessage());
					}
					// Increment to have correct number of images for the limited drive storage
					nrOfImagesPreExisting++; 
				}
			}
		}
		
		log.info("Scanned path {} for existing images. {} images found.", 
				dir.toAbsolutePath().toString(), nrOfImagesPreExisting);
	}
	
	public void setCachingMode(String cachingMode) {
		this.cachingMode = CachingMode.getCachingModeFromString(cachingMode);
	}
	
	public void setCachingRule(String cachingRule) {
		this.cachingRule = CachingRule.getCachingRuleFromString(cachingRule);
	}
	
	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	public void setStorageMode(String storageMode) {
		this.storageMode = StorageMode.getStorageModeFromString(storageMode);
	}
	
	public void setStorageRule(String storageRule) {
		this.storageRule = StorageRule.getStorageRuleFromString(storageRule);
	}
	
	public void deleteImages() {
		deleteUnusedImages(new ArrayList<>());
	}
	
	public void deleteUnusedImages(List<Long> imagesToKeep) {
		File currentDir = workingDir.toFile();
		int nrOfImagesDeleted = 0;
		
		if (currentDir.exists() && currentDir.isDirectory()) {
			for (File file : currentDir.listFiles()) {
				if (file.isFile() && !imagesToKeep.contains(Long.parseLong(file.getName()))) {
					file.delete();
					nrOfImagesDeleted++;
				}
			}
		}
		
		log.info("Deleted images in working directory {}. {} images deleted.", 
				workingDir.toAbsolutePath().toString(), nrOfImagesDeleted);
	}
	
	public void deleteWorkingDir() {
		File currentDir = workingDir.toFile();
		
		if (currentDir.exists() && currentDir.isDirectory()) {
			currentDir.delete();
		}
		
		log.info("Deleted working directory {}.", workingDir.toAbsolutePath().toString());
	}
	
	public void setupStorage() {
		Predicate<StoreImage> storagePredicate = null;
		switch (storageRule) {
			case ALL: storagePredicate = new StoreAll<StoreImage>(); break;
			case FULL_SIZE_IMG: storagePredicate = new StoreLargeImages(); break;
			default: storagePredicate = new StoreAll<StoreImage>(); break;
		}
	
		storage = null;
		switch (storageMode) {
			case DRIVE: storage = new DriveStorage(workingDir, imgDB, storagePredicate); break;
			case DRIVE_LIMITED: storage = new LimitedDriveStorage(workingDir, imgDB, 
					storagePredicate, nrOfImagesToGenerate + nrOfImagesPreExisting); break;
			default: storage = new DriveStorage(workingDir, imgDB, storagePredicate); break;
		}

		Predicate<StoreImage> cachePredicate = null;
		switch (cachingRule) {
			case ALL: cachePredicate = new CacheAll<StoreImage>(); break;
			default: cachePredicate = new CacheAll<StoreImage>(); break;
		}
		
		IDataCache<StoreImage> cache = null;
		switch (cachingMode) {
			case FIFO: cache = new FirstInFirstOut<StoreImage>(storage, cacheSize, cachePredicate); break;
			case LIFO: cache = new LastInFirstOut<StoreImage>(storage, cacheSize, cachePredicate); break;
			case RR: cache = new RandomReplacement<StoreImage>(storage, cacheSize, cachePredicate); break;
			case LFU: cache = new LeastFrequentlyUsed<StoreImage>(storage, cacheSize, cachePredicate); break;
			case LRU: cache = new LeastRecentlyUsed<StoreImage>(storage, cacheSize, cachePredicate); break;
			case MRU: cache = new MostRecentlyUsed<StoreImage>(storage, cacheSize, cachePredicate); break;
			case NONE: break;
			default: break;
		}
		
		if (cache != null) {
			for (StoreImage img : preCacheImg) {
				cache.cacheData(img);
			}
			storage = cache;
		}
		
		log.info("Storage setup done.");
	}
	
	public void configureImageProvider() {
		ImageProvider.IP.setImageDB(imgDB);
		ImageProvider.IP.setImageCreatorRunner(imgCreatorRunner);
		ImageProvider.IP.setStorage(storage);
		
		log.info("Storage and image database handed over to image provider");
	}
	
	public Path getWorkingDir() {
		return workingDir;
	}
	
	public boolean isFinished() {
		if (storage == null) {
			return false;
		}
		if (imgCreatorRunner.isRunning()) {
			return false;
		}
		if (imgCreatorRunner.getNrOfImagesCreated() != nrOfImagesToGenerate) {
			return false;
		}
		return true;
	}
	
	public String getState() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Image Provider State:").append(System.lineSeparator());
		sb.append("---------------------").append(System.lineSeparator());
		sb.append("Working Directory: ").append(workingDir.toAbsolutePath().toString()).append(System.lineSeparator());
		sb.append("Storage Mode: ").append(storageMode.getStrRepresentation()).append(System.lineSeparator());
		sb.append("Storage Rule: ").append(storageRule.getStrRepresentation()).append(System.lineSeparator());
		sb.append("Caching Mode: ").append(cachingMode.getStrRepresentation()).append(System.lineSeparator());
		sb.append("Caching Rule: ").append(cachingRule.getStrRepresentation()).append(System.lineSeparator());
		sb.append("Creator Thread: ").append(imgCreatorRunner.isRunning() ? "Running" : "Finished")
				.append(System.lineSeparator());
		sb.append("Images Created: ").append(String.valueOf(imgCreatorRunner.getNrOfImagesCreated()))
				.append(" / ").append(String.valueOf(nrOfImagesToGenerate)).append(System.lineSeparator());
		sb.append("Avg. Creation Time Per Image (ms): ").append(String.valueOf(imgCreatorRunner.getAvgCreationTime()))
				.append(System.lineSeparator());
		sb.append("Pre-Existing Images Found: ").append(String.valueOf(nrOfImagesPreExisting))
				.append(System.lineSeparator());
		
		return sb.toString();
	}

	/*
	 * Convenience methods
	 */

	public void teardown() {
		deleteImages();
		deleteWorkingDir();
	}
	
	public void startup() {
		// Delete all images in case the image provider was not shutdown gracefully last time, leaving images in disk
		deleteImages();
		deleteWorkingDir();
		createWorkingDir();
		detectPreExistingImages();
		generateImages();
		setupStorage();
		configureImageProvider();
	}
	
	public void reconfiguration() {
		Thread x = new Thread() {

			@Override
			public void run() {
				// Stop image creation to have sort of a steady state to work on
				imgCreatorRunner.stopCreation();
				while (imgCreatorRunner.isRunning()) {
					try {
						Thread.sleep(imgCreatorRunner.getAvgCreationTime());
					} catch (InterruptedException interrupted) {
						log.info("Thread to regenerate images interrupted while waiting for image creator "
								+ "thread to stop.", interrupted);
					}
				}

				imgDB = new ImageDB();
				
				deleteImages();
				detectPreExistingImages();
				generateImages();
				setupStorage();
				configureImageProvider();
			}
		};
		x.start();
	}
	
}
