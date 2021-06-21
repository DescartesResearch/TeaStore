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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import tools.descartes.teastore.persistence.domain.ProductRepository;
import tools.descartes.teastore.persistence.repository.DataGenerator;
import tools.descartes.teastore.registryclient.util.AbstractCRUDEndpoint;
import tools.descartes.teastore.entities.Product;

/**
 * Persistence endpoint for for CRUD operations on products.
 * @author Joakim von Kistowski
 *
 */
@Path("products")
public class ProductEndpoint extends AbstractCRUDEndpoint<Product> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long createEntity(final Product product) {
		if (DataGenerator.GENERATOR.isMaintenanceMode()) {
			return -1L;
		}
		return ProductRepository.REPOSITORY.createEntity(product);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Product findEntityById(final long id) {
		Product product = ProductRepository.REPOSITORY.getEntity(id);
		if (product == null) {
			return null;
		}
		return new Product(product);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Product> listAllEntities(final int startIndex, final int maxResultCount) {
		List<Product> products = new ArrayList<Product>();
		for (Product p : ProductRepository.REPOSITORY.getAllEntities(startIndex, maxResultCount)) {
			products.add(new Product(p));
		}
		return products;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean updateEntity(long id, Product product) {
		return ProductRepository.REPOSITORY.updateEntity(id, product);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean deleteEntity(long id) {
		if (DataGenerator.GENERATOR.isMaintenanceMode()) {
			return false;
		}
		return ProductRepository.REPOSITORY.removeEntity(id);
	}
	
	/**
	 * Returns all products with the given category Id (all products in that category).
	 * @param categoryId The id of the Category.
	 * @param startPosition The index (NOT ID) of the first product in the category to return.
	 * @param maxResult The max number of products to return.
	 * @return list of products in the category.
	 */
	@GET
	@Path("category/{category:[0-9][0-9]*}")
	public List<Product> listAllForCategory(@PathParam("category") final Long categoryId,
			@QueryParam("start") final Integer startPosition,
			@QueryParam("max") final Integer maxResult) {
		if (categoryId == null) {
			return listAll(startPosition, maxResult);
		}
		List<Product> products = new ArrayList<Product>();
		for (Product p : ProductRepository.REPOSITORY.getAllEntities(categoryId,
				parseIntQueryParam(startPosition), parseIntQueryParam(maxResult))) {
			products.add(new Product(p));
		}
		return products;
	}
	
	/**
	 * Returns the count of produts with the given category Id (products in that category).
	 * @param categoryId The id of the Category.
	 * @return list of products in the category.
	 */
	@GET
	@Path("count/{category:[0-9][0-9]*}")
	public Response countForCategory(@PathParam("category") final Long categoryId) {
		if (categoryId == null) {
			return Response.status(404).build();
		}
		long count = ProductRepository.REPOSITORY.getProductCount(categoryId);
		if (count >= 0) {
			return Response.ok(String.valueOf(count)).build();
		}
		return Response.status(404).build();
	}
}
