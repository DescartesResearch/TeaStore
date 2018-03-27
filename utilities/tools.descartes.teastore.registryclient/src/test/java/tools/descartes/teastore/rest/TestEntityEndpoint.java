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
package tools.descartes.teastore.rest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Path;

import tools.descartes.teastore.registryclient.util.AbstractCRUDEndpoint;

/**
 * Endpoint for test entities.
 * @author Joakim von Kistowski
 *
 */
@Path("testentities")
public class TestEntityEndpoint extends AbstractCRUDEndpoint<TestEntity> {

	private static HashMap<Long, TestEntity> entities = new HashMap<Long, TestEntity>();
	
	private static long idCounter = 1;

	/**
	 * {@inheritDoc}
	 * Creates a test entity.
	 * @param entity Entity to create.
	 */
	@Override
	protected long createEntity(TestEntity entity) {
		long id = idCounter++;
		TestEntity ent = new TestEntity();
		ent.setId(id);
		ent.setAttribute(entity.getAttribute());
		entities.put(id, ent);
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TestEntity findEntityById(long id) {
		return entities.get(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<TestEntity> listAllEntities(int startIndex, int maxResultCount) {
		List<TestEntity> entityList = new ArrayList<TestEntity>(entities.values());
		entityList.sort(Comparator.comparingLong(TestEntity::getId));
		int start = 0;
		if (startIndex >= 0) {
			start = startIndex;
		}
		int end = entityList.size();
		if (maxResultCount >= 0) {
			end = Math.min(entityList.size(), start + maxResultCount);
		}
		entityList = entityList.subList(start, end);
		return entityList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean updateEntity(long id, TestEntity entity) {
		TestEntity ent = null;
		ent = entities.get(id);
		if (ent == null) {
			return false;
		}
		ent.setAttribute(entity.getAttribute());
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean deleteEntity(long id) {
		TestEntity ent = entities.remove(id);
		if (ent != null) {
			return true;
		}
		return false;
	}

}
