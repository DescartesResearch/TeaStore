package tools.descartes.petsupplystore.store.security;

import java.util.Random;

/**
 * Generates random session id.
 * @author Simon
 *
 */
public class RandomSessionIdGenerator implements ISessionIdGenerator {

	private static Random random = new Random();
	
	@Override
	public String getSessionID() {
		return "" + random.nextInt();
	}

}
