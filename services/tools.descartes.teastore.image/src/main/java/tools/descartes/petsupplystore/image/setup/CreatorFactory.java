package tools.descartes.petsupplystore.image.setup;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petsupplystore.image.ImageDB;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.ImageSize;
import tools.descartes.teastore.entities.ImageSizePreset;

public class CreatorFactory {

	private int shapesPerImage = 0;
	private ImageSize imgSize = ImageSizePreset.STD_IMAGE_SIZE;
	private Path workingDir = SetupController.SETUP.getWorkingDir();
	private Map<Category, BufferedImage> categoryImages;
	private List<Long> products;
	private List<Category> categories;
	private ImageDB imgDB;
	private AtomicLong nrOfImagesGenerated;	
	
	private final Logger log = LoggerFactory.getLogger(CreatorFactory.class);
	
	public CreatorFactory(int shapesPerImage, ImageDB imgDB, ImageSize imgSize, Path workingDir, 
			Map<Category, List<Long>> products, Map<Category, BufferedImage> categoryImages,
			AtomicLong nrOfImagesGenerated) {
		if (imgDB == null) {
			log.error("Supplied image database is null.");
			throw new NullPointerException("Supplied image database is null.");
		}
		if (products == null) {
			log.error("Supplied product map is null.");
			throw new NullPointerException("Supplied product map is null.");
		}
		if (nrOfImagesGenerated == null) {
			log.error("Supplied counter for images generated is null.");
			throw new NullPointerException("Supplied counter for images generated is null.");
		}
		
		if (workingDir == null) {
			log.info("Supplied working directory is null. Set to value {}.", SetupController.SETUP.getWorkingDir());
		} else {
			this.workingDir = workingDir;
		}
		if (categoryImages == null) {
			log.info("Supplied category images are null. Defaulting to not add category images.");
		} else {
			this.categoryImages = categoryImages;
		}
		if (imgSize == null) {
			log.info("Supplied image size is null. Defaulting to standard size of {}.", ImageSizePreset.STD_IMAGE_SIZE);
		} else {
			this.imgSize = imgSize;
		}
		if (shapesPerImage < 0) {
			log.info("Number of shapes per image cannot be below 0, was {}. Set to 0.", shapesPerImage);
		} else {
			this.shapesPerImage = shapesPerImage;
		}
		this.products = products.entrySet().stream()
				.flatMap(e -> e.getValue().stream())
				.collect(Collectors.toList());
		this.categories = products.entrySet().stream()
				.flatMap(e -> e.getValue().stream().map(x -> e.getKey()))
				.collect(Collectors.toList());
		this.imgDB = imgDB;
		this.nrOfImagesGenerated = nrOfImagesGenerated;
	}
	
	public Runnable newRunnable() {
		return new CreatorRunner(imgDB, imgSize, products.remove(0), shapesPerImage, 
				categoryImages.getOrDefault(categories.remove(0), null), workingDir, nrOfImagesGenerated);
	}

}
