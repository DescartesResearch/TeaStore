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

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import tools.descartes.teastore.entities.ImageSize;
import tools.descartes.teastore.entities.ImageSizePreset;

public class TestImageScaler {

  private static final String IMG_DATA = "iVBORw0KGgoAAAANSUhEUgAAAM0AAADNCAMAAAAsYgRbAAAAGXRFWHRTb2Z0d"
      + "2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAABJQTFRF3NSmzMewPxIG//ncJEJsldTou1jHgAAAARBJREFUeNrs2EE"
      + "KgCAQBVDLuv+V20dENbMY831wKz4Y/VHb/5RGQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0PzMWtya"
      + "GhoaGhoaGhoaGhoaGhoxtb0QGhoaGhoaGhoaGhoaGhoaMbRLEvv50VTQ9OTQ5OpyZ01GpM2g0bfmDQaL7S+ofFC6xv"
      + "3ZpxJiywakzbvd9r3RWPS9I2+MWk0+kbf0Hih9Y17U0nTHibrDDQ0NDQ0NDQ0NDQ0NDQ0NTXbRSL/AK72o6GhoaGho"
      + "RlL8951vwsNDQ0NDQ1NDc0WyHtDTEhDQ0NDQ0NTS5MdGhoaGhoaGhoaGhoaGhoaGhoaGhoaGposzSHAAErMwwQ2HwR"
      + "QAAAAAElFTkSuQmCC";

  private BufferedImage img;
  private int widthBefore;
  private int heightBefore;

  @Before
  public void initialize() {
    ByteArrayInputStream bais = new ByteArrayInputStream(
        Base64.getDecoder().decode(IMG_DATA.getBytes()));
    try {
      img = ImageIO.read(bais);
    } catch (IOException e) {
      System.out.println("IOException while reading from input stream. Message: " + e.getMessage());
      e.printStackTrace();
    }
    widthBefore = img.getWidth();
    heightBefore = img.getHeight();
  }

  // We can only test that it scales correctly, it is not really feasible to test
  // for the correct content.
  @Test
  public void testScale() {
    BufferedImage uut = ImageScaler.scale(img, ImageSizePreset.FULL.getSize());
    assertEquals(ImageSizePreset.FULL.getSize().getWidth(), uut.getWidth());
    assertEquals(ImageSizePreset.FULL.getSize().getHeight(), uut.getHeight());
    uut = ImageScaler.scale(img, ImageSizePreset.ICON.getSize());
    assertEquals(ImageSizePreset.ICON.getSize().getWidth(), uut.getWidth());
    assertEquals(ImageSizePreset.ICON.getSize().getHeight(), uut.getHeight());

    uut = ImageScaler.scale(img, 2.2);
    assertEquals((int) (widthBefore * 2.2), uut.getWidth());
    assertEquals((int) (heightBefore * 2.2), uut.getHeight());
    uut = ImageScaler.scale(img, 0.33);
    assertEquals((int) (widthBefore * 0.33), uut.getWidth());
    assertEquals((int) (heightBefore * 0.33), uut.getHeight());
    uut = ImageScaler.scale(img, 1.0);
    assertEquals(widthBefore, uut.getWidth());
    assertEquals(heightBefore, uut.getHeight());
    uut = ImageScaler.scale(img, 0.001);
    assertEquals(1, uut.getWidth());
    assertEquals(1, uut.getHeight());

    uut = ImageScaler.scale(img, 1.0, 2.5);
    assertEquals(widthBefore, uut.getWidth());
    assertEquals((int) (heightBefore * 2.5), uut.getHeight());
    uut = ImageScaler.scale(img, 2.5, 1.0);
    assertEquals((int) (widthBefore * 2.5), uut.getWidth());
    assertEquals(heightBefore, uut.getHeight());
    uut = ImageScaler.scale(img, 0.5, 4.0);
    assertEquals((int) (widthBefore * 0.5), uut.getWidth());
    assertEquals((int) (heightBefore * 4.0), uut.getHeight());
    uut = ImageScaler.scale(img, 4.0, 0.5);
    assertEquals((int) (widthBefore * 4.0), uut.getWidth());
    assertEquals((int) (heightBefore * 0.5), uut.getHeight());

    uut = ImageScaler.scale(img, 807);
    assertEquals(807, uut.getWidth());
    assertEquals(807, uut.getHeight());
    uut = ImageScaler.scale(img, 122);
    assertEquals(122, uut.getWidth());
    assertEquals(122, uut.getHeight());
    uut = ImageScaler.scale(img, widthBefore);
    assertEquals(widthBefore, uut.getWidth());
    assertEquals(widthBefore, uut.getHeight());

    uut = ImageScaler.scale(img, widthBefore, 654);
    assertEquals(widthBefore, uut.getWidth());
    assertEquals(654, uut.getHeight());
    uut = ImageScaler.scale(img, 654, heightBefore);
    assertEquals(654, uut.getWidth());
    assertEquals(heightBefore, uut.getHeight());
    uut = ImageScaler.scale(img, 111, 555);
    assertEquals(111, uut.getWidth());
    assertEquals(555, uut.getHeight());
    uut = ImageScaler.scale(img, 555, 111);
    assertEquals(555, uut.getWidth());
    assertEquals(111, uut.getHeight());
    uut = ImageScaler.scale(img, 1, 1);
    assertEquals(1, uut.getWidth());
    assertEquals(1, uut.getHeight());
  }

  @Test(expected = NullPointerException.class)
  public void testScaleImageSizeNull() {
    ImageScaler.scale(img, (ImageSize) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testScaleFactorZero() {
    ImageScaler.scale(img, 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testScaleFactorBelowZero() {
    ImageScaler.scale(img, -1.34);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testScaleFactorTwoZero() {
    ImageScaler.scale(img, 0.0, 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testScaleFactorTwoSingleBelowZero() {
    ImageScaler.scale(img, 1.5, -2.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testScaleFactorTwoBelowZero() {
    ImageScaler.scale(img, -1.22, -2.22);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testScalePixelZero() {
    ImageScaler.scale(img, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testScalePixelBelowOne() {
    ImageScaler.scale(img, -4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testScalePixelTwoZero() {
    ImageScaler.scale(img, 0, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testScalePixelTwoSIngleBelowZero() {
    ImageScaler.scale(img, 5, -6);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testScalePixelTwoBelowOne() {
    ImageScaler.scale(img, -4, -6);
  }

  @Test(expected = NullPointerException.class)
  public void testScaleImageNull() {
    ImageScaler.scale(null, 50);
  }
}
