package tools.descartes.teastore.image.cache.entry;

import tools.descartes.teastore.image.StoreImage;

public class AbstractEntryWrapper extends AbstractEntry<StoreImage> {

  public AbstractEntryWrapper(StoreImage data) {
    super(data);
  }

  @Override
  public void wasUsed() {

  }

}
