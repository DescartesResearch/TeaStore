package tools.descartes.teastore.image.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestFirstInFirstOut extends AbstractCacheTestInit {

  @Test
  public void testConstructorSimple() {
    new FirstInFirstOut<DummyData>();
  }

  @Test
  public void testConstructorSize() {
    new FirstInFirstOut<DummyData>(24 * 1024 * 1024);
    new FirstInFirstOut<DummyData>(1);
  }

  @Test
  public void testConstructorSizePredicate() {
    new FirstInFirstOut<DummyData>(1, predicate -> true);
  }

  @Test
  public void testConstructorStorageSizePredicate() {
    new FirstInFirstOut<DummyData>(storage, 1, predicate -> true);
  }

  @Test
  public void testConstructorStorageNull() {
    new FirstInFirstOut<DummyData>(null, 1, predicate -> true);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorPredicateNull() {
    new FirstInFirstOut<DummyData>(1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorZeroSize() {
    new FirstInFirstOut<DummyData>(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNegativeSize() {
    new FirstInFirstOut<DummyData>(-1);
  }

  @Test
  public void testCacheData() {
    // Standard caching behavior
    FirstInFirstOut<DummyData> uut = new FirstInFirstOut<>(storage, 6500,
        predicate -> predicate != null);
    uut.cacheData(c0);
    assertTrue(uut.dataIsInCache(c0.getId()));
    uut.cacheData(c1);
    assertTrue(uut.dataIsInCache(c1.getId()));
    uut.cacheData(c2);
    assertTrue(uut.dataIsInCache(c2.getId()));

    uut.cacheData(c3);
    assertFalse(uut.dataIsInCache(c0.getId()));
    assertFalse(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));
    uut.cacheData(c3);
    assertFalse(uut.dataIsInCache(c0.getId()));
    assertFalse(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));
    uut.cacheData(c0);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertFalse(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));
  }

}
