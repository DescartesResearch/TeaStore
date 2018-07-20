package tools.descartes.teastore.entities;

/**
 * Available store image sizes.
 * 
 * @author Norbert Schmitt
 *
 */
public class ImageSize {

  /**
   * Divider string.
   */
  public static final String IMAGE_SIZE_DIVIDER = "x";

  /**
   * Image width.
   */
  private int width;

  /**
   * Image height.
   */
  private int height;

  /**
   * Default constructor.
   */
  public ImageSize() {
  }

  /**
   * Constructor with variable width and height.
   * 
   * @param width
   *          imageWidth
   * @param height
   *          imageHeight
   */
  public ImageSize(int width, int height) {
    setWidth(width);
    setHeight(height);
  }

  /**
   * Constructor with ImageSize object.
   * 
   * @param size
   *          ImageSize object
   */
  public ImageSize(ImageSize size) {
    setWidth(size.getWidth());
    setHeight(size.getHeight());
  }

  /**
   * Getter for image width.
   * @return image width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Setter for image width.
   * @param width image width
   */
  public void setWidth(int width) {
    if (width <= 0) {
      throw new IllegalArgumentException("Image width cannot be zero or negative.");
    }
    this.width = width;
  }

  /**
   * Getter for image height.
   * @return image height.
   */
  public int getHeight() {
    return height;
  }

  /**
   * Setter for image height.
   * @param height image height
   */
  public void setHeight(int height) {
    if (height <= 0) {
      throw new IllegalArgumentException("Image height cannot be zero or negative.");
    }
    this.height = height;
  }

  /**
   * Calculates number of pixels of an image.
   * @return number of pixels
   */
  public int getPixelCount() {
    return width * height;
  }

  /**
   * Parses from String.
   * @param str String to pars from
   * @return ImageSize object
   */
  public static ImageSize parseImageSize(String str) {
    if (str == null) {
      throw new NullPointerException("Supplied string is null.");
    }
    if (str.isEmpty()) {
      throw new IllegalArgumentException("Supplied string is empty.");
    }

    String[] tmp = str.trim().split(IMAGE_SIZE_DIVIDER);
    if (tmp.length != 2) {
      throw new IllegalArgumentException("Malformed string supplied. Does not contain exactly two size "
          + "values divided by \"" + IMAGE_SIZE_DIVIDER + "\".");
    }

    int width = 0;
    int height = 0;

    try {
      width = Integer.parseInt(tmp[0].trim());
      height = Integer.parseInt(tmp[1].trim());
    } catch (NumberFormatException parseException) {
      throw new IllegalArgumentException("Malformed string supplied. Cannot parse size values.");
    }

    return new ImageSize(width, height);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + height;
    result = prime * result + width;
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

    ImageSize other = (ImageSize) obj;
    if (height != other.height) {
      return false;
    }
    if (width != other.width) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.valueOf(width) + IMAGE_SIZE_DIVIDER + String.valueOf(height);
  }
}
