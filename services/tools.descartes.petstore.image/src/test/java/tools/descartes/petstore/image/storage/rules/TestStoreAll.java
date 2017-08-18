package tools.descartes.petstore.image.storage.rules;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.petstore.image.StoreImage;

public class TestStoreAll {
	
	@Mock
	private StoreImage mockedImg; 

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testRule() {
		StoreAll<StoreImage> uut = new StoreAll<>();
		assertTrue(uut.test(mockedImg));
	}
	
}
