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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PreRemove;

import tools.descartes.teastore.persistence.repository.CacheManager;
import tools.descartes.teastore.entities.Order;

/**
 * Entity for persisting Orders in database.
 * @author Joakim von Kistowski
 *
 */
@Entity
public class PersistenceOrder extends Order {

	@Id
	@GeneratedValue
	private long id;
	private LocalDateTime orderTime;
	
	private long totalPriceInCents;
	private String addressName;
	private String address1;
	private String address2;
	
	private String creditCardCompany;
	private String creditCardNumber;
	private LocalDate creditCardExpiryLocalDate;
	
	@OneToMany(mappedBy = "order", cascade = {CascadeType.ALL})
	private List<PersistenceOrderItem> orderItems;
	
	@ManyToOne(optional = false)
	private PersistenceUser user;

	/**
	 * Create a new and empty order.
	 */
	PersistenceOrder() {
		super();
		orderItems = new ArrayList<PersistenceOrderItem>();
		orderTime = LocalDateTime.now();
		creditCardExpiryLocalDate = LocalDate.now();
	}
	
	/**
	 * Delete orders and order items.
	 */
	@PreRemove
	private void deleteOrders() {
		EntityManager em = OrderRepository.REPOSITORY.getEMF().createEntityManager();
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM PersistenceOrderItem oi WHERE oi.order = :order")
			.setParameter("order", this).executeUpdate();
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
		CacheManager.MANAGER.clearCache(PersistenceUser.class);
		CacheManager.MANAGER.clearCache(PersistenceOrderItem.class);
		CacheManager.MANAGER.clearRemoteCache(PersistenceOrder.class);
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
	public long getUserId() {
		return user.getId();
	}

	/**
	 * Unsupported operation in persistence.
	 * @param userId unsupported parameter.
	 */
	@Override
	public void setUserId(long userId) {
		//unsupported operation
	}

	/**
	 * Get the order's time.
	 * @return The time.
	 */
	public LocalDateTime getOrderTime() {
		return orderTime;
	}

	/**
	 * Sets the order's time.
	 * @param orderTime The time to set.
	 */
	public void setOrderTime(LocalDateTime orderTime) {
		this.orderTime = orderTime;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTime() {
		return getOrderTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTime(String time) {
		if (time != null && !time.isEmpty()) {
			setOrderTime(LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTotalPriceInCents() {
		return totalPriceInCents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTotalPriceInCents(long totalPriceInCents) {
		this.totalPriceInCents = totalPriceInCents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAddressName() {
		return addressName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAddress1() {
		return address1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAddress2() {
		return address2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCreditCardCompany() {
		return creditCardCompany;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCreditCardCompany(String creditCardCompany) {
		this.creditCardCompany = creditCardCompany;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	/**
	 * Get the credit card expiry date.
	 * @return The date.
	 */
	public LocalDate getCreditCardExpiryLocalDate() {
		return creditCardExpiryLocalDate;
	}

	/**
	 * Set the credit card expiry date.
	 * @param creditCardExpiryLocalDate the date to set.
	 */
	public void setCreditCardExpiryLocalDate(LocalDate creditCardExpiryLocalDate) {
		this.creditCardExpiryLocalDate = creditCardExpiryLocalDate;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCreditCardExpiryDate() {
		return getCreditCardExpiryLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCreditCardExpiryDate(String creditCardExpiryDate) {
		if (creditCardExpiryDate != null && !creditCardExpiryDate.isEmpty()) {
			setCreditCardExpiryLocalDate(LocalDate.parse(creditCardExpiryDate, DateTimeFormatter.ISO_LOCAL_DATE));
		}
	}

	/**
	 * Get the order items for the order.
	 * @return The order items.
	 */
	public List<PersistenceOrderItem> getOrderItems() {
		return orderItems;
	}

	/**
	 * Set the order items.
	 * @param orderItems The order items.
	 */
	public void setOrderItems(List<PersistenceOrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	/**
	 * Get the ordering user.
	 * @return The user.
	 */
	public PersistenceUser getUser() {
		return user;
	}

	/**
	 * Set the ordering user.
	 * @param user The user.
	 */
	public void setUser(PersistenceUser user) {
		this.user = user;
	}
	
}
