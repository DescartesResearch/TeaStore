package tools.descartes.teastore.auth.security;

import java.util.Random;

/**
 * Generates random session id.
 * 
 * @author Simon
 *
 */
public class RandomSessionIdGenerator implements ISessionIdGenerator {

  private static Random random = new Random();

  @Override
  public String getSessionId() {
    return "" + random.nextInt();
  }

}
