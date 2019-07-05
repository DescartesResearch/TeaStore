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
package tools.descartes.teastore.registry.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry for the services.
 * 
 * @author Simon Eismann
 *
 */
public final class Registry {

  private static Registry registry = new Registry();
  private Map<String, List<String>> serviceLocationMap = new HashMap<String, List<String>>();
  private Map<String, HeartbeatInfo> heartbeatMap = Collections
      .synchronizedMap(new HashMap<String, HeartbeatInfo>());
  private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private Lock readLock = readWriteLock.readLock();
  private Lock writeLock = readWriteLock.writeLock();
  private static final Logger LOG = LoggerFactory.getLogger(Registry.class);

  private Registry() {
  }

  /**
   * Getter for singleton registry.
   * 
   * @return registry singleton
   */
  public static Registry getRegistryInstance() {
    return registry;
  }

  /**
   * Returns all locations for a service.
   * 
   * @param name
   *          Name of the service
   * @return List over all locations
   */
  public List<String> getLocations(String name) {
    List<String> locations;
    readLock.lock();
    try {
      locations = serviceLocationMap.get(name);
    } finally {
      readLock.unlock();
    }

    writeLock.lock();
    try {
      if (locations == null) {
        locations = new LinkedList<String>();
        serviceLocationMap.put(name, locations);
      }
    } finally {
      writeLock.unlock();
    }
    return locations;
  }

  private void updateHeartbeatMap(String name, String location) {
    HeartbeatInfo info = heartbeatMap.get(name + location);
    if (info == null) {
      heartbeatMap.put(name + location, new HeartbeatInfo());
    } else {
      info.newHeartbeat();
    }
  }

  /**
   * Unregisters a service instance from the registry.
   * 
   * @param name
   *          name of the service
   * @param location
   *          instance location
   * @return boolean success indicator
   */
  public boolean unregister(String name, String location) {
    writeLock.lock();
    try {
      List<String> locations = serviceLocationMap.get(name);
      if (locations == null) {
        return false;
      }

      boolean removed = locations.remove(location);
      if (locations.size() == 0) {
        serviceLocationMap.remove(name);
      }

      if (removed) {
        LOG.info("Unregistered " + name + "@" + location);
      }
      return removed;
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * Registers a service instance from the registry.
   * 
   * @param name
   *          name of the service
   * @param location
   *          instance location
   * @return boolean success indicator
   */
  public boolean register(String name, String location) {
    updateHeartbeatMap(name, location);

    writeLock.lock();
    try {
      List<String> locations = getLocations(name);
      if (locations.contains(location)) {
        return false;
      }
    
      serviceLocationMap.get(name).add(location);
      LOG.info("Registered " + name + "@" + location);
      return true;
    } finally {
      writeLock.unlock();
    }
  }
  
  /**
   * removes service instances from the registry based on heartbeat.
   */
  public void heartBeatCleanup() {
    writeLock.lock();
    try {
      serviceLocationMap.entrySet().stream().forEach(entry -> {
        for (Iterator<String> iter = entry.getValue().iterator(); iter.hasNext();) {
          String location = iter.next();
          if (!heartbeatMap.get(entry.getKey() + location).isAlive()) {
            iter.remove();
            LOG.warn(
                "Removed " + entry.getKey() + "@" + location + " since it failed the heartbeat!");
          }
        }
      });
    } finally {
      writeLock.unlock();
    }
  }
}
