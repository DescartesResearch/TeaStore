package tools.descartes.teastore.image.storage;

import tools.descartes.teastore.image.cache.entry.ICachable;

public class NoStorage<T extends ICachable<T>> implements IDataStorage<T> {

	@Override
	public boolean dataExists(long id) {
		return false;
	}

	@Override
	public T loadData(long id) {
		return null;
	}

	@Override
	public boolean saveData(T data) {
		return false;
	}

	@Override
	public boolean dataIsStorable(T data) {
		return false;
	}

	@Override
	public boolean deleteData(T data) {
		return false;
	}
	
}
