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

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter for persisting Java8 LocalDate in database.
 * @author Joakim von Kistowski
 *
 */
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {
	
	/**
	 * Converts LocalDate to a supported format.
	 * @param locDate The date to convert.
	 * @return The date in a supported format.
	 */
    @Override
    public Date convertToDatabaseColumn(LocalDate locDate) {
    	if (locDate == null) {
    		return null;
    	}
    	return Date.valueOf(locDate);
    }

    /**
	 * Converts database format to LocalDate.
	 * @param sqlDate The date to convert.
	 * @return The date as LocalDate.
	 */
    @Override
    public LocalDate convertToEntityAttribute(Date sqlDate) {
    	if (sqlDate == null) {
    		return null;
    	}
    	return sqlDate.toLocalDate();
    }
}
