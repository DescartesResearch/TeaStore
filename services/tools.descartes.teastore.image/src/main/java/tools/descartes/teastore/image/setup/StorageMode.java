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
 * This enum contains the different storage implementations and their string representation.
 * @author Norbert Schmitt
 */
public enum StorageMode {

  /**
   * Store all images on the pyhsical drive.
   */
  DRIVE("Drive");

  /**
   * Standard storage implementation used by the image provider service.
   */
  public static final StorageMode STD_STORAGE_MODE = DRIVE;

  private final String strRepresentation;

  private StorageMode(String strRepresentation) {
    this.strRepresentation = strRepresentation;
  }

  /**
   * Returns the string representation of the used storage implementation.
   * @return String representation.
   */
  public String getStrRepresentation() {
    return strRepresentation;
  }

  /**
   * Convert string representation to the correct object. Will return the standard storage implementation if the 
   * string representation is unknown.
   * @param strStorageMode String representation of the storage implementation.
   * @return Enum value of the storage implementation.
   */
  public static StorageMode getStorageModeFromString(String strStorageMode) {
    return Arrays.asList(StorageMode.values()).stream()
        .filter(mode -> mode.strRepresentation.equals(strStorageMode)).findFirst()
        .orElse(STD_STORAGE_MODE);
  }
}
