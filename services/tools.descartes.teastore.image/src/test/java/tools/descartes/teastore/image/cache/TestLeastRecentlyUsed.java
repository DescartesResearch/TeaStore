package tools.descartes.teastore.image.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestLeastRecentlyUsed extends AbstractCacheTestInit {

  @Test
  public void testConstructorSimple() {
    new LeastRecentlyUsed<DummyData>();
  }

  @Test
  public void testConstructorSize() {
    new LeastRecentlyUsed<DummyData>(24 * 1024 * 1024);
    new LeastRecentlyUsed<DummyData>(1);
  }

  @Test
  public void testConstructorSizePredicate() {
    new LeastRecentlyUsed<DummyData>(1, predicate -> true);
  }

  @Test
  public void testConstructorStorageSizePredicate() {
    new LeastRecentlyUsed<DummyData>(storage, 1, predicate -> true);
  }

  @Test
  public void testConstructorStorageNull() {
    new LeastRecentlyUsed<DummyData>(null, 1, predicate -> true);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorPredicateNull() {
    new LeastRecentlyUsed<DummyData>(1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorZeroSize() {
    new LeastRecentlyUsed<DummyData>(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNegativeSize() {
    new LeastRecentlyUsed<DummyData>(-1);
  }

  @Test
  public void testCacheData() {
    // Standard caching behavior
    LeastRecentlyUsed<DummyData> uut = new LeastRecentlyUsed<>(storage, 6500,
        predicate -> predicate != null);
    uut.cacheData(c0);
    assertTrue(uut.dataIsInCache(c0.getId()));
    uut.cacheData(c1);
    assertTrue(uut.dataIsInCache(c1.getId()));
    uut.cacheData(c2);
    assertTrue(uut.dataIsInCache(c2.getId()));
    uut.loadData(c2.getId());
    uut.loadData(c1.getId());

    uut.cacheData(c3);
    assertFalse(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));
    uut.cacheData(c3);
    assertFalse(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));
  }

}
