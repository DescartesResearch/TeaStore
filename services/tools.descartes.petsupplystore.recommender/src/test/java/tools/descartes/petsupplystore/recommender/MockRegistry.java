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
package tools.descartes.petsupplystore.recommender;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import tools.descartes.petsupplystore.registryclient.Service;

/**
 * Mocks a persistence provider for Recommender testing.
 * 
 * @author Joakim von Kistowski
 *
 */
public class MockRegistry {

	/**
	 * Default Port for the mock registry.
	 */
	public static final int MOCK_REGISTRY_PORT = 43002;

	@Rule
	private WireMockRule wireMockRule = new WireMockRule(MOCK_REGISTRY_PORT);
	
	/**
	 * Create a mock registry operating on port {@value #MOCK_REGISTRY_PORT}.
	 * 
	 * @param persistencePorts Ports of the (mock) persistence providers. 
	 * @param recommenderPorts Ports of the recommenders.
	 * @throws JsonProcessingException Exception on failure.
	 */
	public MockRegistry(List<Integer> persistencePorts, List<Integer> recommenderPorts)
			throws JsonProcessingException {
		initializeServiceQueryStubs(persistencePorts, recommenderPorts);
		initializeUpdateAndHeartbeatStubs();
	}

	private void initializeServiceQueryStubs(List<Integer> persistencePorts, List<Integer> recommenderPorts)
			throws JsonProcessingException {
		List<String> persistences = new LinkedList<String>();
		for (int persistencePort: persistencePorts) {
			persistences.add("localhost:" + persistencePort);
		}
		String json = new ObjectMapper().writeValueAsString(persistences);
		wireMockRule.stubFor(WireMock.get(WireMock.urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.PERSISTENCE.getServiceName()))
						.willReturn(WireMock.okJson(json)));
		List<String> recommenders = new LinkedList<String>();
		for (int recommenderPort: recommenderPorts) {
			recommenders.add("localhost:" + recommenderPort);
		}
		json = new ObjectMapper().writeValueAsString(recommenders);
		wireMockRule.stubFor(WireMock.get(WireMock.urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.RECOMMENDER.getServiceName()))
						.willReturn(WireMock.okJson(json)));
	}
	
	private void initializeUpdateAndHeartbeatStubs() {
		wireMockRule.stubFor(WireMock.put(WireMock.urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/*"))
						.willReturn(WireMock.ok()));
		wireMockRule.stubFor(WireMock.delete(WireMock.urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/*"))
						.willReturn(WireMock.ok()));
	}
	
	/**
	 * Create a mock persistence operating on Port
	 * {@value #MOCK_REGISTRY_PORT}.
	 * Assumes a persistence at the default {@link MockPersistenceProvider}.
	 * @param recommenderPorts Ports of the recommenders.
	 * @throws JsonProcessingException Exception on failure.
	 */
	public MockRegistry(List<Integer> recommenderPorts) throws JsonProcessingException {
		this(Arrays.asList(MockPersistenceProvider.MOCK_PERSISTENCE_PORT),
				recommenderPorts);
	}

	/**
	 * Get the mock registry port.
	 * @return The port.
	 */
	public int getPort() {
		return MOCK_REGISTRY_PORT;
	}
}
