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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.entities.ImageSizePreset;
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
import tools.descartes.petsupplystore.image.storage.rules.StoreAll;
import tools.descartes.petsupplystore.image.storage.rules.StoreLargeImages;
import tools.descartes.petsupplystore.registryclient.RegistryClient;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.loadbalancers.ServiceLoadBalancer;

public enum SetupController {

	SETUP;

	private interface SetupControllerConstants {
		public final static Path STD_WORKING_DIR = Paths.get("images");
		// Longest wait period before querying the persistence again if it is finished
		// creating entries
		public final static int PERSISTENCE_CREATION_MAX_WAIT_TIME = 120000;
		// Wait time in ms before checking again for an existing persistence service
		public final static IntStream PERSISTENCE_CREATION_WAIT_TIME = IntStream.concat(
				IntStream.of(1000, 2000, 5000, 10000, 30000, 60000),
				IntStream.generate(() -> PERSISTENCE_CREATION_MAX_WAIT_TIME));
		// Nubmer of available logical cpus for image creation
		public final static int CREATION_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
		// Wait time in ms for the image creation thread pool to terminate all threads.
		public final static long CREATION_THREAD_POOL_WAIT = 500;
		// Wait time in ms (per image to generate) before an image provider service is
		// registered if there is another
		// image provider service registered.
		public final static long CREATION_THREAD_POOL_WAIT_PER_IMG_NR = 70;
	}

	private StorageRule storageRule = StorageRule.STD_STORAGE_RULE;
	private CachingRule cachingRule = CachingRule.STD_CACHING_RULE;
	private Path workingDir = SetupControllerConstants.STD_WORKING_DIR;
	private long cacheSize = IDataCache.STD_MAX_CACHE_SIZE;
	private StorageMode storageMode = StorageMode.STD_STORAGE_MODE;
	private CachingMode cachingMode = CachingMode.STD_CACHING_MODE;
	private long nrOfImagesToGenerate = 0;
	private long nrOfImagesExisting = 0;
	private long nrOfImagesForCategory = 0;
	private AtomicLong nrOfImagesGenerated = new AtomicLong();
	private HashMap<String, BufferedImage> categoryImages = new HashMap<>();
	private ImageDB imgDB = new ImageDB();
	private List<StoreImage> preCacheImg = new ArrayList<>();
	private IDataStorage<StoreImage> storage = null;
	private IDataCache<StoreImage> cache = null;
	private ScheduledThreadPoolExecutor imgCreationPool = new ScheduledThreadPoolExecutor(
			SetupControllerConstants.CREATION_THREAD_POOL_SIZE);
	private Logger log = LoggerFactory.getLogger(SetupController.class);

	private SetupController() {

	}

	private void waitForPersistence() {
		// We have to wait for the database that all entries are created before
		// generating images (which queries persistence). Yes we want to wait forever in
		// case the persistence is
		// not answering.
		while (true) {
			Response result = ServiceLoadBalancer.loadBalanceRESTOperation(Service.PERSISTENCE, "generatedb",
					String.class, client -> client.getService().path(client.getApplicationURI())
							.path(client.getEndpointURI()).path("finished").request().get());

			if (result == null ? false : Boolean.parseBoolean(result.readEntity(String.class))) {
				if (result != null) {
					result.close();
				}
				break;
			}

			if (result != null) {
				result.close();
			}

			try {
				int nextWaitTime = SetupControllerConstants.PERSISTENCE_CREATION_WAIT_TIME.findFirst()
						.orElseGet(() -> SetupControllerConstants.PERSISTENCE_CREATION_MAX_WAIT_TIME);
				log.info("Persistence not reachable. Waiting for {}ms.", nextWaitTime);
				Thread.sleep(nextWaitTime);
			} catch (InterruptedException interrupted) {
				log.info("Thread interrupted while waiting for persistence to be available.", interrupted);
			}
		}
	}

	private void fetchProductsForCategory(Category category, HashMap<Category, List<Long>> products) {
		waitForPersistence();

		Response result = ServiceLoadBalancer.loadBalanceRESTOperation(Service.PERSISTENCE, "products", Product.class,
				client -> client.getService().path(client.getApplicationURI()).path(client.getEndpointURI())
						.path("category").path(String.valueOf(category.getId())).queryParam("start", 0)
						.queryParam("max", -1).request().get());

		if (result == null) {
			products.put(category, new ArrayList<>());
			log.info("No products for category {} ({}) found.", category.getName(), category.getId());
		} else {
			List<Long> tmp = convertToIDs(result.readEntity(new GenericType<List<Product>>() { }));
			products.put(category, tmp);
			result.close();
			log.info("Category {} ({}) contains {} products.", category.getName(), category.getId(), tmp.size());
		}
	}

	private List<Category> fetchCategories() {
		waitForPersistence();

		List<Category> categories = null;
		Response result = ServiceLoadBalancer.loadBalanceRESTOperation(Service.PERSISTENCE, "categories",
				Category.class, client -> client.getService().path(client.getApplicationURI())
						.path(client.getEndpointURI()).request().get());

		if (result == null) {
			log.warn("No categories found.");
		} else {
			categories = result.readEntity(new GenericType<List<Category>>() { });
			result.close();
			log.info("{} categories found.", categories.size());
		}
		return categories == null ? new ArrayList<Category>() : categories;
	}

	private List<Long> convertToIDs(List<Product> products) {
		if (products == null) {
			return new ArrayList<>();
		}
		return products.stream().map(product -> product.getId()).collect(Collectors.toList());
	}

	private HashMap<Category, BufferedImage> matchCategoriesToImage(List<Category> categories) {
		HashMap<Category, BufferedImage> result = new HashMap<>();

		List<String> imageNames = categoryImages.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toList());
		for (String name : imageNames) {
			for (Category category : categories) {
				String[] tmp = category.getName().split(",");
				if (tmp[0].toLowerCase().equals(name)) {
					log.info("Found matching category {} ({}) for image {}.", category.getName(), category.getId(),
							name + "." + StoreImage.STORE_IMAGE_FORMAT);
					result.put(category, categoryImages.get(name));
				}
			}
		}
		return result;
	}

	public void generateImages() {
		List<Category> categories = fetchCategories();
		HashMap<Category, List<Long>> products = new HashMap<>();
		categories.forEach(cat -> fetchProductsForCategory(cat, products));

		generateImages(products, matchCategoriesToImage(categories));
	}

	public void generateImages(Map<Category, List<Long>> products, Map<Category, BufferedImage> categoryImages) {
		nrOfImagesToGenerate = products.entrySet().stream().flatMap(e -> e.getValue().stream()).count();

		CreatorFactory factory = new CreatorFactory(ImageCreator.STD_NR_OF_SHAPES_PER_IMAGE, imgDB,
				ImageSizePreset.STD_IMAGE_SIZE, workingDir, products, categoryImages, nrOfImagesGenerated);

		// Schedule all image creation tasks
		for (long i = 0; i < nrOfImagesToGenerate; i++) {
			imgCreationPool.execute(factory.newRunnable());
		}

		log.info("Image creator thread started. {} {} sized images to generate using {} threads.", nrOfImagesToGenerate,
				ImageSizePreset.STD_IMAGE_SIZE.toString(), SetupControllerConstants.CREATION_THREAD_POOL_SIZE);
	}

	public void detectCategoryImages() {
		log.info("Trying to find images that indicate categories in generated images.");

		String resPath = "categoryimg" + File.separator + "dogs.png";
		File dir = getPathToResource(resPath).toFile();

		if (dir != null) {
			log.info("Found resource directory with category images at {}.", dir.toPath().toAbsolutePath().toString());
		} else {
			log.info("Resource path {} not found.", resPath);
			return;
		}

		nrOfImagesForCategory = 0;
		if (dir != null && dir.exists() && dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isFile() && file.getName().endsWith(StoreImage.STORE_IMAGE_FORMAT)) {
					try {
						categoryImages.put(file.getName().substring(0, file.getName().length() - 4),
								ImageIO.read(file));
						nrOfImagesForCategory++;
					} catch (IOException ioException) {
						log.warn("An IOException occured while reading image file " + file.getAbsolutePath() + ".",
								ioException);
					}
				}
			}
		}
		log.info("Found {} images for categories.", nrOfImagesForCategory);
	}

	public void createWorkingDir() {
		if (!workingDir.toFile().exists()) {
			if (!workingDir.toFile().mkdir()) {
				log.error("Standard working directory \"" + workingDir.toAbsolutePath() + "\" could not be created.");
				throw new IllegalArgumentException(
						"Standard working directory \"" + workingDir.toAbsolutePath() + "\" could not be created.");
			} else {
				log.info("Working directory {} created.", workingDir.toAbsolutePath().toString());
			}
		} else {
			log.info("Working directory {} already existed.", workingDir.toAbsolutePath().toString());
		}
	}

	public Path getPathToResource(String resource) {
		// NOTODO: Rework the code piece fetching the existing images until the next
		// comment
		URL url = this.getClass().getResource(resource);
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
		}
		// End of rework
		return dir;
	}

	public void detectExistingImages() {
		detectExistingImages(imgDB);
	}

	public void detectExistingImages(ImageDB db) {
		if (db == null) {
			log.error("The supplied image database is null.");
			throw new NullPointerException("The supplied image database is null.");
		}

		String resPath = "existingimg" + File.separator + "front.png";
		Path dir = getPathToResource(resPath);

		if (dir != null) {
			log.info("Found resource directory with existing images at {}.", dir.toAbsolutePath().toString());
		} else {
			log.info("Resource path {} not found.", resPath);
			return;
		}

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

					db.setImageMapping(
							file.getName().substring(0,
									file.getName().length() - StoreImage.STORE_IMAGE_FORMAT.length() - 1),
							imageID, new ImageSize(buffImg.getWidth(), buffImg.getHeight()));
					StoreImage img = new StoreImage(imageID, buffImg, ImageSizePreset.FULL.getSize());
					preCacheImg.add(img);

					try {
						Files.write(workingDir.resolve(String.valueOf(imageID)), img.getByteArray(),
								StandardOpenOption.CREATE, StandardOpenOption.WRITE,
								StandardOpenOption.TRUNCATE_EXISTING);
					} catch (IOException ioException) {
						log.warn("An IOException occured while writing the image with ID " + String.valueOf(imageID)
								+ " to the file " + workingDir.resolve(String.valueOf(imageID)).toAbsolutePath() + ".",
								ioException.getMessage());
					}
					// Increment to have correct number of images for the limited drive storage
					nrOfImagesExisting++;
				}
			}
		}

		log.info("Scanned path {} for existing images. {} images found.", dir.toAbsolutePath().toString(),
				nrOfImagesExisting);
	}

	public boolean setCacheSize(long cacheSize) {
		if (cacheSize < 0) {
			log.info("Tried to set cache size to a value below zero. Keeping old value");
			return false;
		}
		if (cache == null) {
			log.info("No cache defined.");
			return false;
		}
		return cache.setMaxCacheSize(cacheSize);
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

		log.info("Deleted images in working directory {}. {} images deleted.", workingDir.toAbsolutePath().toString(),
				nrOfImagesDeleted);
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
		case ALL:
			storagePredicate = new StoreAll<StoreImage>();
			break;
		case FULL_SIZE_IMG:
			storagePredicate = new StoreLargeImages();
			break;
		default:
			storagePredicate = new StoreAll<StoreImage>();
			break;
		}

		storage = null;
		switch (storageMode) {
		case DRIVE:
			storage = new DriveStorage(workingDir, imgDB, storagePredicate);
			break;
		default:
			storage = new DriveStorage(workingDir, imgDB, storagePredicate);
			break;
		}

		Predicate<StoreImage> cachePredicate = null;
		switch (cachingRule) {
		case ALL:
			cachePredicate = new CacheAll<StoreImage>();
			break;
		default:
			cachePredicate = new CacheAll<StoreImage>();
			break;
		}

		cache = null;
		switch (cachingMode) {
		case FIFO:
			cache = new FirstInFirstOut<StoreImage>(storage, cacheSize, cachePredicate);
			break;
		case LIFO:
			cache = new LastInFirstOut<StoreImage>(storage, cacheSize, cachePredicate);
			break;
		case RR:
			cache = new RandomReplacement<StoreImage>(storage, cacheSize, cachePredicate);
			break;
		case LFU:
			cache = new LeastFrequentlyUsed<StoreImage>(storage, cacheSize, cachePredicate);
			break;
		case LRU:
			cache = new LeastRecentlyUsed<StoreImage>(storage, cacheSize, cachePredicate);
			break;
		case MRU:
			cache = new MostRecentlyUsed<StoreImage>(storage, cacheSize, cachePredicate);
			break;
		case NONE:
			break;
		default:
			break;
		}

		if (cache != null) {
			for (StoreImage img : preCacheImg) {
				cache.cacheData(img);
			}
		}

		log.info("Storage setup done.");
	}

	public void configureImageProvider() {
		ImageProvider.IP.setImageDB(imgDB);
		ImageProvider.IP.setStorage(cache == null ? storage : cache);

		log.info("Storage and image database handed over to image provider");
	}

	public Path getWorkingDir() {
		return workingDir;
	}

	public boolean isFinished() {
		if (storage == null) {
			return false;
		}
		if (imgCreationPool.getQueue().size() != 0) {
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
		sb.append("Creator Thread: ").append(imgCreationPool.getQueue().size() != 0 ? "Running" : "Finished")
				.append(System.lineSeparator());
		sb.append("Images Created: ").append(String.valueOf(nrOfImagesGenerated.get())).append(" / ")
				.append(String.valueOf(nrOfImagesToGenerate)).append(System.lineSeparator());
		sb.append("Pre-Existing Images Found: ").append(String.valueOf(nrOfImagesExisting))
				.append(System.lineSeparator());
		sb.append("Category Images Found: ").append(String.valueOf(nrOfImagesForCategory))
				.append(System.lineSeparator());

		return sb.toString();
	}

	private void waitAndStopImageCreation(boolean terminate, long waitTime) {
		// Stop image creation to have sort of a steady state to work on
		// Shutdown now will finish all running tasks and not schedule new threads
		// Shutdown does allow the thread pool to finish all available tasks but no new
		// ones
		if (terminate) {
			imgCreationPool.shutdownNow();
			log.info("Send termination signal to image creation thread pool.");
		} else {
			imgCreationPool.shutdown();
			log.info("Send shutdown signal to image creation thread pool.");
		}
		try {
			if (imgCreationPool.awaitTermination(waitTime, TimeUnit.MILLISECONDS)) {
				log.info("Image creation stopped.");
			} else {
				log.warn("Image creation thread pool not terminating after {}ms. Stop waiting.", waitTime);
			}
		} catch (InterruptedException interruptedException) {
			log.warn("Waiting for image creation thread pool termination interrupted by exception.",
					interruptedException);
		}
		// Maybe we need to keep a reference to the old thread pool if it has not
		// finished properly yet.
		imgCreationPool = new ScheduledThreadPoolExecutor(SetupControllerConstants.CREATION_THREAD_POOL_SIZE);
	}

	private boolean isFirstImageProvider() {
		return RegistryClient.getClient().getServersForService(Service.IMAGE).size() == 0;
	}

	/*
	 * Convenience methods
	 */

	public void teardown() {
		deleteImages();
		deleteWorkingDir();
	}

	public void startup() {
		// Delete all images in case the image provider was not shutdown gracefully last
		// time, leaving images on disk
		deleteImages();
		deleteWorkingDir();
		createWorkingDir();
		detectExistingImages();
		detectCategoryImages();
		generateImages();
		setupStorage();
		configureImageProvider();
		// Check if this is the first image provider. If not, wait for termination of
		// the image creation before registering
		if (!isFirstImageProvider()) {
			waitAndStopImageCreation(false,
					((nrOfImagesToGenerate - nrOfImagesGenerated.get())
							/ SetupControllerConstants.CREATION_THREAD_POOL_SIZE)
							* SetupControllerConstants.CREATION_THREAD_POOL_WAIT_PER_IMG_NR);
		}
	}

	public void reconfiguration() {
		Thread x = new Thread() {

			@Override
			public void run() {
				waitAndStopImageCreation(true, SetupControllerConstants.CREATION_THREAD_POOL_WAIT);
				imgDB = new ImageDB();

				deleteImages();
				detectExistingImages();
				detectCategoryImages();
				generateImages();
				setupStorage();
				configureImageProvider();
			}
		};
		x.start();
	}

}
