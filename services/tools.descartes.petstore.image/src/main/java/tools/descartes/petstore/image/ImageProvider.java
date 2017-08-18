package tools.descartes.petstore.image;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.stream.Collectors;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.image.setup.ImageCreatorRunner;
import tools.descartes.petstore.image.setup.ImageIDFactory;
import tools.descartes.petstore.image.setup.SetupController;
import tools.descartes.petstore.image.storage.IDataStorage;

public class ImageProvider {
	
	public static final String IMAGE_NOT_FOUND = "notFound";
	
	private final class Tupel {
		public final ImageDBKey id;
		public final String imgString;
		
		public Tupel(final ImageDBKey id, final String imgString) {
			this.id = id;
			this.imgString = imgString;
		}
		
		public ImageDBKey getKey() {
			return id;
		}
		
		public String getImgString() {
			return imgString;
		}
	}
	
	private static ImageProvider instance = new ImageProvider();
	private ImageDB db;
	private IDataStorage<StoreImage> storage;
	private ImageCreatorRunner imgCreatorRunner;
	
	private ImageProvider() {
		
	}
	
	public static ImageProvider getInstance() {
		return instance;
	}
	
	public void setImageDB(ImageDB imgDB) {
		db = imgDB;
	}
	
	public void setStorage(IDataStorage<StoreImage> imgStorage) {
		storage = imgStorage;
	}
	
	public void setImageCreatorRunner(ImageCreatorRunner runner) {
		imgCreatorRunner = runner;
	}

	private void waitForImageCreator() {
		if (imgCreatorRunner == null)
			SetupController.getInstance().finalizeSetup();
		if (imgCreatorRunner.isRunning()) {
			imgCreatorRunner.pause();
			try {
				Thread.sleep(imgCreatorRunner.getAvgCreationTime());
			} catch (InterruptedException e) {
				
			}
		}
	}

	public Map<Long, String> getProductImages(Map<Long, ImageSize> images) {
		waitForImageCreator();
		Map<Long, String> result = images.entrySet().stream()
				.map(a -> getImageFor(new ImageDBKey(a.getKey()), a.getValue()))
				.filter(a -> a != null && a.getImgString() != null)
				.collect(Collectors.toMap(t -> t.getKey().getProductID(), t -> t.getImgString()));
		imgCreatorRunner.resume();
		return result;
	}
	
	public Map<String, String> getWebUIImages(Map<String, ImageSize> images) {
		waitForImageCreator();
		Map<String, String> result = images.entrySet().stream()
				.map(a -> getImageFor(new ImageDBKey(a.getKey()), a.getValue()))
				.filter(a -> a != null && a.getImgString() != null)
				.collect(Collectors.toMap(t -> t.getKey().getWebUIName(), t -> t.getImgString()));
		imgCreatorRunner.resume();
		return result;
	}
	
	private StoreImage scaleAndRegisterImg(BufferedImage image, ImageDBKey key, ImageSize size) {
		StoreImage storedImg = new StoreImage(ImageIDFactory.getInstance().getNextImageID(), 
				ImageScaler.scale(image, size), size);
		db.setImageMapping(key, storedImg.getId(), size);
		storage.saveData(storedImg);
		return storedImg;
	}
	
	private Tupel getImageFor(ImageDBKey key, ImageSize size) {
		if (db == null || storage == null)
			return null;
		if (key == null || size == null)
			return null;
		if (!key.isProductKey() && (key.getWebUIName() == null || key.getWebUIName().isEmpty()))
			return null;
		
		// Try to retrieve image from disk or from cache
		long storedImgID = db.getImageID(key, size);
		StoreImage storedImg = storage.loadData(storedImgID);
		
		// If we dont have the image in the right size, get the biggest one and scale it
		if (storedImg == null) {
			storedImg = storage.loadData(db.getImageID(key, ImageSize.getBiggestSize()));
			if (storedImg != null) {
				storedImg = scaleAndRegisterImg(storedImg.getImage(), key, size);
			} else {
				storedImg = storage.loadData(db.getImageID(IMAGE_NOT_FOUND, size));
				if (storedImg == null) {
					storedImg = storage.loadData(db.getImageID(IMAGE_NOT_FOUND, ImageSize.getBiggestSize()));
					if (storedImg == null)
						return null;
					storedImg = scaleAndRegisterImg(storedImg.getImage(), new ImageDBKey(IMAGE_NOT_FOUND), size);
				}
			}
		}
		
		return new Tupel(key, storedImg.toString());
	}
	
//	public static void main(String[] args) {
////		HashMap<String, ImageSize> imgs = new HashMap<>();
////		imgs.put("front", ImageSize.MAIN_IMAGE);
////		imgs.put("icon", ImageSize.ICON);
////		
//		ImageProvider p = ImageProvider.getInstance();
////		long tb = System.currentTimeMillis();
////		Map<String, String> base = p.getWebUIImages(imgs);
////		long ta = System.currentTimeMillis();
////		Map<String, String> base2 = p.getWebUIImages(imgs);
////		long tc = System.currentTimeMillis();
////		Map<String, String> base3 = p.getWebUIImages(imgs);
////		long td = System.currentTimeMillis();
//
//		HashMap<Long, ImageSize> pImgs = new HashMap<>();
//		for (int i = 0; i <= 30; i++) {
//			pImgs.put((long)i, ImageSize.PREVIEW);
//		}
//		long tpb = System.currentTimeMillis();
//		for (int i = 0; i < 100; i++) {
//			Map<Long, String> base4 = p.getProductImages(pImgs);
//		}
//		long tpa = System.currentTimeMillis();	
//		Map<Long, String> base5 = p.getProductImages(pImgs);
//		long tpc = System.currentTimeMillis();	
//		
////		System.out.println("Time: " + (ta - tb));
////		System.out.println("Time2: " + (tc - ta));
////		System.out.println("Time3: " + (td - tc));
//		System.out.println("TimeP: " + ((tpa - tpb) / 100));
//		System.out.println("TimeP: " + (tpc - tpa));
////		System.out.println(base.size());
////		System.out.println(base2.size());
////		System.out.println(base.keySet());
////		System.out.println(base2.keySet());
////		System.out.println(base4.size());
////		System.out.println(base4.keySet());
////		System.out.println(base5.size());
////		System.out.println(base5.keySet());
//	}
}
