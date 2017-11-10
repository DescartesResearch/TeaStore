package tools.descartes.petsupplystore.image.cache.entry;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.petsupplystore.image.StoreImage;

public class TestAbstractEntry {

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
}
