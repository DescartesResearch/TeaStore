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

import tools.descartes.teastore.registryclient.Service;

/**
 * Exception thrown for too many load balancer retries.
 * @author Joakim von Kistowski
 *
 */
public class LoadBalancerTimeoutException extends RuntimeException {

	private static final long serialVersionUID = 5101941775644953394L;

	private Service targetService;

	/**
	 * Creates a new LoadBalancerTimoutException.
	 * @param message The the timeout message.
	 * @param targetService The service for which the load balancer failed.
	 */
	public LoadBalancerTimeoutException(String message, Service targetService) {
		super(message);
		this.targetService = targetService;
	}
	
	/**
	 * Returns the service for which the timeout occurred.
	 * @return The service for which the timeout occurred.
	 */
	public Service getTargetService() {
		return targetService;
	}
	
}
