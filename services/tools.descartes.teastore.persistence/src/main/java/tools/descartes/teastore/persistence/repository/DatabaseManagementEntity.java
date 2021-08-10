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
package tools.descartes.teastore.persistence.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * Entity for persisting database managenemt information in database.
 * @author Joakim von Kistowski
 *
 */
@Entity
public class DatabaseManagementEntity {

	@Id
	@GeneratedValue
	private long id;
	
	private boolean finishedGenerating;
	
	/**
	 * Create a new management entity.
	 */
	DatabaseManagementEntity() {
		finishedGenerating = false;
	}

	/**
	 * Get the id.
	 * @return the id
	 */
	long getId() {
		return id;
	}

	/**
	 * False if the database is currently being generated.
	 * True, otherwise.
	 * @return Database generation status.
	 */
	public boolean isFinishedGenerating() {
		return finishedGenerating;
	}

	/**
	 * Specify if the database has finished generating.
	 * False if the database is currently being generated, true otherwise.
	 * @param finishedGenerating Database generation status.
	 */
	public void setFinishedGenerating(boolean finishedGenerating) {
		this.finishedGenerating = finishedGenerating;
	}
	
}
