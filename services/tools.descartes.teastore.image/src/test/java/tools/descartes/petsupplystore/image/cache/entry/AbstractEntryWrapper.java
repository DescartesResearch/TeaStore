package tools.descartes.petsupplystore.image.cache.entry;

import tools.descartes.petsupplystore.image.StoreImage;

public class AbstractEntryWrapper extends AbstractEntry<StoreImage> {

	public AbstractEntryWrapper(StoreImage data) {
		super(data);
	}

	@Override
	public void wasUsed() {
		
	}

}
