package tools.descartes.petstore.image.cache.entry;

public class TimedEntry<D extends ICachable<D>> extends AbstractEntry<D> {

	private long time = 0;
	
	public TimedEntry(D data) {
		super(data);
		wasUsed();
	}
	
	public long getTime() {
		return time;
	}

	@Override
	public void wasUsed() {
		time = System.nanoTime();
	}
}
