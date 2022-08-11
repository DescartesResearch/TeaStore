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

public class TestAbstractEntry {

  private static final long MOCKED_BYTE_SIZE = 5000;
  private static final long MOCKED_ID = 1234567890;

  @Mock
  private StoreImage mockedImg;
  @Mock
  private StoreImage mockedImgNotEqual;
  @Mock
  private AbstractEntry<StoreImage> nullEntry;

  @Before
  public void initialize() {
    MockitoAnnotations.openMocks(this);
    when(mockedImg.getByteSize()).thenReturn(MOCKED_BYTE_SIZE);
    when(mockedImg.getId()).thenReturn(MOCKED_ID);
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
    assertEquals(MOCKED_BYTE_SIZE, uut.getByteSize());
  }

  @Test
  public void testGetID() {
    AbstractEntryWrapper uut = new AbstractEntryWrapper(mockedImg);
    assertEquals(MOCKED_ID, uut.getId());
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
