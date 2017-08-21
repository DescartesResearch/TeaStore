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
package tools.descartes.petstore.persistence.repository;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Class for managing the EMF singleton.
 * @author Jóakim von Kistowski
 *
 */
final class EMFManager {

	private static EntityManagerFactory emf = null; 
	
	private EMFManager() {
		
	}
	
	/**
	 * Get the entity manager factory.
	 * @return The entity manager factory.
	 */
	static EntityManagerFactory getEMF() {
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory("tools.descartes.petstore.persistence");
		}
		return emf;
	}
	
	/**
	 * Closes and deletes EMF to be reinitialized later.
	 */
	static void clearEMF() {
		if (emf != null) {
			emf.close();
		}
		emf = null;
	}
}
