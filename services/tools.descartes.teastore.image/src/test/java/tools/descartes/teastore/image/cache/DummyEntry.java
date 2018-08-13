package tools.descartes.teastore.image.cache;

import tools.descartes.teastore.image.StoreImage;
import tools.descartes.teastore.image.cache.entry.ICacheEntry;

public final class DummyEntry implements ICacheEntry<StoreImage> {

  private final StoreImage data;

  public DummyEntry(StoreImage data) {
    this.data = data;
  }

  @Override
  public void wasUsed() {
  }

  @Override
  public long getByteSize() {
    return data.getByteSize();
  }

  @Override
  public long getId() {
    return data.getId();
  }

  @Override
  public StoreImage getData() {
    return data;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DummyEntry other = (DummyEntry) obj;
    if (data == null) {
      if (other.data != null)
        return false;
    } else if (data.getId() != other.data.getId())
      return false;
    return true;
  }

}
