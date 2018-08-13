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
package tools.descartes.teastore.image.setup;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Singleton helper class generating unique image identifiers.
 * @author Norbert Schmitt
 */
public enum ImageIDFactory {

  /**
   * Instance of the image id factory.
   */
  ID;

  private AtomicLong nextID = new AtomicLong(1);

  private ImageIDFactory() {

  }

  /**
   * Returns the next unique image identifier.
   * @return The next unique image identifier.
   */
  public long getNextImageID() {
    return nextID.getAndIncrement();
  }

  /**
   * Sets the first image identifier to start with.
   * @param nextID The image identifier to start with.
   */
  public void startAtID(long nextID) {
    this.nextID.set(nextID);
  }
}
