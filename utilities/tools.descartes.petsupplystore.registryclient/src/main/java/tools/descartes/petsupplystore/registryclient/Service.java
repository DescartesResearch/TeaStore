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
package tools.descartes.petsupplystore.registryclient;

/**
 * Enum of all services in the pet supply store.
 * @author Joakim von Kistowski
 *
 */
public enum Service {
	/**
	 * Persistence service.
	 */
	PERSISTENCE("tools.descartes.petsupplystore.persistence"),
	/**
	 * Recommender service.
	 */
	RECOMMENDER("tools.descartes.petsupplystore.recommender"),
	/**
	 * Store service.
	 */
	AUTH("tools.descartes.petsupplystore.auth"),
	/**
	 * WebUi service.
	 */
	WEBUI("tools.descartes.petsupplystore.webui"),
	/**
	 * Image Provider service.
	 */
	IMAGE("tools.descartes.petsupplystore.image");
	
	private String serviceName;
	
	/**
	 * Service enums have service names. Names are also contexts.
	 * @param serviceName The name of the service.
	 */
	Service(String serviceName) {
		this.serviceName = serviceName;
	}
	
	/**
	 * Gets the service name; name is also context.
	 * @return The service name.
	 */
	public String getServiceName() {
		return serviceName;
	}
}
