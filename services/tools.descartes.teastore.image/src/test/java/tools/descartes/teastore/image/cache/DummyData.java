package tools.descartes.teastore.image.cache;

import tools.descartes.teastore.image.cache.entry.ICachable;

public class DummyData implements ICachable<DummyData> {

  private long byteSize;
  private long id;

  public DummyData(long id, long byteSize) {
    this.byteSize = byteSize;
    this.id = id;
  }

  @Override
  public long getByteSize() {
    return byteSize;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DummyData other = (DummyData) obj;
    if (id != other.id) {
      return false;
    }
    return true;
  }

}
