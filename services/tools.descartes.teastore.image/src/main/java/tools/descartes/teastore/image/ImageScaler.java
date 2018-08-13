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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.entities.ImageSize;

/**
 * Utility class with static methods to scale a
 * {@link java.awt.image.BufferedImage} to a given size or by a given ratio,
 * returning a copy of the {@link java.awt.image.BufferedImage} with the scaled
 * size and identical content.
 * 
 * @author Norbert Schmitt
 *
 */
public final class ImageScaler {

  private static Logger log = LoggerFactory.getLogger(ImageScaler.class);

  private ImageScaler() {
	  
  }
  
  /**
   * Scales a given image by rendering the supplied image to the size, determined
   * by the given {@link tools.descartes.teastore.entities.ImageSize}. The method
   * will throw a {@link java.lang.NullPointerException} if the supplied image or
   * size is null.
   * 
   * @param image
   *          Image to scale to the given size
   * @param size
   *          Size to scale image to
   * @return New image scaled to the given
   *         {@link tools.descartes.teastore.entities.ImageSize}
   */
  public static BufferedImage scale(BufferedImage image, ImageSize size) {
    if (size == null) {
      log.error("The supplied image size is null.");
      throw new NullPointerException("The supplied image size is null.");
    }

    return scale(image, size.getWidth(), size.getHeight());
  }

  /**
   * Scales a given image by rendering the supplied image by the given scaling
   * factor. Width and height are scaled by the same factor. Ratios above one will
   * result in larger images and ratios below one will result in smaller images.
   * The new image size is calculated by multiplying the ratio with the old image
   * size. Numbers behind the decimal point will dropped (integer arithmetics).
   * The method will throw an {@link java.lang.IllegalArgumentException} if the
   * scaling factor is 0 or below. A {@link java.lang.NullPointerException} will
   * be thrown if the supplied image is null.
   * 
   * @param image
   *          Image to scale by the given ratio
   * @param scalingFactor
   *          Ratio to scale image
   * @return New image scaled by the given ratio
   */
  public static BufferedImage scale(BufferedImage image, double scalingFactor) {
    if (scalingFactor <= 0.0) {
      log.error("The supplied scaling factor is 0 or below.");
      throw new IllegalArgumentException("The supplied scaling factor is 0 or below.");
    }

    return scale(image, scalingFactor, scalingFactor);
  }

  /**
   * Scales a given image by rendering the supplied image by the two given scaling
   * factors for width and height. Width and height are scaled independently.
   * Ratios above one will result in larger images and ratios below one will
   * result in smaller images. The new image size is calculated by multiplying the
   * ratio with the old image size. Numbers behind the decimal point will dropped
   * (integer arithmetics). The method will throw an
   * {@link java.lang.IllegalArgumentException} if either of the two scaling
   * factors is 0 or below. A {@link java.lang.NullPointerException} will be
   * thrown if the supplied image is null.
   * 
   * @param image
   *          Image to scale by the two given ratio
   * @param widthScaling
   *          Ratio to scale image width
   * @param heightScaling
   *          Ratio to scale image height
   * @return New image scaled by the given ratios
   */
  public static BufferedImage scale(BufferedImage image, double widthScaling,
      double heightScaling) {
    if (widthScaling <= 0.0) {
      log.error("The supplied width scaling factor is 0 or below.");
      throw new IllegalArgumentException("The supplied width scaling factor is 0 or below.");
    }
    if (heightScaling <= 0.0) {
      log.error("The supplied height scaling factor is 0 or below.");
      throw new IllegalArgumentException("The supplied height scaling factor is 0 or below.");
    }

    int newWidth = (int) (image.getWidth() * widthScaling);
    if (newWidth == 0) {
      newWidth = 1;
    }
    
    int newHeight = (int) (image.getHeight() * heightScaling);
    if (newHeight == 0) {
      newHeight = 1;
    }
    
    return scale(image, newWidth, newHeight);
  }

  /**
   * Scales a given image by rendering the supplied image to the given size. The
   * method will return a new image with the given size as width and height. The
   * method will throw an {@link java.lang.IllegalArgumentException} if the pixel
   * size is 0 or below. A {@link java.lang.NullPointerException} will be thrown
   * if the supplied image is null
   * 
   * @param image
   *          Image to scale to the given size
   * @param size
   *          Size to scale image to in pixel
   * @return New image scaled to the given size
   */
  public static BufferedImage scale(BufferedImage image, int size) {
    if (size <= 0) {
      log.error("The supplied pixel size is below 1.");
      throw new IllegalArgumentException("The supplied pixel size is below 1.");
    }

    return scale(image, size, size);
  }

  /**
   * Scales a given image by rendering the supplied image with the given width and
   * height into a new image and returning the new image. The method will throw an
   * {@link java.lang.IllegalArgumentException} if either of the two image sizes
   * is 0 or below. A {@link java.lang.NullPointerException} will be thrown if the
   * supplied image is null.
   * 
   * @param image
   *          Image to scale to the given width and height
   * @param width
   *          Width to scale image to
   * @param height
   *          Height to scale image to
   * @return New image scaled to the given width and height
   */
  public static BufferedImage scale(BufferedImage image, int width, int height) {
    if (image == null) {
      log.error("The supplied image is null.");
      throw new NullPointerException("The supplied image is null.");
    }
    if (width <= 0) {
      log.error("The supplied pixel width is below 1.");
      throw new IllegalArgumentException("The supplied pixel width is below 1.");
    }
    if (height <= 0) {
      log.error("The supplied pixel height is below 1.");
      throw new IllegalArgumentException("The supplied pixel height is below 1.");
    }

    BufferedImage scaledImg = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
    Graphics2D graphics = scaledImg.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    graphics.drawImage(image, 0, 0, width, height, null);
    graphics.dispose();
    return scaledImg;
  }
}
