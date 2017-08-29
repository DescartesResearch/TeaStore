package tools.descartes.petsupplystore.image.setup;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.image.ImageDB;
import tools.descartes.petsupplystore.image.StoreImage;

public class CreatorRunner implements Runnable {

	long productID;
	long seed;
	ImageDB imgDB;
	ImageSize size = ImageSize.STD_IMAGE_SIZE;
	Path workingDir;
	int shapesPerImage;
	BufferedImage categoryImage;
	Logger log = LoggerFactory.getLogger(CreatorRunner.class);
	
	public CreatorRunner(ImageDB imgDB, ImageSize size, long productID, int shapesPerImage, 
			BufferedImage categoryImage, long seed, Path workingDir) {
		this.imgDB = imgDB;
		this.size = size;
		this.productID = productID;
		this.shapesPerImage = shapesPerImage;
		this.categoryImage = categoryImage;
		this.seed = seed;
		this.workingDir = workingDir;
	}
	
	@Override
	public void run() {
		long imgID = ImageIDFactory.ID.getNextImageID();
		Random rand = new Random(seed);
	
		// All products must be added to the database
		imgDB.setImageMapping(productID, imgID, size);

		// Resolve path and create a new image
		Path imgFile = workingDir.resolve(String.valueOf(imgID));
		
		BufferedImage img = ImageCreator.createImage(shapesPerImage, categoryImage, size, rand);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		try {
			ImageIO.write(img, StoreImage.STORE_IMAGE_FORMAT, stream);
			Files.write(imgFile, Base64.getEncoder().encode(stream.toByteArray()), 
					StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException ioException) {
			log.warn("An IOException occured while writing image with ID " + String.valueOf(imgID) + " to file "
					+ imgFile.toAbsolutePath() + ".", ioException);
		}
	}

}
