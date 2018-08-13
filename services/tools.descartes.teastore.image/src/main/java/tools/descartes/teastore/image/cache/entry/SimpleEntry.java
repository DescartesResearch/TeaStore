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
 * Instantiable wrapper class not storing any additional data aparat from the
 * cachable data.
 * 
 * @author Norbert Schmitt
 *
 * @param <D>
 *          Cachable data that must implement
 *          {@link tools.descartes.teastore.image.cache.entry.ICachable}
 */
public class SimpleEntry<D extends ICachable<D>> extends AbstractEntry<D> {

  /**
   * Basic constructor storing the cachable data. If the cachable data supplied is
   * null, a {@link java.lang.NullPointerException} is thrown.
   * 
   * @param data
   *          Cachable data
   */
  public SimpleEntry(D data) {
    super(data);
  }

  @Override
  public void wasUsed() {
    // There is nothing to do.
  }

}
