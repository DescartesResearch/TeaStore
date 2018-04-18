package tools.descartes.teastore.auth.security;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptProvider {

	public static boolean checkPassword(String password, String password2) {
		return BCrypt.checkpw(password, password2);
	}
}
