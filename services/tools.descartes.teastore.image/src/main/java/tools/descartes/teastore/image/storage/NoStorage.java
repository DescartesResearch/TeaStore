package tools.descartes.teastore.image.storage;

import tools.descartes.teastore.image.cache.entry.ICachable;

/**
 * This is a dummy implementations to allows the image provider to be used as a cache only.
 * @author Norbert Schmitt
 *
 * @param <T> Entry Type implementing ICachable.
 */
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
