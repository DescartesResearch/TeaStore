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
package tools.descartes.petsupplystore.image.setup;

import java.util.Arrays;

public enum CachingMode {

	FIFO("FIFO"),
	LIFO("LIFO"),
	RR("RR"),
	LFU("LFU"),
	LRU("LRU"),
	MRU("MRU"),
	NONE("Disabled");
	
	public static final CachingMode STD_CACHING_MODE = LFU;
	
	private final String strRepresentation;
	
	private CachingMode(String strRepresentation) {
		this.strRepresentation = strRepresentation;
	}
	
	public String getStrRepresentation() {
		return new String(strRepresentation);
	}
	
	public static CachingMode getCachingModeFromString(String strCachingMode) {
		return Arrays.asList(CachingMode.values()).stream()
				.filter(mode -> mode.strRepresentation.equals(strCachingMode))
				.findFirst()
				.orElse(STD_CACHING_MODE);
	}
}
