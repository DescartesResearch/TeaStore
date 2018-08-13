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
package tools.descartes.teastore.image.setup;

import java.util.Arrays;

/**
 * This enum contains the different storage rule implementations and their string representation.
 * @author Norbert Schmitt
 */
public enum StorageRule {

  /**
   * Store all data without restrictions.
   */
  ALL("All"), 
  
  /** 
   * Store only images if their size is equal to {@link tools.descartes.teastore.entities.ImageSizePreset.FULL}.
   */
  FULL_SIZE_IMG("Full-size-images");

  /**
   * Standard storage rule implementation used by the image provider service.
   */
  public static final StorageRule STD_STORAGE_RULE = ALL;

  private final String strRepresentation;

  private StorageRule(String strRepresentation) {
    this.strRepresentation = strRepresentation;
  }
  
  /**
   * Returns the string representation of the used storage rule implementation.
   * @return String representation.
   */
  public String getStrRepresentation() {
    return strRepresentation;
  }

  /**
   * Convert string representation to the correct object. Will return the standard storage rule implementation if the 
   * string representation is unknown.
   * @param strStorageRule String representation of the storage rule implementation.
   * @return Enum value of the storage rule implementation.
   */
  public static StorageRule getStorageRuleFromString(String strStorageRule) {
    return Arrays.asList(StorageRule.values()).stream()
        .filter(mode -> mode.strRepresentation.equals(strStorageRule)).findFirst()
        .orElse(STD_STORAGE_RULE);
  }
}
