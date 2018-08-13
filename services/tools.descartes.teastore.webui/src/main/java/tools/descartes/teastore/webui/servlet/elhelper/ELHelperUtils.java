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
package tools.descartes.teastore.webui.servlet.elhelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Helper functions to call from JSP.
 * @author Joakim von Kistowski
 *
 */
public final class ELHelperUtils {

	/**
	 * The helper singleton to pass to EL.
	 */
	public static final ELHelperUtils  UTILS = new ELHelperUtils();

	private static final NumberFormat PRICE_FORMAT = new DecimalFormat("#0.00");
	
	private ELHelperUtils() {
		
	}
	
	/**
	 * Formats date.
	 * @param isoFormattedDate string containing date
	 * @return pretty formatted date
	 */
	public String formatToPrettyDate(String isoFormattedDate) {
		return LocalDateTime.parse(isoFormattedDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
				.format(DateTimeFormatter.ofPattern("yyyy MM dd - HH:mm:ss"));
	}
	
	/**
	 * Format price.
	 * @param price price to be formatted
	 * @return formatted price
	 */
	public String formatPriceInCents(long price) {
		return "&#36; " + PRICE_FORMAT.format((double) price / 100.0);
	}
	
}
