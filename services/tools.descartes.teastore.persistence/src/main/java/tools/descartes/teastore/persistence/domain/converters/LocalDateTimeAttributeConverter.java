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
package tools.descartes.teastore.persistence.domain.converters;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter for persisting Java8 LocalDateTime in database.
 * @author Joakim von Kistowski
 *
 */
@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {
	
	/**
	 * Converts LocalDateTime to a supported format.
	 * @param locDateTime The date time to convert.
	 * @return The date time in a supported format.
	 */
	@Override
	public Timestamp convertToDatabaseColumn(LocalDateTime locDateTime) {
		if (locDateTime == null) {
			return null;
		}
		return Timestamp.valueOf(locDateTime);
	}

	
	/**
	 * Converts database format to LocalDateTime.
	 * @param sqlTimestamp The date time to convert.
	 * @return The date as LocalDateTime.
	 */
	@Override
	public LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
		if (sqlTimestamp == null) {
			return null;
		}
		return sqlTimestamp.toLocalDateTime();
	}
}
