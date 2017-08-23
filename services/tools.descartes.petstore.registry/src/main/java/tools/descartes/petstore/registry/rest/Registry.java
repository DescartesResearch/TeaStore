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
package tools.descartes.petstore.registry.rest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Registry for the services.
 * @author Simon Eismann
 *
 */
public final class Registry {

	private static Registry registry = null;
	private Map<String, List<String>> map = new ConcurrentHashMap<String, List<String>>();
	private Map<String, HeartbeatInfo> heartbeatMap = new ConcurrentHashMap<String, HeartbeatInfo>();
	private static final Logger LOG = LoggerFactory.getLogger(Registry.class);
	
	private Registry() { } 
	
	/**
	 * Returns the map used by the registry, should only be used by test cases.
	 * @return map used by registry
	 */
	public Map<String, HeartbeatInfo> getHeartbeatMap() {
		return heartbeatMap;
	}
	
	
	/**
	 * Returns the map used by the registry, should only be used by test cases.
	 * @return map used by registry
	 */
	public Map<String, List<String>> getMap() {
		return map;
	}
	
	/**
	 * Getter for singleton registry.
	 * @return registry singleton
	 */
	public static Registry getRegistryInstance() {
		if (registry == null) {
			registry = new Registry();
		}
		return registry;
	}
	
	/**
	 * Returns all locations for a service.
	 * @param name Name of the service
	 * @return List over all locations
	 */
	public synchronized List<String> getLocations(String name) {
		return getFromMap(name);
	}
	
	private List<String> getFromMap(String name) {
		if (map.get(name) == null) {
			map.put(name, new LinkedList<String>());
		}
		return map.get(name);
	}
	
	private void updateHeartbeatMap(String name, String location) {
		HeartbeatInfo info = getFromHeartbeatMap(name, location);
		if (info == null) {
			heartbeatMap.put(name + location, new HeartbeatInfo());
		} else {
			info.newHeartbeat();
		}
	}
	
	private HeartbeatInfo getFromHeartbeatMap(String name, String location) {
		return heartbeatMap.get(name + location);
	}
	
	/**
	 * Checks if service is alive.
	 * @param name service name
	 * @param location service location
	 * @return true if alive
	 */
	public boolean isAlive(String name, String location) {
		return getFromHeartbeatMap(name, location).isAlive();
	}
	
	/**
	 * Unregisters a service instance from the registry.
	 * @param name name of the service
	 * @param location instance location
	 * @return boolean success indicator
	 */
	public synchronized boolean unregister(String name, String location) {
		if (map.get(name) == null) {
			return false;
		}
		List<String> locations = map.get(name);
		
		return locations.remove(location);
	}

	/**
	 * Registers a service instance from the registry.
	 * @param name name of the service
	 * @param location instance location
	 * @return boolean success indicator
	 */
	public synchronized boolean register(String name, String location) {
		List<String> locations = getFromMap(name);
		updateHeartbeatMap(name, location);
		if (locations.contains(location)) {
			return false;
		}
		LOG.info("Registered " + name + "@" + location);
		locations.add(location);
		return true;
	}
 }
