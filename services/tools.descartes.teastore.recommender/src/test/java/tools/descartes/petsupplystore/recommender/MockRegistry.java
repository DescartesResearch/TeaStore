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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import tools.descartes.teastore.registryclient.Service;

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
	public static final int DEFAULT_MOCK_REGISTRY_PORT = 43002;
	private int port;
	/**
	 * Create a mock registry using a wiremock rule.
	 * Recommended: Use {@link #DEFAULT_MOCK_REGISTRY_PORT} as port for your rule.
	 * @param rule The wiremock rule to generate the mock stubs for.
	 * @param persistencePorts Ports of the (mock) persistence providers. 
	 * @param recommenderPorts Ports of the recommenders.
	 * @throws JsonProcessingException Exception on failure.
	 */
	public MockRegistry(WireMockRule rule, List<Integer> persistencePorts, List<Integer> recommenderPorts)
			throws JsonProcessingException {
		port = rule.port();
		initializeServiceQueryStubs(rule, persistencePorts, recommenderPorts);
		initializeUpdateAndHeartbeatStubs(rule);
	}

	private void initializeServiceQueryStubs(WireMockRule rule,
			List<Integer> persistencePorts, List<Integer> recommenderPorts)
			throws JsonProcessingException {
		List<String> persistences = new LinkedList<String>();
		for (int persistencePort: persistencePorts) {
			persistences.add("localhost:" + persistencePort);
		}
		String json = new ObjectMapper().writeValueAsString(persistences);
		rule.stubFor(WireMock.get(WireMock.urlEqualTo(
				"/tools.descartes.teastore.registry/rest/services/" + Service.PERSISTENCE.getServiceName() + "/"))
						.willReturn(WireMock.okJson(json)));
		List<String> recommenders = new LinkedList<String>();
		for (int recommenderPort: recommenderPorts) {
			recommenders.add("localhost:" + recommenderPort);
		}
		json = new ObjectMapper().writeValueAsString(recommenders);
		rule.stubFor(WireMock.get(WireMock.urlEqualTo(
				"/tools.descartes.teastore.registry/rest/services/" + Service.RECOMMENDER.getServiceName() + "/"))
						.willReturn(WireMock.okJson(json)));
	}
	
	private void initializeUpdateAndHeartbeatStubs(WireMockRule rule) {
		rule.stubFor(WireMock.put(WireMock.urlMatching(
				"/tools.descartes.teastore.registry/rest/services/.*"))
						.willReturn(WireMock.ok()));
		rule.stubFor(WireMock.delete(WireMock.urlMatching(
				"/tools.descartes.teastore.registry/rest/services/.*"))
						.willReturn(WireMock.ok()));
	}
	
	/**
	 * Create a mock persistence operating using a wiremock rule.
	 * Recommended: Use {@link #DEFAULT_MOCK_REGISTRY_PORT} as port for your rule.
	 * Assumes a persistence at the default {@link MockPersistenceProvider}.
	 * @param rule The wiremock rule to generate the mock stubs for.
	 * @param recommenderPorts Ports of the recommenders.
	 * @throws JsonProcessingException Exception on failure.
	 */
	public MockRegistry(WireMockRule rule, List<Integer> recommenderPorts)
			throws JsonProcessingException {
		this(rule, Arrays.asList(MockPersistenceProvider.DEFAULT_MOCK_PERSISTENCE_PORT),
				recommenderPorts);
	}

	/**
	 * Get the mock registry port.
	 * @return The port.
	 */
	public int getPort() {
		return this.port;
	}
}
