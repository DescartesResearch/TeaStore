package tools.descartes.petsupplystore.registryclient.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.petsupplystore.registryclient.util.NotFoundException;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.Product;

public class RestUtil {
	
	public static void throwCommonExceptions(Response responseWithStatus)
			throws NotFoundException, LoadBalancerTimeoutException {
		if (responseWithStatus.getStatus() == Status.NOT_FOUND.getStatusCode()) {
			throw new NotFoundException();
		} else if (responseWithStatus.getStatus() == Status.REQUEST_TIMEOUT.getStatusCode()) {
			throw new LoadBalancerTimeoutException("Timout waiting for Store.", Service.AUTH);
		}
	}
	
	public static <T> T readEntityOrNull(Response r, Class<T> entityClass) {
		if (r != null) {
			if (r.getStatus() == 200) {
				return r.readEntity(entityClass);
			} else {
				r.bufferEntity();
			}
		}
		return null;
	}
	
	public static <T> T readThrowAndOrClose(Response responseWithStatus, Class<T> entityClass) {
		T entity = null;
		entity = readEntityOrNull(responseWithStatus, entityClass);
		throwCommonExceptions(responseWithStatus);
		return entity;
	}
	
	public static List<Order> readListThrowAndOrCloseOrder(Response r) {
		List<Order> entity = null;
		if (r != null) {
			if (r.getStatus() == 200) {
				entity = r.readEntity(new GenericType<List<Order>>() { });
			} else {
				r.bufferEntity();
			}
		}
		if (r == null || entity == null) {
			entity = new ArrayList<Order>();
		}
		throwCommonExceptions(r);
		return entity;
	}
	
	public static List<Product> readListThrowAndOrCloseProduct(Response r) {
		List<Product> entity = null;
		if (r != null) {
			if (r.getStatus() == 200) {
				entity = r.readEntity(new GenericType<List<Product>>() { });
			} else {
				r.bufferEntity();
			}
		}
		if (r == null || entity == null) {
			entity = new ArrayList<Product>();
		}
		throwCommonExceptions(r);
		return entity;
	}
}
