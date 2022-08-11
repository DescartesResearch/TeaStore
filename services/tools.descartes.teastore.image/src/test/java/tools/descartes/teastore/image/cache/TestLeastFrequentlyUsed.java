package tools.descartes.teastore.image.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class TestLeastFrequentlyUsed extends AbstractCacheTestInit {

  @Test
  public void testConstructorSimple() {
    new LeastFrequentlyUsed<DummyData>();
  }

  @Test
  public void testConstructorSize() {
    new LeastFrequentlyUsed<DummyData>(24 * 1024 * 1024);
    new LeastFrequentlyUsed<DummyData>(1);
  }

  @Test
  public void testConstructorSizePredicate() {
    new LeastFrequentlyUsed<DummyData>(1, predicate -> true);
  }

  @Test
  public void testConstructorStorageSizePredicate() {
    new LeastFrequentlyUsed<DummyData>(storage, 1, predicate -> true);
  }

  @Test
  public void testConstructorStorageNull() {
    new LeastFrequentlyUsed<DummyData>(null, 1, predicate -> true);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorPredicateNull() {
    new LeastFrequentlyUsed<DummyData>(1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorZeroSize() {
    new LeastFrequentlyUsed<DummyData>(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNegativeSize() {
    new LeastFrequentlyUsed<DummyData>(-1);
  }

  @Test
  public void testCacheData() {
    // Standard caching behavior
    LeastFrequentlyUsed<DummyData> uut = new LeastFrequentlyUsed<>(storage, 6500,
        predicate -> predicate != null);
    uut.cacheData(c0);
    assertTrue(uut.dataIsInCache(c0.getId()));
    uut.cacheData(c1);
    assertTrue(uut.dataIsInCache(c1.getId()));
    uut.cacheData(c2);
    assertTrue(uut.dataIsInCache(c2.getId()));
    uut.loadData(c0.getId());
    uut.loadData(c1.getId());
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
