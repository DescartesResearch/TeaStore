package tools.descartes.teastore.image.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestLastInFirstOut extends AbstractCacheTestInit {

  @Test
  public void testConstructorSimple() {
    new LastInFirstOut<DummyData>();
  }

  @Test
  public void testConstructorSize() {
    new LastInFirstOut<DummyData>(24 * 1024 * 1024);
    new LastInFirstOut<DummyData>(1);
  }

  @Test
  public void testConstructorSizePredicate() {
    new LastInFirstOut<DummyData>(1, predicate -> true);
  }

  @Test
  public void testConstructorStorageSizePredicate() {
    new LastInFirstOut<DummyData>(storage, 1, predicate -> true);
  }

  @Test
  public void testConstructorStorageNull() {
    new LastInFirstOut<DummyData>(null, 1, predicate -> true);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorPredicateNull() {
    new LastInFirstOut<DummyData>(1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorZeroSize() {
    new LastInFirstOut<DummyData>(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNegativeSize() {
    new LastInFirstOut<DummyData>(-1);
  }

  @Test
  public void testCacheData() {
    // Standard caching behavior
    LastInFirstOut<DummyData> uut = new LastInFirstOut<>(storage, 6500,
        predicate -> predicate != null);
    uut.cacheData(c0);
    assertTrue(uut.dataIsInCache(c0.getId()));
    uut.cacheData(c1);
    assertTrue(uut.dataIsInCache(c1.getId()));
    uut.cacheData(c2);
    assertTrue(uut.dataIsInCache(c2.getId()));

    uut.cacheData(c3);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertFalse(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));
    uut.cacheData(c3);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertFalse(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));
    uut.cacheData(c1);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertFalse(uut.dataIsInCache(c3.getId()));
  }

}
