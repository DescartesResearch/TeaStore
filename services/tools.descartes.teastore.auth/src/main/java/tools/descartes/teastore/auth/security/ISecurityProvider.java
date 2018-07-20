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

package tools.descartes.teastore.auth.security;

import tools.descartes.teastore.entities.message.SessionBlob;

/**
 * Utilities for securing (e.g. encrypting) session blobs.
 * 
 * @author Joakim von Kistowski
 *
 */
public interface ISecurityProvider {

  /**
   * Get the key provider for this security provider.
   * 
   * @return The key provider.
   */
  public IKeyProvider getKeyProvider();

  /**
   * Secures a session blob. May encrypt or hash values within the blob.
   * 
   * @param blob
   *          The blob to secure.
   * @return A secure blob to be passed on to the web ui.
   */
  public SessionBlob secure(SessionBlob blob);

  /**
   * Validates a secured session blob. Returns a valid and readable (e.g.
   * decrypted) blob. Returns null for invalid blobs.
   * 
   * @param blob
   *          The blob to secure.
   * @return The valid and readable (e.g. decrypted) blob. Returns null for
   *         invalid blobs.
   */
  public SessionBlob validate(SessionBlob blob);

}
