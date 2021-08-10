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
package tools.descartes.teastore.persistence.rest;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import tools.descartes.teastore.persistence.domain.UserRepository;
import tools.descartes.teastore.persistence.repository.DataGenerator;
import tools.descartes.teastore.registryclient.util.AbstractCRUDEndpoint;
import tools.descartes.teastore.entities.User;

/**
 * Persistence endpoint for CRUD operations on Categories.
 * 
 * @author Joakim von Kistowski
 *
 */
@Path("users")
public class UserEndpoint extends AbstractCRUDEndpoint<User> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long createEntity(final User category) {
		if (DataGenerator.GENERATOR.isMaintenanceMode()) {
			return -1L;
		}
		try {
			return UserRepository.REPOSITORY.createEntity(category);
		} catch (Exception e) {
			// SQL errors, especially for duplicate user names
			return -1L;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected User findEntityById(final long id) {
		User user = UserRepository.REPOSITORY.getEntity(id);
		if (user == null) {
			return null;
		}
		return new User(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<User> listAllEntities(final int startIndex, final int maxResultCount) {
		List<User> users = new ArrayList<User>();
		for (User u : UserRepository.REPOSITORY.getAllEntities(startIndex, maxResultCount)) {
			users.add(new User(u));
		}
		return users;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean updateEntity(long id, User category) {
		return UserRepository.REPOSITORY.updateEntity(id, category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean deleteEntity(long id) {
		if (DataGenerator.GENERATOR.isMaintenanceMode()) {
			return false;
		}
		return UserRepository.REPOSITORY.removeEntity(id);
	}

	/**
	 * Retreive user with the provided name.
	 * 
	 * @param name
	 *            name of the entity to find.
	 * @return A Response containing the entity.
	 */
	@GET
	@Path("name/{name}")
	public Response findById(@PathParam("name") final String name) {
		if (name == null || name.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		User entity = UserRepository.REPOSITORY.getUserByName(name);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(new User(entity)).build();
	}
}
