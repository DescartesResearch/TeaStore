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
package tools.descartes.teastore.image.cache.entry;

/**
 * Interface that must be imlemented if it should use the cache implementations
 * provided in the package {@link tools.descartes.teastore.image.cache}. Each
 * cachable data type must have a unique ID and a byte size to be identifiable
 * and the cache can determine if there is enough space left.
 * 
 * @author Norbert Schmitt
 *
 * @param <D>
 *          Data type that must implement this interface.
 */
public interface ICachable<D extends ICachable<D>> {

  /**
   * Returns the byte size of the cachable data.
   * 
   * @return The byte size of the cachable data
   */
  public long getByteSize();

  /**
   * Returns the unique identifier of the cachable data.
   * 
   * @return The unique identifier of the cachable data
   */
  public long getId();

}
