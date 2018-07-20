package tools.descartes.teastore.image.cache.entry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.teastore.image.StoreImage;
import tools.descartes.teastore.image.cache.entry.AbstractEntry;

public class TestAbstractEntry {

  private final long mockedByteSize = 5000;
  private final long mockedID = 1234567890;

  @Mock
  private StoreImage mockedImg;
  @Mock
  private StoreImage mockedImgNotEqual;
  @Mock
  private AbstractEntry<StoreImage> nullEntry;

  @Before
  public void initialize() {
    MockitoAnnotations.initMocks(this);
    when(mockedImg.getByteSize()).thenReturn(mockedByteSize);
    when(mockedImg.getId()).thenReturn(mockedID);
    when(mockedImgNotEqual.getByteSize()).thenReturn(300L);
    when(mockedImgNotEqual.getId()).thenReturn(9876543210L);
    when(nullEntry.getData()).thenReturn(null);
  }

  @Test
  public void testConstructor() {
    new AbstractEntryWrapper(mockedImg);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorNull() {
    new AbstractEntryWrapper(null);
  }

  @Test
  public void testUseCount() {
    AbstractEntryWrapper uut = new AbstractEntryWrapper(mockedImg);
    uut.wasUsed();
  }

  @Test
  public void testGetData() {
    AbstractEntryWrapper uut = new AbstractEntryWrapper(mockedImg);
    assertEquals(mockedImg, uut.getData());
  }

  @Test
  public void testGetByteSize() {
    AbstractEntryWrapper uut = new AbstractEntryWrapper(mockedImg);
    assertEquals(mockedByteSize, uut.getByteSize());
  }

  @Test
  public void testGetID() {
    AbstractEntryWrapper uut = new AbstractEntryWrapper(mockedImg);
    assertEquals(mockedID, uut.getId());
  }

  @Test
  public void testEquals() {
    AbstractEntryWrapper uut = new AbstractEntryWrapper(mockedImg);
    AbstractEntryWrapper uut2 = new AbstractEntryWrapper(mockedImgNotEqual);
    assertTrue(uut.equals(uut));
    assertFalse(uut.equals(uut2));
    assertFalse(uut.equals(null));
    assertFalse(uut.equals(nullEntry));
  }
}
