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
package tools.descartes.teastore.registryclient;

import com.netflix.loadbalancer.Server;

/**
 * Daemon which sends out heartbeats to the registry.
 * @author Simon
 */
public class RegistryClientHeartbeatDaemon implements Runnable {

	private Service service;
	private Server server;
	
	/**
	 * Constructor.
	 * @param service Service enum
	 * @param server Service location
	 */
	public RegistryClientHeartbeatDaemon(Service service, Server server) {
		this.server = server;
		this.service = service;
	}
	
	@Override
	public void run() {
		try {
			RegistryClient.getClient().registerOnce(service, server);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
