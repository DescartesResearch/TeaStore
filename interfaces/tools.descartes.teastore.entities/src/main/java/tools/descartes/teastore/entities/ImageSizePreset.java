package tools.descartes.teastore.entities;

/**
 * 
 * @author Simon
 *
 */
public enum ImageSizePreset {
  /**
   * Website icon.
   */
	ICON(new ImageSize(64, 64)),
	/**
	 * Developer portraits.
	 */
	PORTRAIT(new ImageSize(160, 240)),
	/**
	 * Descartes logo.
	 */
	LOGO(new ImageSize(600, 195)),
	/**
	 * Main image.
	 */
	MAIN_IMAGE(new ImageSize(400, 310)),
	/**
	 * preview image size.
	 */
	PREVIEW(new ImageSize(64, 64)),
	/**
	 * Recommender image.
	 */
	RECOMMENDATION(new ImageSize(125, 125)),
	/**
	 * Full size product image.
	 */
	FULL(new ImageSize(300, 300)),
	/**
	 * Error images.
	 */
	ERROR(new ImageSize(600, 400)),
	/**
	 * Index image sizes.
	 */
	INDEX(new ImageSize(600, 450));

  /**
   * Set standard image size to FULL.
   */
	public static final ImageSize STD_IMAGE_SIZE = FULL.getSize();
	
	/**
	 * Image size.
	 */
	private final ImageSize size;
	
	/**
	 * Constructor.
	 * @param size ImageSize
	 */
	private ImageSizePreset(ImageSize size) {
		this.size = size;
	}
	
	/**
	 * Getter for image size.
	 * @return image size
	 */
	public ImageSize getSize() {
		return size;
	}
}
