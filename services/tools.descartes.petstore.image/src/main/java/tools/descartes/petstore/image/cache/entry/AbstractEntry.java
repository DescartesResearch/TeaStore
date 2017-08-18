package tools.descartes.petstore.image.cache.entry;

public abstract class AbstractEntry<D extends ICachable<D>> {
	
	private D data;
	
	public AbstractEntry(D data) {
		if (data == null)
			throw new NullPointerException("Supplied data is null.");
		
		this.data = data;
	}

	public D getData() {
		return data;
	}
	
	public abstract void wasUsed();
	
	public long getId() {
		return data.getId();
	}
	
	public long getByteSize() {
		return data.getByteSize();
	}
	
}
