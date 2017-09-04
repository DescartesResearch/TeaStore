package tools.descartes.petsupplystore.entities;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Available store image sizes.
 * @author Norbert Schmitt
 *
 */
public class ImageSize {

	public enum Preset {
		ICON(new ImageSize(64, 64)),
		PORTRAIT(new ImageSize(160, 240)),
		LOGO(new ImageSize(600, 195)),
		MAIN_IMAGE(new ImageSize(450, 450)),
		PREVIEW(new ImageSize(125, 125)),
		FULL(new ImageSize(600, 600)),
		ERROR(new ImageSize(600, 400)),
		INDEX(new ImageSize(600, 450));
		
		private final ImageSize size;
		
		private Preset(ImageSize size) {
			this.size = size;
		}
		
		public ImageSize getSize() {
			return size;
		}
	}
	
	public static final ImageSize STD_IMAGE_SIZE = Preset.FULL.getSize();
	
	private int width;
	private int height;
	
	private final Logger log = LoggerFactory.getLogger(ImageSize.class);

	public ImageSize() {
		this(1, 1);
	}
	
	public ImageSize(int width, int height) {
		setWidth(width);
		setHeight(height);
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		if (width <= 0) {
			log.error("Image width cannot be zero or negative.");
			throw new IllegalArgumentException("Image width cannot be zero or negative.");
		}
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		if (height <= 0) {
			log.error("Image height cannot be zero or negative.");
			throw new IllegalArgumentException("Image height cannot be zero or negative.");
		}
		this.height = height;
	}
	
	public int getPixelCount() {
		return width * height;
	}
	
	public static ImageSize getBiggestPreset() {
		return Arrays.asList(ImageSize.Preset.values()).stream()
				.map(i -> i.getSize())
				.max((a, b) -> a.getPixelCount() - b.getPixelCount()).orElse(STD_IMAGE_SIZE);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		ImageSize other = (ImageSize) obj;
		if (height != other.height) {
			return false;
		}
		if (width != other.width) {
			return false;
		}
		return true;
	}
}
