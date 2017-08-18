package tools.descartes.petstore.image.cache.entry;

public class CountedEntry<D extends ICachable<D>> extends AbstractEntry<D> {
	
	private int useCount = 0;
	
	public CountedEntry(D data) {
		super(data);
	}
	
	public int getUseCount() {
		return useCount;
	}
	
	@Override
	public void wasUsed() {
		this.useCount++;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + useCount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		CountedEntry other = (CountedEntry) obj;
		if (useCount != other.useCount)
			return false;
		return true;
	}

}
