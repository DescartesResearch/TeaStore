/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.registryclient.loadbalancers;

import java.util.function.Function;

import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.registryclient.util.RESTClient;
import tools.descartes.teastore.registryclient.util.TimeoutException;

/**
 * Wrapper for results from service load balancer calls.
 * @param <R> Entity Type to wrap.
 * @author Joakim von Kistowski
 *
 */
final class ServiceLoadBalancerResult<R> {

	private int statusCode = 200;
	private R entity = null;
	
	private ServiceLoadBalancerResult() {
		
	}
	
	/**
	 * Create a load balancer result by performing a REST operation.
	 * @param client The rest client to perform the operation with (determined by load balancer).
	 * @param operation The operation to perform (passed from the user).
	 * @param <T> REST client type.
	 * @param <R> Entity type.
	 * @throws TimeoutException On receiving the 408 status code.
	 * @throws NotFoundException On receiving the 404 status code.
	 * @return The result. Entity is always null on failure.
	 */
	static <T, R> ServiceLoadBalancerResult<R> fromRESTOperation(
			RESTClient<T> client, Function<RESTClient<T>, R> operation)
			throws NotFoundException, TimeoutException {
		ServiceLoadBalancerResult<R> result = new ServiceLoadBalancerResult<>();
		try {
			result.setEntity(operation.apply(client));
		} catch (NotFoundException e) {
			result.setStatusCode(NotFoundException.ERROR_CODE);
		} catch (TimeoutException e) {
			result.setStatusCode(TimeoutException.ERROR_CODE);
		}
		return result;
	}

	/**
	 * Get the status code for the returned response.
	 * @return The status code.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Set the status code for the returned response.
	 * @param statusCode The status code.
	 */
	private void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Get the entity from the returned response.
	 * @return The entity.
	 */
	public R getEntity() {
		return entity;
	}

	/**
	 * Set the entity from the returned response.
	 * @param entity The entity.
	 */
	private void setEntity(R entity) {
		this.entity = entity;
	}
	
	
	
}
