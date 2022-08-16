package tools.descartes.teastore.auth.security;

import org.junit.Assert;
import org.junit.Test;

import tools.descartes.teastore.entities.message.SessionBlob;

/**
 * Test for the ConstantKeyProvider.
 * 
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
