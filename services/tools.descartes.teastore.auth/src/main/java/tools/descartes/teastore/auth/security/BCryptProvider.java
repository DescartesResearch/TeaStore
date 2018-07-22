package tools.descartes.teastore.auth.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Wrapper for BCrypt.
 * @author Simon
 *
 */
public final class BCryptProvider {

  /**
   * Hides default constructor.
   */
  private BCryptProvider() {
    
  }
  
  /**
   * validate password using BCrypt.
   * @param password password
   * @param password2 other password
   * @return true if password is correct
   */
  public static boolean checkPassword(String password, String password2) {
    return BCrypt.checkpw(password, password2);
  }
}
