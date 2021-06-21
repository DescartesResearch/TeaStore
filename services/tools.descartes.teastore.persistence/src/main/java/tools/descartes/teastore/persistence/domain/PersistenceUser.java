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
package tools.descartes.teastore.persistence.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PreRemove;

import tools.descartes.teastore.persistence.repository.CacheManager;
import tools.descartes.teastore.entities.User;

/**
 * Persistence entity for user.
 * @author Joakim von Kistowski
 *
 */
@Entity
public class PersistenceUser extends User {

	@Id
	@GeneratedValue
	private long id;
	
	private String userName;
	
	private String password;
	private String realName;
	private String email;
	
	@OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
	private List<PersistenceOrder> orders;
	
	/**
	 * Delete orders and order items.
	 */
	@PreRemove
	private void deleteOrders() {
		EntityManager em = UserRepository.REPOSITORY.getEMF().createEntityManager();
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM PersistenceOrderItem oi WHERE oi.order.user = :user")
			.setParameter("user", this).executeUpdate();
			em.createQuery("DELETE FROM PersistenceOrder o WHERE o.user = :user")
			.setParameter("user", this).executeUpdate();
			em.getTransaction().commit();
		} finally {
	        em.close();
	    }
	}
	
	/**
	 * Clear users and order items from cache post remove.
	 */
	@PostRemove
	private void clearCaches() {
		CacheManager.MANAGER.clearCache(PersistenceOrder.class);
		CacheManager.MANAGER.clearRemoteCache(PersistenceUser.class);
	}
	
	/**
	 * Create a new and empty user.
	 */
	PersistenceUser() {
		super();
		orders = new ArrayList<>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getId() {
		return id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUserName() {
		return userName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPassword() {
		return password;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRealName() {
		return realName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEmail() {
		return email;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Get the user's orders.
	 * @return the orders.
	 */
	public List<PersistenceOrder> getOrders() {
		return orders;
	}	
}
