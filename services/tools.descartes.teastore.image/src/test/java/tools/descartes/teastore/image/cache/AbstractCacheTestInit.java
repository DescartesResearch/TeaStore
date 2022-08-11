package tools.descartes.teastore.image.cache;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.teastore.image.storage.IDataStorage;

public class AbstractCacheTestInit {

  protected DummyData c0 = new DummyData(0L, 1000L);
  protected DummyData c1 = new DummyData(1L, 2000L);
  protected DummyData c2 = new DummyData(2L, 3000L);
  protected DummyData c3 = new DummyData(3L, 4000L);
  @Mock
  protected IDataStorage<DummyData> storage;

  @Before
  public void initialize() {
    MockitoAnnotations.openMocks(this);
    // when(c0.getId()).thenReturn(0L);
    // when(c0.getByteSize()).thenReturn(1000L);
    // when(c0.equals(any())).thenReturn(false);
    // when(c0.equals(c0)).thenReturn(true);
    // when(c1.getId()).thenReturn(1L);
    // when(c1.getByteSize()).thenReturn(2000L);
    // when(c1.equals(any())).thenReturn(false);
    // when(c1.equals(c1)).thenReturn(true);
    // when(c2.getId()).thenReturn(2L);
    // when(c2.getByteSize()).thenReturn(3000L);
    // when(c2.equals(any())).thenReturn(false);
    // when(c2.equals(c2)).thenReturn(true);
    // when(c3.getId()).thenReturn(3L);
    // when(c3.getByteSize()).thenReturn(4000L);
    // when(c3.equals(any())).thenReturn(false);
    // when(c3.equals(c3)).thenReturn(true);
    when(storage.dataExists(anyLong())).thenReturn(false);
    when(storage.dataExists(0)).thenReturn(true);
    when(storage.dataExists(1)).thenReturn(true);
    when(storage.dataExists(2)).thenReturn(true);
    when(storage.dataExists(3)).thenReturn(true);
    when(storage.loadData(anyLong())).thenReturn(null);
    when(storage.loadData(0)).thenReturn(c0);
    when(storage.loadData(1)).thenReturn(c1);
    when(storage.loadData(2)).thenReturn(c2);
    when(storage.loadData(3)).thenReturn(c3);
    when(storage.saveData(any())).thenReturn(true);
    when(storage.saveData(c3)).thenReturn(false);
    when(storage.dataIsStorable(any())).thenReturn(true);
    when(storage.deleteData(any())).thenReturn(false);
    when(storage.deleteData(c0)).thenReturn(true, false);
    when(storage.deleteData(c1)).thenReturn(true, false);
    when(storage.deleteData(c2)).thenReturn(true, false);
    when(storage.deleteData(c3)).thenReturn(true, false);
  }

}
