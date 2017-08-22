package tools.descartes.petstore.image.storage;

import tools.descartes.petstore.image.cache.entry.ICachable;

public class NoStorage<T extends ICachable<T>> implements IDataStorage<T> {

	@Override
	public boolean dataExists(long id) {
		return true;
	}

	@Override
	public T loadData(long id) {
		return null;
	}

	@Override
	public boolean saveData(T data) {
		return true;
	}

	@Override
	public boolean dataIsStorable(T data) {
		return true;
	}

	@Override
	public boolean deleteData(T data) {
		return true;
	}
	
}
