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
package tools.descartes.petstore.entities.message;

/**
 * Simple container for sending IDs in JSON/XML using REST.
 * @author Joakim von Kistowski
 */
public class IdContainer {

	private long id;

	/**
	 * Create a new and empty IdContainer.
	 */
	public IdContainer() {
		
	}
	
	/**
	 * Create a new IdContainer.
	 * @param id The id to contain.
	 */
	public IdContainer(long id) {
		setId(id);
	}
	
	/**
	 * Get the ID.
	 * @return The id.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the id.
	 * @param id The id.
	 */
	public void setId(long id) {
		this.id = id;
	}
	
}
