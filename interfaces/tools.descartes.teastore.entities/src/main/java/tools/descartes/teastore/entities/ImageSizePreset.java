package tools.descartes.teastore.entities;


public enum ImageSizePreset {
	ICON(new ImageSize(64, 64)),
	PORTRAIT(new ImageSize(160, 240)),
	LOGO(new ImageSize(400, 310)),
	MAIN_IMAGE(new ImageSize(450, 450)),
	PREVIEW(new ImageSize(64, 64)),
	RECOMMENDATION(new ImageSize(125, 125)),
	FULL(new ImageSize(300, 300)),
	ERROR(new ImageSize(600, 400)),
	INDEX(new ImageSize(600, 450));

	public static final ImageSize STD_IMAGE_SIZE = FULL.getSize();
	
	private final ImageSize size;
	
	private ImageSizePreset(ImageSize size) {
		this.size = size;
	}
	
	public ImageSize getSize() {
		return size;
	}
	
//	public static ImageSize getBiggestPreset() {
//		return Arrays.asList(ImageSizePreset.values()).stream()
//				.map(i -> i.getSize())
//				.max((a, b) -> a.getPixelCount() - b.getPixelCount()).orElse(STD_IMAGE_SIZE);
//	}
}
