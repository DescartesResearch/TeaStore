package tools.descartes.petsupplystore.image.cache;

import tools.descartes.petsupplystore.image.cache.entry.ICachable;

public class Dummy implements ICachable<Dummy> {
	
	private long id;
	
	public Dummy(long id) {
		this.id = id;
	}

	@Override
	public long getByteSize() {
		return 1L;
	}

	@Override
	public long getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object other) {
		// Assume we only get Dummy objects here.
		Dummy otherDummy = (Dummy)other;
		return otherDummy.getId() == this.id;
	}
}
