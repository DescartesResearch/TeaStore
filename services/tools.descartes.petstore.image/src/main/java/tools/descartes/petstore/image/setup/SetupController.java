package tools.descartes.petstore.image.setup;

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

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.entities.Product;
import tools.descartes.petstore.image.ImageDB;
import tools.descartes.petstore.image.ImageProvider;
import tools.descartes.petstore.image.StoreImage;
import tools.descartes.petstore.image.cache.FirstInFirstOut;
import tools.descartes.petstore.image.cache.IDataCache;
import tools.descartes.petstore.image.cache.LastInFirstOut;
import tools.descartes.petstore.image.cache.LeastFrequentlyUsed;
import tools.descartes.petstore.image.cache.LeastRecentlyUsed;
import tools.descartes.petstore.image.cache.MostRecentlyUsed;
import tools.descartes.petstore.image.cache.RandomReplacement;
import tools.descartes.petstore.image.cache.rules.CacheAll;
import tools.descartes.petstore.image.storage.DriveStorage;
import tools.descartes.petstore.image.storage.IDataStorage;
import tools.descartes.petstore.image.storage.LimitedDriveStorage;
import tools.descartes.petstore.image.storage.rules.StoreAll;
import tools.descartes.petstore.image.storage.rules.StoreLargeImages;
import tools.descartes.petstore.registryclient.Service;
import tools.descartes.petstore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.petstore.registryclient.rest.LoadBalancedCRUDOperations;

public class SetupController {
	
	public final static Path STD_WORKING_DIR = Paths.get("images");
	public final static int PERSISTENCE_CREATION_WAIT_TIME = 1000;
	
	private static SetupController instance = new SetupController();
	
	private StorageRule storageRule = StorageRule.STD_STORAGE_RULE;
	private CachingRule cachingRule = CachingRule.STD_CACHING_RULE;
	private Path workingDir = STD_WORKING_DIR;
	private long cacheSize = IDataCache.STD_MAX_CACHE_SIZE;
	private StorageMode storageMode = StorageMode.STD_STORAGE_MODE;
	private CachingMode cachingMode = CachingMode.STD_CACHING_MODE;
	private int nrOfImagesToGenerate = 0;
	private int nrOfImagesPreExisting = 0;
	private ImageDB imgDB = new ImageDB();
	private List<StoreImage> preCacheImg = new ArrayList<>();
	private ImageCreatorRunner imgCreatorRunner;
	private Thread imgCreatorThread;
	
	private SetupController() {
		createWorkingDir(workingDir);
	}
	
	public void setWorkingDir(Path path) { 
		if (path != null)
			workingDir = path;
	}
	
	public static SetupController getInstance() {
		return instance;
	}

	private List<Product> fetchProducts() {
		// We have to wait for the database that all entries are created before 
		// generating images (which queries persistence)
		while (true) {
			Response result = ServiceLoadBalancer.loadBalanceRESTOperation(Service.PERSISTENCE, "generatedb", 
					String.class, client -> client.getService().path(client.getApplicationURI())
					.path(client.getEnpointURI()).path("finished").request().get());
			if (result == null ? false : Boolean.parseBoolean(result.readEntity(String.class))) {
				break;
			}
			try {
				Thread.sleep(PERSISTENCE_CREATION_WAIT_TIME);
			} catch (InterruptedException e) {
				
			}
		}
		// TODO: Make it a REST instead of CRUD operation
		List<Product> products = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "products", 
				Product.class, -1, -1);
		return products == null ? new ArrayList<Product>() : products;
	}
	
	private List<Long> convertToIDs(List<Product> products) {
		return products.stream().map(product -> product.getId()).collect(Collectors.toList());
	}
	
	public void generateImages() {
		List<Product> products = fetchProducts();
		if (products == null)
			return;
		
		List<Long> productIDs = convertToIDs(products);
		generateImages(productIDs, productIDs.size());		
	}

	public void generateImages(int nrOfImagesToGenerate) {
		List<Product> products = fetchProducts();
		if (products == null)
			return;
		
		List<Long> productIDs = convertToIDs(products);
		generateImages(productIDs, nrOfImagesToGenerate);
	}
	
	public void generateImages(List<Long> productIDs, int nrOfImagesToGenerate) {
		if (productIDs == null || nrOfImagesToGenerate <= 0)
			return;

		this.nrOfImagesToGenerate = nrOfImagesToGenerate;
		
		// Create images
		imgCreatorRunner = new ImageCreatorRunner(productIDs, STD_WORKING_DIR, imgDB, 
				ImageCreator.STD_NR_OF_SHAPES_PER_IMAGE, ImageCreator.STD_SEED, ImageSize.STD_IMAGE_SIZE, 
				nrOfImagesToGenerate);
		imgCreatorThread = new Thread(imgCreatorRunner);
		imgCreatorThread.start();
	}
	
	private void createWorkingDir(Path directory) {
		if (!directory.toFile().exists()) {
			if (!directory.toFile().mkdir()) {
				throw new IllegalArgumentException("Standard working directory \"" 
						+ directory.toAbsolutePath() + "\" could not be created.");
			}
		}
	}	
	
	public void detectPreExistingImages() {
		detectPreExistingImages(imgDB, workingDir);
	}
	
	public void detectPreExistingImages(Path directory) {
		detectPreExistingImages(imgDB, directory);
	}
	
	public void detectPreExistingImages(ImageDB db, Path directory) {
		if (db == null)
			throw new NullPointerException("Image database is null.");
		if (directory == null)
			throw new NullPointerException("Working directory is null.");
	
		createWorkingDir(workingDir);
		this.nrOfImagesPreExisting = 0;
		
		ImageIDFactory idFactory = ImageIDFactory.getInstance();
		
		URL url = this.getClass().getResource("front.png");
		Path dir = null;
		try {
			String path = URLDecoder.decode(url.getPath(), "UTF-8");
			if (path.contains(":"))
				path = path.substring(3);
			dir = Paths.get(path).getParent();
		} catch (UnsupportedEncodingException e) {
			return;
		}

		File currentDir = dir.toFile();

		if (currentDir.isDirectory()) {
			for (File file : currentDir.listFiles()) {
				if (file.isFile() && file.getName().endsWith(StoreImage.STORE_IMAGE_FORMAT)) {
					long imageID = idFactory.getNextImageID();
					
					// Copy files to correct file with the image id number
					try {
						BufferedImage buffImg = ImageIO.read(file);
						if (buffImg == null)
							continue;
					
						db.setImageMapping(file.getName().substring(0, 
								file.getName().length() - StoreImage.STORE_IMAGE_FORMAT.length() - 1), 
								imageID, ImageSize.FULL);
						
						StoreImage img = new StoreImage(imageID, buffImg, ImageSize.FULL);
						preCacheImg.add(img);
						Files.write(directory.resolve(String.valueOf(imageID)), img.getByteArray(), 
								StandardOpenOption.CREATE, 
								StandardOpenOption.WRITE, 
								StandardOpenOption.TRUNCATE_EXISTING);
					} catch (IOException e) {
						
					}
					// Increment to have correct number of images for the limited drive storage
					nrOfImagesPreExisting++; 
				}
			}
		}
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
	
	public void deleteAllCreatedData() {
		deleteUnusedImages(new ArrayList<>());
	}
	
	public void finalizeSetup() {
		Predicate<StoreImage> storagePredicate = null;
		switch (storageRule) {
			case ALL: storagePredicate = new StoreAll<StoreImage>(); break;
			case FULL_SIZE_IMG: storagePredicate = new StoreLargeImages(); break;
			default: storagePredicate = new StoreAll<StoreImage>(); break;
		}
		
		IDataStorage<StoreImage> storage = null;
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

		ImageProvider provider = ImageProvider.getInstance();
		provider.setImageDB(imgDB);
		provider.setImageCreatorRunner(imgCreatorRunner);
		if (cache == null) {
			provider.setStorage(storage);
		} else {
			for (StoreImage i : preCacheImg)
				cache.cacheData(i);
			provider.setStorage(cache);
		}
	}
	
	public Path getWorkingDir() {
		return workingDir;
	}
	
	private void deleteUnusedImages() {
		deleteUnusedImages(new ArrayList<>());
	}
	
	private void deleteUnusedImages(List<Long> imagesToKeep) {
		File currentDir = workingDir.toFile();
		
		if (currentDir.isDirectory()) {
			for (File file : currentDir.listFiles()) {
				if (file.isFile() && !imagesToKeep.contains(Long.parseLong(file.getName()))) {
					file.delete();
				}
			}
			currentDir.delete();
		}
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
					} catch (InterruptedException e) {

					}
				}

				imgDB = new ImageDB();
				deleteUnusedImages();
				detectPreExistingImages();
				generateImages();
				finalizeSetup();	
			}
		};
		x.start();
	}
	
}
