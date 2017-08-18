package tools.descartes.petstore.image.setup;

import java.util.concurrent.atomic.AtomicLong;

public final class ImageIDFactory {

	private static ImageIDFactory instance = new ImageIDFactory();
	private AtomicLong nextID = new AtomicLong(1);
	
	private ImageIDFactory() {

	}
	
	public static ImageIDFactory getInstance() {
		return instance;
	}
	
	public long getNextImageID() {
		return nextID.getAndIncrement();
	}
	
	public void startAtID(long nextID) {
		this.nextID.set(nextID);
	}
}
