package tools.descartes.petstore.store.security;


import org.junit.Assert;
import org.junit.Test;

import tools.descartes.petstore.entities.message.SessionBlob;

/**
 * Test for the SHASecurityProviuder.
 * @author Simon
 *
 */
public class SHASecurityProviderTest {

	/**
	 * checks security token behavior.
	 */
	@Test
	public void test() {
		SHASecurityProvider provider = new SHASecurityProvider();
		SessionBlob blob = new SessionBlob();
		blob.setSID("1234");
		blob.setUID(123456L);
		Assert.assertTrue(provider.validate(blob) == null);
		provider.secure(blob);
		Assert.assertTrue(provider.validate(blob) != null);
		blob.setUID(13L);
		Assert.assertTrue(provider.validate(blob) == null);
		provider.secure(blob);
		Assert.assertTrue(provider.validate(blob) != null);
	}

}
