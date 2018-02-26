package tools.descartes.petsupplystore.auth.security;


import org.junit.Assert;
import org.junit.Test;

import tools.descartes.petsupplystore.entities.message.SessionBlob;
import tools.descartes.petsupplystore.auth.security.ConstantKeyProvider;

/**
 * Test for the ConstantKeyProvider.
 * @author Simon
 *
 */
public class ConstantKeyProviderTest {

	/**
	 * checks the getKey() functionality.
	 */
	@Test
	public void test() {  
		Assert.assertEquals("thebestsecretkey", new ConstantKeyProvider().getKey(null));
		Assert.assertEquals("thebestsecretkey", new ConstantKeyProvider().getKey(new SessionBlob()));
	}

}
