package tools.descartes.petstore.webui.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tools.descartes.petstore.entities.message.SessionBlob;

public abstract class AbstractUIServlet extends HttpServlet {

	private static final long serialVersionUID = 5907651960296151734L;
	protected static final String MESSAGECOOKIE = "petsupplystoreMessageCookie";
	protected static final String SUCESSLOGIN = "You are logged in!";
	protected static final String SUCESSLOGOUT = "You are logged out!";
	protected static final String PRODUCTCOOKIE = "petsupplystorenumberProductsCookie";
	protected static final String BLOB = "sessionBlob";
	protected static final String ORDERCONFIRMED = "Your order is confirmed!";
	protected static final String CARTUPDATED = "Your cart is updated!";
	protected static final String ADDPRODUCT = "Product %s is added to cart!";
	protected static final String REMOVEPRODUCT = "Product %s is removed from cart!";

	protected SessionBlob getSessionBlob(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie cook : request.getCookies()) {
				if (cook.getName().equals(BLOB)) {
					ObjectMapper o = new ObjectMapper();
					try {
						return o.readValue(URLDecoder.decode(cook.getValue(), "UTF-8"), SessionBlob.class);
					} catch (IOException e) {
						throw new IllegalStateException("Cookie corrupted!");
					}
				}
			}
		}
		return new SessionBlob();
	}

	protected void saveSessionBlob(SessionBlob blob, HttpServletResponse response) {
		ObjectMapper o = new ObjectMapper();
		try {
			Cookie cookie = new Cookie(BLOB, URLEncoder.encode(o.writeValueAsString(blob), "UTF-8"));
			response.addCookie(cookie);
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not save blob!");
		}
	}

	protected void destroySessionBlob(SessionBlob blob, HttpServletResponse response) {
		ObjectMapper o = new ObjectMapper();
		try {
			Cookie cookie = new Cookie(BLOB, URLEncoder.encode(o.writeValueAsString(blob), "UTF-8"));
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not destroy blob!");
		}
	}
	
	protected boolean redirect(String target, HttpServletResponse response, String cookiename, String value) throws IOException {
		if(!cookiename.equals("")) {
			Cookie cookie = new Cookie(cookiename, value.replace(" ", "_"));
			response.addCookie(cookie);
		}
		
		return redirect(target, response);
	}
	
	protected boolean redirect(String target, HttpServletResponse response) throws IOException {
		if(!target.startsWith("/")) {
			target = "/" + target;
		}
		response.sendRedirect(getServletContext().getContextPath()+target);
		
		return true;
		
	}
	
	protected void checkforCookie(HttpServletRequest request, HttpServletResponse response) {
		if (request.getCookies() != null) {
			for (Cookie cook : request.getCookies()) {
				if (cook.getName().equals(MESSAGECOOKIE)) {
					request.getSession().setAttribute("message", cook.getValue().replaceAll("_", " "));
					cook.setMaxAge(0);
					response.addCookie(cook);
				} else if (cook.getName().equals(PRODUCTCOOKIE)) {
					request.getSession().setAttribute("numberProducts", cook.getValue());
				}
			}
		}
	}
}