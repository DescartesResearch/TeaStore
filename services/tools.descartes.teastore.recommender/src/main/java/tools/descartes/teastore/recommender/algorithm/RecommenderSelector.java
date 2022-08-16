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
package tools.descartes.teastore.recommender.algorithm;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.recommender.algorithm.impl.UseFallBackException;
import tools.descartes.teastore.recommender.algorithm.impl.cf.PreprocessedSlopeOneRecommender;
import tools.descartes.teastore.recommender.algorithm.impl.cf.SlopeOneRecommender;
import tools.descartes.teastore.recommender.algorithm.impl.orderbased.OrderBasedRecommender;
import tools.descartes.teastore.recommender.algorithm.impl.pop.PopularityBasedRecommender;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;

/**
 * A strategy selector for the Recommender functionality.
 * 
 * @author Johannes Grohmann
 *
 */
public final class RecommenderSelector implements IRecommender {

	/**
	 * This map lists all currently available recommending approaches and assigns
	 * them their "name" for the environment variable.
	 */
	private static Map<String, Class<? extends IRecommender>> recommenders = new HashMap<>();

	static {
		recommenders = new HashMap<String, Class<? extends IRecommender>>();
		recommenders.put("Popularity", PopularityBasedRecommender.class);
		recommenders.put("SlopeOne", SlopeOneRecommender.class);
		recommenders.put("PreprocessedSlopeOne", PreprocessedSlopeOneRecommender.class);
		recommenders.put("OrderBased", OrderBasedRecommender.class);
	}

	/**
	 * The default recommender to choose, if no other recommender was set.
	 */
	private static final Class<? extends IRecommender> DEFAULT_RECOMMENDER = SlopeOneRecommender.class;

	private static final Logger LOG = LoggerFactory.getLogger(RecommenderSelector.class);

	private static RecommenderSelector instance;

	private IRecommender fallbackrecommender;

	private IRecommender recommender;

	/**
	 * Private Constructor.
	 */
	private RecommenderSelector() {
		fallbackrecommender = new PopularityBasedRecommender();
		try {
			String recommendername = (String) new InitialContext().lookup("java:comp/env/recommenderAlgorithm");
			// if a specific algorithm is set, we can use that algorithm
			if (recommenders.containsKey(recommendername)) {
				try {
					recommender = recommenders.get(recommendername).getDeclaredConstructor().newInstance();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			} else {
				LOG.warn("Recommendername: " + recommendername
						+ " was not found. Using default recommender (SlopeOneRecommeder).");
				try {
					recommender = DEFAULT_RECOMMENDER.getDeclaredConstructor().newInstance();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// if creating a new instance fails
			e.printStackTrace();
			LOG.warn("Could not create an instance of the requested recommender. Using fallback.");
			recommender = fallbackrecommender;
		} catch (NamingException e) {
			// if nothing was set
			LOG.info("Recommender not set. Using default recommender (SlopeOneRecommeder).");
			try {
				try {
					recommender = DEFAULT_RECOMMENDER.getDeclaredConstructor().newInstance();
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				} catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				} catch (SecurityException e1) {
					e1.printStackTrace();
				}
			} catch (InstantiationException | IllegalAccessException e1) {
				// also the default algorithm could fail
				e1.printStackTrace();
				LOG.warn("Could not create an instance of DEFAULT_RECOMMENDER " + DEFAULT_RECOMMENDER.getName() + ".");
				recommender = fallbackrecommender;
			}
		}
	}

	@Override
	public List<Long> recommendProducts(Long userid, List<OrderItem> currentItems)
			throws UnsupportedOperationException {
		try {
			return recommender.recommendProducts(userid, currentItems);
		} catch (UseFallBackException e) {
			// a UseFallBackException is usually ignored (as it is conceptual and might
			// occur quite often)
			LOG.trace("Executing " + recommender.getClass().getName()
					+ " as recommender failed. Using fallback recommender. Reason:\n" + e.getMessage());
			return fallbackrecommender.recommendProducts(userid, currentItems);
		} catch (UnsupportedOperationException e) {
			// if algorithm is not yet trained, we throw the error
			LOG.error("Executing " + recommender.getClass().getName()
					+ " threw an UnsupportedOperationException. The recommender was not finished with training.");
			throw e;
		} catch (Exception e) {
			// any other exception is just reported
			LOG.warn("Executing " + recommender.getClass().getName()
					+ " threw an unexpected error. Using fallback recommender. Reason:\n" + e.getMessage());
			return fallbackrecommender.recommendProducts(userid, currentItems);
		}
	}

	/**
	 * Returns the instance of this Singleton or creates a new one, if this is the
	 * first call of this method.
	 * 
	 * @return The instance of this class.
	 */
	public static synchronized RecommenderSelector getInstance() {
		if (instance == null) {
			 instance = new RecommenderSelector();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.IRecommender#train(java.util.List,
	 * java.util.List)
	 */
	@Override
	public void train(List<OrderItem> orderItems, List<Order> orders) {
		recommender.train(orderItems, orders);
		fallbackrecommender.train(orderItems, orders);
	}

}
