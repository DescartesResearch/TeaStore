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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.entities.ImageSize;
import tools.descartes.teastore.image.cache.entry.ICachable;

/**
 * Entity for images provided by the image-provider service.
 * 
 * @author Norbert Schmitt
 *
 */
public class StoreImage implements ICachable<StoreImage> {

  /**
   * Standard image format for storage (reading and writing).
   */
  public static final String STORE_IMAGE_FORMAT = "png";

  /**
   * String prepended in the
   * 
   * <pre>
   * src
   * </pre>
   * 
   * attribute of an image tag for embedding the image data.
   */
  public static final String STORE_IMAGE_DATA_STRING = "data:image/" + STORE_IMAGE_FORMAT
      + ";base64,";

  private final long id;
  private byte[] data;
  private ImageSize size;
  private Logger log = LoggerFactory.getLogger(StoreImage.class);

  /**
   * Creates a new store image with a given id and size. The image is converted
   * from the Java internal representation to a base64 encoded byte array.
   * 
   * @param id
   *          The unique image id.
   * @param image
   *          The image data itself.
   * @param size
   *          The image size.
   */
  public StoreImage(final long id, BufferedImage image, ImageSize size) {
    if (image == null) {
      log.error("Supplied image is null.");
      throw new NullPointerException("Supplied image is null.");
    }

    this.id = id;
    setImage(image);
    setImageSize(size);
  }

  /**
   * Constructor.
   * @param id imageId
   * @param base64 base64 encoded byte array
   * @param size Imagesize object
   */
  public StoreImage(final long id, byte[] base64, ImageSize size) {
    if (base64 == null) {
      log.error("Supplied base64 encoded byte array is null.");
      throw new NullPointerException("Supplied base64 encoded byte array is null.");
    }

    this.id = id;
    data = Arrays.copyOf(base64, base64.length);
    setImageSize(size);
  }

  private void setImageSize(ImageSize size) {
    if (size == null) {
      log.error("Supplied image size is null.");
      throw new NullPointerException("Supplied image size is null.");
    }
    this.size = size;
  }

  /**
   * Copy constructor for StoreImage.
   * 
   * @param image
   *          Image to copy.
   */
  public StoreImage(StoreImage image) {
    if (image == null) {
      log.error("Supplied store image is null.");
      throw new NullPointerException("Store image is null.");
    }

    this.id = image.getId();
    this.data = Arrays.copyOf(image.getByteArray(), image.getByteArray().length);
    this.size = image.getSize();
  }

  /**
   * Returns the unique image identifier.
   * 
   * @return Unique image id.
   */
  public long getId() {
    return id;
  }

  // Converts the Java internal image representation to a byte array and encodes
  // it in base64 for embedding.
  private void setImage(BufferedImage image) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, STORE_IMAGE_FORMAT, stream);
    } catch (IOException ioException) {
      log.warn("An IOException occured while trying to write image to a stream.", ioException);
    }
    data = Base64.getEncoder().encode(stream.toByteArray());
  }

  /**
   * Returns the image as the Java internal representation.
   * 
   * @return The image itself.
   */
  public BufferedImage getImage() {
    BufferedImage image = null;
    ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
    try {
      image = ImageIO.read(stream);
    } catch (IOException ioException) {
      log.warn("An IOException occured while trying to read image from stream.", ioException);
    }
    return image;
  }

  /**
   * Returns the image size.
   * 
   * @return The image size.
   */
  public ImageSize getSize() {
    return size;
  }

  /**
   * Returns the number of bytes stored in the internal data structure.
   * 
   * @return Number of bytes stored for this image.
   */
  @Override
  public long getByteSize() {
    return data.length;
  }

  /**
   * Returns the base64 encoded byte array as string.
   * 
   * @return Base64 encoded data as string.
   */
  public String getBase64() {
    return new String(data);
  }

  /**
   * Getter for bytearray.
   * @return bytearray
   */
  public byte[] getByteArray() {
    return Arrays.copyOf(data, data.length);
  }

  @Override
  public String toString() {
    return STORE_IMAGE_DATA_STRING + getBase64();
  }

  // Auto-generated
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    StoreImage other = (StoreImage) obj;
    if (id != other.getId()) {
      return false;
    }
    return true;
  }
}
