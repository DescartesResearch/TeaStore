package tools.descartes.petsupplystore.image.setup;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petsupplystore.image.ImageDB;
import tools.descartes.petsupplystore.image.StoreImage;
import tools.descartes.teastore.entities.ImageSize;
import tools.descartes.teastore.entities.ImageSizePreset;

public class CreatorRunner implements Runnable {

	private final long productID;
	private final ImageDB imgDB;
	private final ImageSize size;
	private final Path workingDir;
	private final int shapesPerImage;
	private final BufferedImage categoryImage;
	private final AtomicLong nrOfImagesGenerated;	
	
	private final Logger log = LoggerFactory.getLogger(CreatorRunner.class);
	
	public CreatorRunner(ImageDB imgDB, ImageSize size, long productID, int shapesPerImage, 
			BufferedImage categoryImage, Path workingDir, AtomicLong nrOfImagesGenerated) {
		this.imgDB = imgDB;
		this.productID = productID;
		this.shapesPerImage = shapesPerImage;
		this.categoryImage = categoryImage;
		this.workingDir = workingDir;
		this.nrOfImagesGenerated = nrOfImagesGenerated;
		if (size != null) {
			this.size = size;
		} else {
			this.size = ImageSizePreset.STD_IMAGE_SIZE;
		} 
	}
	
	@Override
	public void run() {
		long imgID = ImageIDFactory.ID.getNextImageID();
		Random rand = new Random(productID);
		
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
			if (!(ioException instanceof ClosedByInterruptException)) {
				log.warn("An IOException occured while writing image with ID " + String.valueOf(imgID) + " to file "
						+ imgFile.toAbsolutePath() + ".", ioException);
			} else {
				log.warn("An exception was thrown during image creation with ID " + String.valueOf(imgID) + " to file "
						+ imgFile.toAbsolutePath() + ".", ioException);
			}
		}
		
		nrOfImagesGenerated.incrementAndGet();
	}

}
