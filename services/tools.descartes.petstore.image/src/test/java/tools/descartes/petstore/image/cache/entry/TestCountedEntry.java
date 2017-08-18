package tools.descartes.petstore.image.cache.entry;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.petstore.image.StoreImage;

public class TestCountedEntry {

	private final long mockedByteSize = 5000;
	private final long mockedID = 1234567890;
	
	@Mock
	private StoreImage mockedImg; 

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		when(mockedImg.getByteSize()).thenReturn(mockedByteSize);
		when(mockedImg.getId()).thenReturn(mockedID);
	}
	
	@Test
	public void testConstructor() {
		new CountedEntry<>(mockedImg);
	}
	
	@Test(expected = NullPointerException.class)
	public void testConstructorNull() {
		new CountedEntry<StoreImage>(null);
	}

	@Test
	public void testUseCount() {
		CountedEntry<StoreImage> uut = new CountedEntry<>(mockedImg);
		assertEquals(0, uut.getUseCount());
		uut.wasUsed();
		assertEquals(1, uut.getUseCount());
		uut.wasUsed();
		uut.wasUsed();
		assertEquals(3, uut.getUseCount());
	}
	
	@Test
	public void testGetData() {
		CountedEntry<StoreImage> uut = new CountedEntry<>(mockedImg);
		assertEquals(mockedImg, uut.getData());
	}
	
	@Test
	public void testGetByteSize() {
		CountedEntry<StoreImage> uut = new CountedEntry<>(mockedImg);
		assertEquals(mockedByteSize, uut.getByteSize());
	}
	
	@Test
	public void testGetID() {
		CountedEntry<StoreImage> uut = new CountedEntry<>(mockedImg);
		assertEquals(mockedID, uut.getId());
	}
	
}
