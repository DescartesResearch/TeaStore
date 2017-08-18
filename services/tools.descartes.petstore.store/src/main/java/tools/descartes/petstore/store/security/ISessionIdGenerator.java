package tools.descartes.petstore.store.security;
/**
 * Generator for Session ids.
 * @author Simon
 *
 */
public interface ISessionIdGenerator {

	/**
	 * Generates session id.
	 * @return session id
	 */
	public String getSessionID();
}
