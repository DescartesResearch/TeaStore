package tools.descartes.teastore.image.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.teastore.image.StoreImage;
import tools.descartes.teastore.image.cache.entry.ICacheEntry;
import tools.descartes.teastore.image.storage.IDataStorage;

public class TestAbstractCache {

  private ArrayList<ICacheEntry<StoreImage>> entries;

  @Mock
  private StoreImage c0;
  @Mock
  private StoreImage c1;
  @Mock
  private StoreImage c2;
  @Mock
  private StoreImage c3;
  @Mock
  private StoreImage c4;
  @Mock
  private IDataStorage<StoreImage> storageAll;
  @Mock
  private IDataStorage<StoreImage> storageNone;

  @Before
  public void initialize() {
    MockitoAnnotations.openMocks(this);
    when(c0.getId()).thenReturn(0L);
    when(c0.getByteSize()).thenReturn(1000L);
    when(c1.getId()).thenReturn(1L);
    when(c1.getByteSize()).thenReturn(2000L);
    when(c2.getId()).thenReturn(2L);
    when(c2.getByteSize()).thenReturn(3000L);
    when(c3.getId()).thenReturn(3L);
    when(c3.getByteSize()).thenReturn(4000L);
    when(c4.getId()).thenReturn(4L);
    when(c4.getByteSize()).thenReturn(1000L, 1000L, 1000L, 3000L);
    when(storageAll.dataExists(anyLong())).thenReturn(false);
    when(storageAll.dataExists(0)).thenReturn(true);
    when(storageAll.dataExists(1)).thenReturn(true);
    when(storageAll.dataExists(2)).thenReturn(true);
    when(storageAll.dataExists(3)).thenReturn(true);
    when(storageAll.loadData(anyLong())).thenReturn(null);
    when(storageAll.loadData(0)).thenReturn(c0);
    when(storageAll.loadData(1)).thenReturn(c1);
    when(storageAll.loadData(2)).thenReturn(c2);
    when(storageAll.loadData(3)).thenReturn(c3);
    when(storageAll.saveData(any())).thenReturn(true);
    when(storageAll.saveData(c3)).thenReturn(false);
    when(storageAll.dataIsStorable(any())).thenReturn(true);
    when(storageAll.deleteData(any())).thenReturn(false);
    when(storageAll.deleteData(c0)).thenReturn(true, false);
    when(storageAll.deleteData(c1)).thenReturn(true, false);
    when(storageAll.deleteData(c2)).thenReturn(true, false);
    when(storageAll.deleteData(c3)).thenReturn(true, false);
    when(storageNone.dataIsStorable(any())).thenReturn(false);

    entries = new ArrayList<>();
  }

  @Test
  public void testConstructor() {
    new AbstractCacheWrapper(entries, storageAll, 1, predicate -> true);
  }

  @Test
  public void testConstructorStorageNull() {
    new AbstractCacheWrapper(entries, null, 1, predicate -> true);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorCollectionNull() {
    new AbstractCacheWrapper(null, storageAll, 1, predicate -> true);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorPredicateNull() {
    new AbstractCacheWrapper(entries, storageAll, 1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorZeroSize() {
    new AbstractCacheWrapper(entries, storageAll, 0, predicate -> true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNegativeSize() {
    new AbstractCacheWrapper(entries, storageAll, -1, predicate -> true);
  }

  @Test
  public void testGetMaxCacheSize() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll,
        IDataCache.STD_MAX_CACHE_SIZE, predicate -> true);
    assertEquals(IDataCache.STD_MAX_CACHE_SIZE, uut.getMaxCacheSize());
    uut = new AbstractCacheWrapper(entries, storageAll, 1234567890, predicate -> true);
    assertEquals(1234567890, uut.getMaxCacheSize());
  }

  @Test
  public void testGetCurrentCacheSize() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll,
        IDataCache.STD_MAX_CACHE_SIZE, predicate -> true);
    assertEquals(0, uut.getCurrentCacheSize());
    uut.cacheData(c0);
    assertEquals(c0.getByteSize(), uut.getCurrentCacheSize());
    uut.cacheData(c1);
    assertEquals(c0.getByteSize() + c1.getByteSize(), uut.getCurrentCacheSize());
  }

  @Test
  public void testGetFreeSpace() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll,
        IDataCache.STD_MAX_CACHE_SIZE, predicate -> true);
    assertEquals(IDataCache.STD_MAX_CACHE_SIZE, uut.getFreeSpace());
    uut.cacheData(c0);
    assertEquals(IDataCache.STD_MAX_CACHE_SIZE - c0.getByteSize(), uut.getFreeSpace());
    uut.cacheData(c1);
    assertEquals(IDataCache.STD_MAX_CACHE_SIZE - (c0.getByteSize() + c1.getByteSize()),
        uut.getFreeSpace());
  }

  @Test
  public void testHasStorageFor() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, null, 6500, predicate -> true);
    assertTrue(uut.hasStorageFor(c0.getByteSize()));
    assertTrue(uut.hasStorageFor(c1.getByteSize()));
    assertTrue(uut.hasStorageFor(c2.getByteSize()));
    assertTrue(uut.hasStorageFor(c3.getByteSize()));
    uut.cacheData(c0);
    uut.cacheData(c1);
    assertTrue(uut.hasStorageFor(c0.getByteSize()));
    assertTrue(uut.hasStorageFor(c1.getByteSize()));
    assertTrue(uut.hasStorageFor(c2.getByteSize()));
    assertFalse(uut.hasStorageFor(c3.getByteSize()));
    uut.cacheData(c2);
    assertFalse(uut.hasStorageFor(c0.getByteSize()));
    assertFalse(uut.hasStorageFor(c1.getByteSize()));
    assertFalse(uut.hasStorageFor(c2.getByteSize()));
    assertFalse(uut.hasStorageFor(c3.getByteSize()));
  }

  @Test
  public void testUncacheData() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll, 20000,
        predicate -> true);
    uut.uncacheData(c0);
    assertFalse(uut.dataIsInCache(c0.getId()));
    uut.cacheData(c0);
    uut.cacheData(c1);
    uut.cacheData(c2);
    uut.cacheData(c3);

    assertTrue(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertTrue(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));

    uut.uncacheData(c2);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));

    uut.uncacheData(c2);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));

    uut.uncacheData(c3);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertFalse(uut.dataIsInCache(c3.getId()));

    entries.clear();
    uut = new AbstractCacheWrapper(entries, storageAll, 2000, predicate -> true);
    uut.cacheData(c4);
    uut.uncacheData(c4);
    assertEquals(0, uut.getCurrentCacheSize());
  }

  @Test
  public void testDataIsCachable() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll, 20000,
        predicate -> true);
    assertTrue(uut.dataIsCachable(c0));
    assertTrue(uut.dataIsCachable(c1));
    assertTrue(uut.dataIsCachable(c2));
    assertTrue(uut.dataIsCachable(c3));

    entries.clear();
    uut = new AbstractCacheWrapper(entries, storageAll, 20000, predicate -> false);
    assertFalse(uut.dataIsCachable(c0));
    assertFalse(uut.dataIsCachable(c1));
    assertFalse(uut.dataIsCachable(c2));
    assertFalse(uut.dataIsCachable(c3));

    entries.clear();
    uut = new AbstractCacheWrapper(entries, storageAll, 20000,
        predicate -> predicate.getId() == c1.getId() || predicate.getId() == c3.getId());
    assertFalse(uut.dataIsCachable(c0));
    assertTrue(uut.dataIsCachable(c1));
    assertFalse(uut.dataIsCachable(c2));
    assertTrue(uut.dataIsCachable(c3));
  }

  @Test
  public void testDataIsInCache() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll, 20000,
        predicate -> true);
    uut.cacheData(c0);
    uut.cacheData(c1);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertFalse(uut.dataIsInCache(c2.getId()));
    assertFalse(uut.dataIsInCache(c3.getId()));
  }

  @Test
  public void testClearCache() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll, 20000,
        predicate -> true);
    uut.clearCache();
    uut.cacheData(c0);
    uut.cacheData(c1);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    uut.clearCache();
    assertFalse(uut.dataIsInCache(c0.getId()));
    assertFalse(uut.dataIsInCache(c1.getId()));
  }

  @Test
  public void testSetMaxCacheSize() {
    final long startSize = 1100;
    final long newSize = 5000;
    final long smallSize = 1000;
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll, startSize,
        predicate -> true);
    assertEquals(startSize, uut.getMaxCacheSize());

    uut.cacheData(c0);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertEquals(startSize, uut.getMaxCacheSize());

    assertTrue(uut.setMaxCacheSize(newSize));
    assertEquals(newSize, uut.getMaxCacheSize());
    uut.cacheData(c1);
    assertTrue(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));

    assertTrue(uut.setMaxCacheSize(smallSize));
    assertEquals(smallSize, uut.getMaxCacheSize());
    assertFalse(uut.dataIsInCache(c0.getId()));
    assertFalse(uut.dataIsInCache(c1.getId()));

  }

  @Test
  public void testDataExists() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, null, 20000, predicate -> true);
    assertFalse(uut.dataExists(c0.getId()));

    uut.cacheData(c0);
    uut.cacheData(c2);
    assertTrue(uut.dataExists(c0.getId()));
    assertFalse(uut.dataExists(c1.getId()));
    assertTrue(uut.dataExists(c2.getId()));
    assertFalse(uut.dataExists(c3.getId()));

    entries.clear();
    uut = new AbstractCacheWrapper(entries, storageAll, 20000, predicate -> true);
    assertTrue(uut.dataExists(c0.getId()));
    assertTrue(uut.dataExists(c1.getId()));
    assertTrue(uut.dataExists(c2.getId()));
    assertTrue(uut.dataExists(c3.getId()));
  }

  @Test
  public void testLoadData() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, null, 20000, predicate -> true);

    uut.cacheData(c0);
    uut.cacheData(c2);
    assertEquals(c0, uut.loadData(c0.getId()));
    assertEquals(null, uut.loadData(c1.getId()));
    assertEquals(c2, uut.loadData(c2.getId()));
    assertEquals(null, uut.loadData(c3.getId()));

    entries.clear();
    uut = new AbstractCacheWrapper(entries, storageAll, 20000, predicate -> true);
    assertEquals(c0, uut.loadData(c0.getId()));
    assertEquals(c1, uut.loadData(c1.getId()));
    assertEquals(c2, uut.loadData(c2.getId()));
    assertEquals(c3, uut.loadData(c3.getId()));
  }

  @Test
  public void testSaveData() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll, 20000,
        predicate -> true);
    assertTrue(uut.saveData(c0));
    assertTrue(uut.saveData(c1));
    assertTrue(uut.saveData(c2));
    assertFalse(uut.saveData(c3));

    assertTrue(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertTrue(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));

    assertFalse(uut.saveData(null));

    entries.clear();
    uut = new AbstractCacheWrapper(entries, null, 20000, predicate -> true);
    assertFalse(uut.saveData(c0));
    assertFalse(uut.saveData(c1));
    assertFalse(uut.saveData(c2));
    assertFalse(uut.saveData(c3));

    assertTrue(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertTrue(uut.dataIsInCache(c2.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));

    assertFalse(uut.saveData(null));
  }

  @Test
  public void testDataIsStorable() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll, 20000,
        predicate -> true);
    assertTrue(uut.dataIsStorable(c0));
    assertTrue(uut.dataIsStorable(null));

    entries.clear();
    uut = new AbstractCacheWrapper(entries, storageNone, 20000, predicate -> true);
    assertFalse(uut.dataIsStorable(c0));
    assertFalse(uut.dataIsStorable(null));
  }

  @Test
  public void testDeleteData() {
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll, 20000,
        predicate -> true);
    uut.cacheData(c0);
    uut.cacheData(c1);

    assertTrue(uut.deleteData(c0));
    assertFalse(uut.deleteData(c0));
    assertTrue(uut.deleteData(c1));
    assertFalse(uut.deleteData(c1));
  }

  @Test
  public void testCacheData() {
    // Standard caching behavior
    AbstractCacheWrapper uut = new AbstractCacheWrapper(entries, storageAll, 6500,
        predicate -> predicate != null);
    uut.cacheData(c0);
    assertTrue(uut.dataIsInCache(c0.getId()));
    uut.cacheData(c1);
    assertTrue(uut.dataIsInCache(c1.getId()));
    uut.cacheData(null);

    uut.cacheData(c3);
    assertFalse(uut.dataIsInCache(c0.getId()));
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));

    // Caching duplicate
    uut.cacheData(c3);
    assertTrue(uut.dataIsInCache(c1.getId()));
    assertTrue(uut.dataIsInCache(c3.getId()));

    // Test cache that denies all entries
    entries.clear();
    uut = new AbstractCacheWrapper(entries, storageAll, 6500, predicate -> false);
    uut.cacheData(c0);
    assertFalse(uut.dataIsInCache(c0.getId()));
    uut.cacheData(c1);
    assertFalse(uut.dataIsInCache(c1.getId()));

    // Test not caching items larger than the max cache size (should not modify
    // already cached data)
    entries.clear();
    uut = new AbstractCacheWrapper(entries, storageAll, 1000, predicate -> true);
    uut.cacheData(c3);
    assertFalse(uut.dataIsInCache(c3.getId()));
  }
}
