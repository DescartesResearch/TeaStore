package tools.descartes.teastore.auth.security;

import com.lambdaworks.crypto.SCryptUtil;

public class SCryptProvider {

	public static boolean checkPassword(String password, String password2) {
		return SCryptUtil.check(password, password2);
	}
}
