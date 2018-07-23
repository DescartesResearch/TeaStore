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
package tools.descartes.teastore.image;

/**
 * Entry for an image database ({@link tools.descartes.teastore.image.ImageDB}). 
 * @author Norbert Schmitt
 */
public class ImageDBKey {

  private final long productID;
  private final String webuiName;
  private final boolean isProductKey;

  /**
   * This entry will represent a product id in the image database.
   * @param productID Product id.
   */
  public ImageDBKey(long productID) {
    this.productID = productID;
    webuiName = null;
    isProductKey = true;
  }

  /**
   * This entry will represents a product id in the image database.
   * @param webuiName Web UI image name.
   */
  public ImageDBKey(String webuiName) {
    this.webuiName = webuiName;
    productID = 0;
    isProductKey = false;
  }

  /**
   * Checks whether this image database entry represents a product or a static image for the web interface. 
   * @return True if this image database entry represents a product id, otherwise false.
   */
  public boolean isProductKey() {
    return isProductKey;
  }

  /**
   * Returns the stored product id this entry represents or zero if it represents a web interface entry.
   * @return Product id or zero if it is a web interface entry.
   */
  public long getProductID() {
    return productID;
  }

  /**
   * Returns the stored web interface name or NULL if it represents a product id.
   * @return Web interface image name or NULL if it represents a product id.
   */
  public String getWebUIName() {
    return webuiName;
  }

}
