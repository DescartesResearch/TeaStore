package tools.descartes.teastore.image.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;


public class TestRandomReplacement extends AbstractCacheTestInit {

  @Test
  public void testConstructorSimple() {
    new RandomReplacement<DummyData>();
  }

  @Test
  public void testConstructorSize() {
    new RandomReplacement<DummyData>(24 * 1024 * 1024);
    new RandomReplacement<DummyData>(1);
  }

  @Test
  public void testConstructorSizePredicate() {
    new RandomReplacement<DummyData>(1, predicate -> true);
  }

  @Test
  public void testConstructorStorageSizePredicate() {
    new RandomReplacement<DummyData>(storage, 1, predicate -> true);
  }

  @Test
  public void testConstructorStorageSizePredicateSeed() {
    new RandomReplacement<DummyData>(storage, 1, predicate -> true, 800);
    new RandomReplacement<DummyData>(storage, 1, predicate -> true, -123);
    new RandomReplacement<DummyData>(storage, 1, predicate -> true, 0);
  }

  @Test
  public void testConstructorStorageNull() {
    new RandomReplacement<DummyData>(null, 1, predicate -> true);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorPredicateNull() {
    new RandomReplacement<DummyData>(1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorZeroSize() {
    new RandomReplacement<DummyData>(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNegativeSize() {
    new RandomReplacement<DummyData>(-1);
  }

  @Test
  public void testCacheData() {
    long seed = 800L;
    Random rand = new Random(seed);
    // Standard caching behavior
    RandomReplacement<DummyData> uut = new RandomReplacement<>(storage, 6500,
        predicate -> predicate != null, seed);
    uut.cacheData(c0);
    assertTrue(uut.dataIsInCache(c0.getId()));
    uut.cacheData(c1);
    assertTrue(uut.dataIsInCache(c1.getId()));
    uut.cacheData(c2);
    assertTrue(uut.dataIsInCache(c2.getId()));

    ArrayList<DummyData> cacheValues = new ArrayList<>();
    cacheValues.add(c0);
    cacheValues.add(c1);
    cacheValues.add(c2);

    uut.cacheData(c3);
    determineReplacement(cacheValues, rand, c3);
    assertEquals(cacheValues.contains(c0), uut.dataIsInCache(c0.getId()));
    assertEquals(cacheValues.contains(c1), uut.dataIsInCache(c1.getId()));
    assertEquals(cacheValues.contains(c2), uut.dataIsInCache(c2.getId()));
    assertEquals(cacheValues.contains(c3), uut.dataIsInCache(c3.getId()));
    uut.cacheData(c3);
    assertEquals(cacheValues.contains(c0), uut.dataIsInCache(c0.getId()));
    assertEquals(cacheValues.contains(c1), uut.dataIsInCache(c1.getId()));
    assertEquals(cacheValues.contains(c2), uut.dataIsInCache(c2.getId()));
    assertEquals(cacheValues.contains(c3), uut.dataIsInCache(c3.getId()));
    uut.cacheData(c0);
    determineReplacement(cacheValues, rand, c0);
    assertEquals(cacheValues.contains(c0), uut.dataIsInCache(c0.getId()));
    assertEquals(cacheValues.contains(c1), uut.dataIsInCache(c1.getId()));
    assertEquals(cacheValues.contains(c2), uut.dataIsInCache(c2.getId()));
    assertEquals(cacheValues.contains(c3), uut.dataIsInCache(c3.getId()));
  }

  private void determineReplacement(ArrayList<DummyData> cacheValues, Random rand, DummyData data) {
    long size = 0;
    while (size < data.getByteSize()) {
      int nextElement = rand.nextInt(cacheValues.size());
      size += cacheValues.get(nextElement).getByteSize();
      cacheValues.remove(nextElement);
    }
    cacheValues.add(data);
  }

}
