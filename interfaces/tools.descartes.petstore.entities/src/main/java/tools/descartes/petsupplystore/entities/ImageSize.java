package tools.descartes.petsupplystore.entities;

import java.util.Arrays;

/**
 * Available store image sizes.
 * @author Norbert Schmitt
 *
 */
public enum ImageSize {
	ICON(64, 64), PORTRAIT(160, 240), LOGO(600, 195), MAIN_IMAGE(450, 450), PREVIEW(125, 125), FULL(600, 600);
	
	public static final ImageSize STD_IMAGE_SIZE = FULL;
	public static final String IMAGE_SIZE_DIVIDER = ";";
	
	private final int width;
	private final int height;
	
	private ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getPixelCount() {
		return width * height;
	}
	
	public static ImageSize getBiggestSize() {
		return Arrays.asList(ImageSize.values()).stream()
				.max((a, b) -> a.getPixelCount() - b.getPixelCount()).orElse(STD_IMAGE_SIZE);
	}
}
