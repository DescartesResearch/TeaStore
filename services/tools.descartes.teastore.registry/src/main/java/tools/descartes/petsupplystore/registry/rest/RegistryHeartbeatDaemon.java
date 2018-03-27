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
package tools.descartes.petsupplystore.registry.rest;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Daemon which sends out heartbeats to the resistered service.
 * 
 * @author Simon
 */
public class RegistryHeartbeatDaemon implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(RegistryHeartbeatDaemon.class);

	@Override
	public void run() {
		try {
			Registry.getRegistryInstance().getMap().entrySet().stream().forEach(entry -> {
				for (Iterator<String> iter = entry.getValue().iterator(); iter.hasNext();) {
					String location = iter.next();
					if (!Registry.getRegistryInstance().isAlive(entry.getKey(), location)) {
						iter.remove();
						LOG.warn("Removed " + entry.getKey() + "@" + location + " since it failed the heartbeat!");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
