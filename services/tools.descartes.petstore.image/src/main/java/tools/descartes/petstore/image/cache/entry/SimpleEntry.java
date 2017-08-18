package tools.descartes.petstore.image.cache.entry;

public class SimpleEntry<D extends ICachable<D>> extends AbstractEntry<D> {

	public SimpleEntry(D data) {
		super(data);
	}

	@Override
	public void wasUsed() {
		// There is nothing to do.
	}

}
