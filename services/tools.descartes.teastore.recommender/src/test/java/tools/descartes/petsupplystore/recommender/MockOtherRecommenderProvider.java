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
package tools.descartes.teastore.recommender;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import tools.descartes.teastore.registryclient.Service;

/**
 * Mocks another recommender instance for Recommender testing.
 * 
 * @author Johannes Grohmann
 *
 */
public class MockOtherRecommenderProvider {

	/**
	 * Default Port for the mock persistence.
	 */
	public static final int DEFAULT_MOCK_RECOMMENDER_PORT = 43003;

	/**
	 * The timestamp to mock. Should include and exclude some entries.
	 */
	private static final String TIMESTAMP = "1500910140000";

	/**
	 * @return the timestamp
	 */
	public static String getTimestamp() {
		return TIMESTAMP;
	}
	
	private int port;

	/**
	 * Create a mock persistence using a wire mock rule. Recommended: Use
	 * {@link #DEFAULT_MOCK_PERSISTENCE_PORT} as port.
	 * 
	 * @param rule
	 *            The wire mock rule to create the mocking stubs for.
	 */
	public MockOtherRecommenderProvider(WireMockRule rule) {
		this.port = rule.port();
		rule.stubFor(
				WireMock.get(WireMock.urlEqualTo("/" + Service.RECOMMENDER.getServiceName() + "/rest/train/timestamp"))
						.willReturn(WireMock.okJson(getTimestamp())));
	}

	/**
	 * Get the mock persistence port.
	 * 
	 * @return The port.
	 */
	public int getPort() {
		return port;
	}
}
