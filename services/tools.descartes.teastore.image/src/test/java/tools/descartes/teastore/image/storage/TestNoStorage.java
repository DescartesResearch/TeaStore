package tools.descartes.teastore.image.storage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.teastore.image.StoreImage;

public class TestNoStorage {

  @Mock
  private StoreImage image;

  @Before
  public void initialize() {
    MockitoAnnotations.openMocks(this);
    when(image.getId()).thenReturn(0L);
  }

  @Test
  public void testDataExists() {
    NoStorage<StoreImage> uut = new NoStorage<>();
    assertFalse(uut.dataExists(image.getId()));
  }

  @Test
  public void testLoadData() {
    NoStorage<StoreImage> uut = new NoStorage<>();
    assertNull(uut.loadData(image.getId()));
  }

  @Test
  public void testSaveData() {
    NoStorage<StoreImage> uut = new NoStorage<>();
    assertFalse(uut.saveData(image));
  }

  @Test
  public void testDataIsStorable() {
    NoStorage<StoreImage> uut = new NoStorage<>();
    assertFalse(uut.dataIsStorable(image));
  }

  @Test
  public void testDeleteData() {
    NoStorage<StoreImage> uut = new NoStorage<>();
    assertFalse(uut.deleteData(image));
  }

}
