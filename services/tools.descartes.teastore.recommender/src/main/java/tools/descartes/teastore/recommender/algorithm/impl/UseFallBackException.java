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
package tools.descartes.teastore.recommender.algorithm.impl;

/**
 * This exception signals a (mostly) conceptual error or lack of information,
 * why the current recommending approach can not be applied. Therefore the
 * calling instance should go for the robust fall-back solution in this case.
 * 
 * @author Johannes
 *
 */
public class UseFallBackException extends RuntimeException {

	/**
	 * @param string
	 *            The error message.
	 */
	public UseFallBackException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2503876420753158905L;

}
