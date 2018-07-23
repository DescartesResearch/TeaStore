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
package tools.descartes.teastore.image.storage;

import tools.descartes.teastore.image.cache.entry.ICachable;

/**
 * Generic data storage interface providing all necessary methods for saving and loading data to the storage.
 * @author Norbert Schmitt
 *
 * @param <T> Entry Type implementing ICachable.
 */
public interface IDataStorage<T extends ICachable<T>> {

  /**
   * Check whether data with the given ID resides in the storage.
   * @param id ID to check for.
   * @return True if the data with the given ID is found, otherwise false.
   */
  public boolean dataExists(long id);

  /**
   * Returns the data with the given ID if it resides in the storage.
   * @param id ID of data to load.
   * @return The data if it resides in storage or NULL if ID does not reside in storage.
   */
  public T loadData(long id);

  /**
   * Save data in the storage.
   * @param data Data to save in the storage.
   * @return True if data was saved in the storage, otherwise false.
   */
  public boolean saveData(T data);

  /**
   * Checks whether data can be saved in the storage according to the storage rule.
   * @param data Data to check if it complies with the storage rule and can be stored.
   * @return True if the data complies with the storage rule, otherwise false.
   */
  public boolean dataIsStorable(T data);

  /**
   * Removes the data from storage.
   * @param data Data to be removed from storage.
   * @return True if the data was deleted, otherwise false.
   */
  public boolean deleteData(T data);
}
