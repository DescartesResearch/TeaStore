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
package tools.descartes.petsupplystore.persistence.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.mindrot.jbcrypt.BCrypt;

import tools.descartes.petsupplystore.persistence.domain.CategoryRepository;
import tools.descartes.petsupplystore.persistence.domain.OrderItemRepository;
import tools.descartes.petsupplystore.persistence.domain.OrderRepository;
import tools.descartes.petsupplystore.persistence.domain.PersistenceCategory;
import tools.descartes.petsupplystore.persistence.domain.PersistenceOrder;
import tools.descartes.petsupplystore.persistence.domain.PersistenceUser;
import tools.descartes.petsupplystore.persistence.domain.ProductRepository;
import tools.descartes.petsupplystore.persistence.domain.UserRepository;
import tools.descartes.petsupplystore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.petsupplystore.registryclient.util.RESTClient;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.User;

/**
 * Class for generating data in the database.
 * 
 * @author Joakim von Kistowski
 *
 */
public final class DataGenerator {

	/**
	 * Status code for maintenance mode.
	 */
	public static final int MAINTENANCE_STATUS_CODE = 503;
	
	/**
	 * Default category count for small database.
	 */
	public static final int SMALL_DB_CATEGORIES = 5;
	/**
	 * Default product count per category for small database.
	 */
	public static final int SMALL_DB_PRODUCTS_PER_CATEGORY = 100;
	/**
	 * Default user count for small database.
	 */
	public static final int SMALL_DB_USERS = 100;
	/**
	 * Default max order per user for small database.
	 */
	public static final int SMALL_DB_MAX_ORDERS_PER_USER = 5;
	
	/**
	 * Default category count for tiny database.
	 */
	public static final int TINY_DB_CATEGORIES = 2;
	/**
	 * Default product count per category for tiny database.
	 */
	public static final int TINY_DB_PRODUCTS_PER_CATEGORY = 20;
	/**
	 * Default user count for tiny database.
	 */
	public static final int TINY_DB_USERS = 5;
	/**
	 * Default max order per user for tiny database.
	 */
	public static final int TINY_DB_MAX_ORDERS_PER_USER = 2;
	
	private Random random = new Random(5);

	private static final String PASSWORD = "password";
	private static final String[] CATEGORYNAMES = { "Red Pandas", "Fish", "Dogs", "Cats", "Reptiles", "Birds", 
			"Hamsters", "Guinea pigs", "Ferrets" };
	private static final String[] CATEGORYDESCRIPTIONS = { "Exotic animals from Asia", "Saltwater and Freshwater",
			"Man's best friend", "They don't serve you, you serve them", "Lizards, Turtles, Snakes",
			"Producers of feathers everywhere", "Nocturnal little things", "Not for eating, exceptions apply",
			"Cute fluffies" };

	private static final String[][] PRODUCTNAMES = {
			{ "Bamboo", "Premium Bamboo", "Climbing Tree 2x4m", "Climbing Tree 3x6m", "Climbing Tree 4x8m", 
					"Nest (grass)", "Nest (leaves)", "Food eggs (7 pieces)", "Food eggs (14 pieces)", 
					"Cage 50x100m", "Cage 100x200m", "Cage 200x400m"}, 
			{ "Aquarium 1x2m", "Aquarium 1x3m", "Aquarium 2x3m", "Aquarium 2x4m", "Pump (up to 200 l)",
					"Pump (up to 4000 l)", "Small net", "Medium Net", "Decorative Rocks", "Decorative Plants",
					"Food: Plankton", "Food: Small Fish" },
			{ "Dog House 2x3m", "Dog House 3x4m", "Leash 2m", "Leash 3m", "Leash, extending (up to 5m)",
					"Collar (textile, black)", "Collar (textile, blue)", "Collar (textile, white)", "Collar (metal)",
					"Ball", "Throwing Stick", "Tasty Meats", "Eating Bowl", "Water Bowl" },
			{ "Scratching Post (small)", "Scratching Post (huge)", "Scratching Playground", "Food Bowl", "Water Bowl",
					"Collar, black", "Collar, white", "Collar, blue", "Litter Box (small)", "Litter Box (huge)",
					"Tasty Meat", "Gross Meat" },
			{ "Terrarium 1x2m", "Terrarium 1x3m", "Terrarium 2x3m", "Terrarium 2x4m", "Heater (50 W)", "Heater (200 W)",
					"Excrement Scoop", "Tropical Sun Light", "Ocean Sun Light", "Electric Incubator", "Food: Flies",
					"Food: Worms" },
			{ "Cage 2x1m", "Cage 3x1m", "Cage 3x2m", "Cage 2x4m", "Perch", "Ladder (small)", "Ladder (huge)",
					"Swing (small)", "Swing (huge)", "Canary Food", "Parrot Food", "Conure Food", "Cage Cleaner" },
			{ "Nuts", "Bigger Nuts", "Grains", "Cage 1x2m", "Cage 1x3m", "Cage 2x3m", "Cage 2x4m", "Bedding",
					"Watter Bottle (small)", "Water Bottle (huge)", "House (small)", "House (huge)" },
			{ "Nuts", "Bigger Nuts", "Grains", "Cage 1x2m", "Cage 1x3m", "Cage 2x3m", "Cage 2x4m", "Bedding",
					"Watter Bottle (small)", "Water Bottle (huge)", "House (small)", "House (huge)" },
			{ "Nuts", "Bigger Nuts", "Grains", "Cage 1x2m", "Cage 1x3m", "Cage 2x3m", "Cage 2x4m", "Bedding",
					"Watter Bottle (small)", "Water Bottle (huge)", "Climbing Post" } };

	private static final String[] FIRSTNAMES = {"James", "John", "Robert", "Michael", "William", "David",
			"Richard", "Charles", "Jospeph", "Thomas", "Christopher", "Daniel", "Paul", "Mark", "Donald",
			"George", "Kenneth", "Steven", "Edward", "Brian", "Ronald", "Anthony", "Kevin", "Jason",
			"Matthew", "Gary", "Timothy", "Jose", "Larry", "Jeffrey", "Frank", "Scott", "Eric", "Stephen",
			"Andrew", "Raymond", "Gregory", "Joshua", "Jerry", "Dennis", "Walter", "Patrick", "Peter",
			"Mary", "Patricia", "Barbara", "Elizabeth", "Jennifer", "Maria", "Susan", "Margaret", "Dorothy",
			"Lisa", "Nancy", "Karen", "Betty", "Helen", "Sandra", "Donna", "Carol", "Ruth", "Sharon",
			"Michelle", "Laura", "Sarah", "Kimberly", "Deborah", "Jessica", "Shirley", "Cynthia"};
	private static final String[] LASTNAMES = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis",
			"Miller", "Wilson", "Moorse", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris",
			"Martin", "Thompson", "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee",
			"Walker", "Hall", "Allen", "Young", "Hernandez", "King", "Wright", "Lopez", "Hill", "Scoot"};
	
	private static final int MAX_ITEMS_PER_ORDER = 10;
	private static final double PREFFERED_CATEGORY_CHANCE = 0.825;
	

	/**
	 * The data generator singleton.
	 */
	public static final DataGenerator GENERATOR = new DataGenerator();

	private boolean maintenanceMode = false;
	
	private DataGenerator() {

	}

	/**
	 * Checks if the database is empty.
	 * 
	 * @return True if the database is empty.
	 */
	public boolean isDatabaseEmpty() {
		// every other entity requires a valid category or user
		return (CategoryRepository.REPOSITORY.getAllEntities(-1, 1).size() == 0
				&& UserRepository.REPOSITORY.getAllEntities(-1, 1).size() == 0);
	}

	/**
	 * Generates data for the database. Uses a fixed random seed.
	 * 
	 * @param categories
	 *            Number of categories.
	 * @param productsPerCategory
	 *            Number of products per category.
	 * @param users
	 *            Number of users. Password is always "password".
	 * @param maxOrdersPerUser
	 *            Maximum order per user.
	 */
	public void generateDatabaseContent(int categories, int productsPerCategory,
			int users, int maxOrdersPerUser) {
		setGenerationFinishedFlag(false);
		CacheManager.MANAGER.clearAllCaches();
		random = new Random(5);
		generateCategories(categories);
		generateProducts(productsPerCategory);
		generateUsers(users);
		generateOrders(maxOrdersPerUser, productsPerCategory);
		setGenerationFinishedFlag(true);
		CacheManager.MANAGER.clearAllCaches();
	}

	private void generateCategories(int categories) {
		for (int i = 0; i < categories; i++) {
			Category category = new Category();
			if (i < CATEGORYDESCRIPTIONS.length) {
				category.setDescription(CATEGORYDESCRIPTIONS[i]);
			} else {
				int version = i / CATEGORYDESCRIPTIONS.length;
				category.setDescription(CATEGORYDESCRIPTIONS[i % CATEGORYDESCRIPTIONS.length] + ", v" + version);
			}
			if (i < CATEGORYNAMES.length) {
				category.setName(CATEGORYNAMES[i]);
			} else {
				int version = i / CATEGORYNAMES.length;
				category.setName(CATEGORYNAMES[i % CATEGORYNAMES.length] + ", v" + version);
			}
			CategoryRepository.REPOSITORY.createEntity(category);
		}
	}

	private void generateProducts(int productsPerCategory) {
		int categoryIndex = 0;
		for (PersistenceCategory category : CategoryRepository.REPOSITORY.getAllEntities()) {
			for (int i = 0; i < productsPerCategory; i++) {
				int productTypeIndex = categoryIndex % PRODUCTNAMES.length;
				int productIndex = i % PRODUCTNAMES[productTypeIndex].length;
				int version = i / PRODUCTNAMES[productTypeIndex].length;
				Product product = new Product();
				if (version == 0) {
					product.setName(PRODUCTNAMES[productTypeIndex][productIndex]);
				} else {
					product.setName(PRODUCTNAMES[productTypeIndex][productIndex] + ", v" + version);
				}
				product.setDescription(
						"The best " + PRODUCTNAMES[productTypeIndex][productIndex] + " for " + category.getName());
				product.setListPriceInCents(95 + random.nextInt(12000));
				product.setCategoryId(category.getId());
				ProductRepository.REPOSITORY.createEntity(product);
			}
			categoryIndex++;
		}
	}

	private void generateUsers(int users) {
		for (int i = 0; i < users; i++) {
			User user = new User();
			user.setUserName("user" + i);
			user.setEmail("user" + i + "@petsupplystore.com");
			user.setRealName(FIRSTNAMES[random.nextInt(FIRSTNAMES.length)]
					+ " " + LASTNAMES[random.nextInt(LASTNAMES.length)]);
			user.setPassword(BCrypt.hashpw(PASSWORD, BCrypt.gensalt()));
			UserRepository.REPOSITORY.createEntity(user);
		}
	}
	
	private void generateOrders(int maxOrdersPerUser, int productsPerCategory) {
		for (PersistenceUser user : UserRepository.REPOSITORY.getAllEntities()) {
			for (int i = 0; i < random.nextInt(maxOrdersPerUser + 1); i++) {
				Order order = new Order();
				order.setAddressName(user.getRealName());
				String eastWest = " East ";
				if (random.nextDouble() > 0.5) {
					eastWest = " West ";
				}
				String northSouth = " North";
				if (random.nextDouble() > 0.5) {
					northSouth = " South";
				}
				order.setAddress1(random.nextInt(9000) + eastWest + random.nextInt(9000) + northSouth);
				order.setAddress2("District " + random.nextInt(500) + ", Utopia, " + (10000 + random.nextInt(40000)));
				order.setCreditCardCompany("MasterCard");
				if (random.nextDouble() > 0.5) {
					order.setCreditCardCompany("Visa");
				}
				order.setCreditCardExpiryDate(LocalDate.ofYearDay(LocalDateTime.now().getYear()
						+ 1 + random.nextInt(10), 1 + random.nextInt(363)).format(DateTimeFormatter.ISO_LOCAL_DATE));
				order.setTime(LocalDateTime.of(LocalDateTime.now().getYear() - random.nextInt(10),
						1 + random.nextInt(10), 1 + random.nextInt(24), random.nextInt(23), random.nextInt(59))
						.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				order.setUserId(user.getId());
				order.setCreditCardNumber(fourDigits() + " " + fourDigits() + " " + fourDigits() + " " + fourDigits());
				long orderId = OrderRepository.REPOSITORY.createEntity(order);
				PersistenceOrder createdOrder = OrderRepository.REPOSITORY.getEntity(orderId);
				long price = 0;
				List<PersistenceCategory> categories = CategoryRepository.REPOSITORY.getAllEntities();
				Category preferred = categories.get(random.nextInt(categories.size()));
				for (int j = 0; j < 1 + random.nextInt(MAX_ITEMS_PER_ORDER); j++) {
					OrderItem item = generateOrderItem(createdOrder, preferred, productsPerCategory);
					price += item.getQuantity() * item.getUnitPriceInCents();
					OrderItemRepository.REPOSITORY.createEntity(item);
				}
				createdOrder.setTotalPriceInCents(price);
				OrderRepository.REPOSITORY.updateEntity(orderId, createdOrder);
			}
		}
	}
	
	//Order and preferred category must have a valid id!
	private OrderItem generateOrderItem(Order order, Category preferred, int productsPerCategory) {
		OrderItem item = new OrderItem();
		item.setOrderId(order.getId());
		item.setQuantity(random.nextInt(7));
		Category itemCategory = preferred;
		if (random.nextDouble() > PREFFERED_CATEGORY_CHANCE) {
			List<PersistenceCategory> categories = CategoryRepository.REPOSITORY.getAllEntities();
			itemCategory = categories.get(random.nextInt(categories.size()));
		}
		Product product = ProductRepository.REPOSITORY.getAllEntities(
				itemCategory.getId(), random.nextInt(productsPerCategory), 1).get(0);
		item.setProductId(product.getId());
		item.setUnitPriceInCents(product.getListPriceInCents());
		return item;
	}
	
	private String fourDigits() {
		return String.valueOf(1000 + random.nextInt(8999));
	}
	
	/**
	 * Drops database and recreates all tables.<br/>
	 * Attention: Does not reset foreign persistence contexts.
	 * Best practice is to call CacheManager.MANAGER.resetAllEMFs() after dropping and then recreating the DB.  
	 */
	public void dropAndCreateTables() {
		CacheManager.MANAGER.clearLocalCacheOnly();
		ServerSession session = CategoryRepository.REPOSITORY.getEM().unwrap(ServerSession.class);  
		SchemaManager schemaManager = new SchemaManager(session);
		schemaManager.replaceDefaultTables(true, true);
		CacheManager.MANAGER.clearLocalCacheOnly();
		CacheManager.MANAGER.resetLocalEMF();
		setGenerationFinishedFlag(false);
		CacheManager.MANAGER.clearAllCaches();
	}
	
	private void setGenerationFinishedFlag(boolean flag) {
		EntityManager em = CategoryRepository.REPOSITORY.getEM();
		try {
			em.getTransaction().begin();
			List<DatabaseManagementEntity> entities =
					em.createQuery("SELECT u FROM "
							+ DatabaseManagementEntity.class.getName()
							+ " u", DatabaseManagementEntity.class)
					.getResultList();
			if (entities == null || entities.isEmpty()) {
				DatabaseManagementEntity entity = new DatabaseManagementEntity();
				entity.setFinishedGenerating(flag);
				em.persist(entity);
			} else {
				DatabaseManagementEntity entity = entities.get(0);
				entity.setFinishedGenerating(flag);
			}
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}
	
	/**
	 * Returns true if the database has finished generating.
	 * False if it is currently generating.
	 * @return False if the database is generating.
	 */
	public boolean getGenerationFinishedFlag() {
		if (isMaintenanceMode()) {
			return false;
		}
		boolean finishedGenerating = false;
		EntityManager em = CategoryRepository.REPOSITORY.getEM();
		try {
			List<DatabaseManagementEntity> entities =
					em.createQuery("SELECT u FROM "
							+ DatabaseManagementEntity.class.getName()
							+ " u", DatabaseManagementEntity.class)
					.getResultList();
			if (entities != null && !entities.isEmpty()) {
				finishedGenerating = entities.get(0).isFinishedGenerating();
			}
		} finally {
			em.close();
		}
		return finishedGenerating;
	}

	/**
	 * Returns if the current persistence is in maintenance mode.
	 * Will return 503 on pretty much every external call in this mode.
	 * @return True if in maintenance, false otherwise.
	 */
	public boolean isMaintenanceMode() {
		return maintenanceMode;
	}

	/**
	 * Put the current persistence into maintenance mode.
	 * Will return 503 on pretty much every external call in this mode.
	 * @param maintenanceMode The maintenance flag.
	 */
	public synchronized void setMaintenanceModeInternal(boolean maintenanceMode) {
		this.maintenanceMode = maintenanceMode;
	}
	
	/**
	 * Puts all persistences into maintenance mode.
	 * Will return 503 on pretty much every external call once in this mode.
	 * @param maintenanceMode The maintenance flag.
	 */
	public void setMaintenanceModeGlobal(boolean maintenanceMode) {
		setMaintenanceModeInternal(maintenanceMode);
		List<Response> rs = ServiceLoadBalancer.multicastRESTToOtherServiceInstances(
				"generatedb", String.class, client -> setMaintenanceModeExternal(client, maintenanceMode));
		rs.forEach(r -> {
				if (r != null) {
					r.bufferEntity();
					r.close();
				}
			}); 
	}
	
	private Response setMaintenanceModeExternal(RESTClient<String> client, final Boolean maintenanceMode) {
		Response r = client.getEndpointTarget().path("maintenance")
		.request(MediaType.TEXT_PLAIN).post(Entity.entity(String.valueOf(maintenanceMode), MediaType.TEXT_PLAIN));
		return r;
	}
}
