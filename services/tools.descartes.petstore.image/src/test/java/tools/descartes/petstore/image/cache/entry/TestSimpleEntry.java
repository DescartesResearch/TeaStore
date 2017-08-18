package tools.descartes.petstore.image.cache.entry;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.petstore.image.StoreImage;

public class TestSimpleEntry {
	
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
		new SimpleEntry<>(mockedImg);
	}
	
	@Test(expected = NullPointerException.class)
	public void testConstructorNull() {
		new SimpleEntry<StoreImage>(null);
	}

	@Test
	public void testUseCount() {
		SimpleEntry<StoreImage> uut = new SimpleEntry<>(mockedImg);
		uut.wasUsed();
	}
	
	@Test
	public void testGetData() {
		SimpleEntry<StoreImage> uut = new SimpleEntry<>(mockedImg);
		assertEquals(mockedImg, uut.getData());
	}
	
	@Test
	public void testGetByteSize() {
		SimpleEntry<StoreImage> uut = new SimpleEntry<>(mockedImg);
		assertEquals(mockedByteSize, uut.getByteSize());
	}
	
	@Test
	public void testGetID() {
		SimpleEntry<StoreImage> uut = new SimpleEntry<>(mockedImg);
		assertEquals(mockedID, uut.getId());
	}
}
