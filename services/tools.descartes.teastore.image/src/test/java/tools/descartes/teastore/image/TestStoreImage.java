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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.teastore.entities.ImageSizePreset;

public class TestStoreImage {

  private static final String IMG_STRING = "data:image/png;base64,";
  private static final String IMG_DATA = "iVBORw0KGgoAAAANSUhEUgAAAM0AAADNCAMAAAAsYgRbAAAAGXRFWHRTb2Z0d"
      + "2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAABJQTFRF3NSmzMewPxIG//ncJEJsldTou1jHgAAAARBJREFUeNrs2EE"
      + "KgCAQBVDLuv+V20dENbMY831wKz4Y/VHb/5RGQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0PzMWtya"
      + "GhoaGhoaGhoaGhoaGhoxtb0QGhoaGhoaGhoaGhoaGhoaMbRLEvv50VTQ9OTQ5OpyZ01GpM2g0bfmDQaL7S+ofFC6xv"
      + "3ZpxJiywakzbvd9r3RWPS9I2+MWk0+kbf0Hih9Y17U0nTHibrDDQ0NDQ0NDQ0NDQ0NDQ0NTXbRSL/AK72o6GhoaGho"
      + "RlL8951vwsNDQ0NDQ1NDc0WyHtDTEhDQ0NDQ0NTS5MdGhoaGhoaGhoaGhoaGhoaGhoaGhoaGposzSHAAErMwwQ2HwR"
      + "QAAAAAElFTkSuQmCC";
  private static final long IMG_ID0 = 0L;
  private static final long IMG_ID1 = 6648764502374L;

  private BufferedImage img;
  @Mock
  private StoreImage mockedImg0;
  @Mock
  private StoreImage mockedImg1;

  private BufferedImage convertToImage(byte[] data) {
    ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(data));
    BufferedImage bi = null;
    try {
      bi = ImageIO.read(bais);
    } catch (IOException e) {

    }
    return bi;
  }

  @Before
  public void initialize() {
    MockitoAnnotations.openMocks(this);
    when(mockedImg0.getByteSize()).thenReturn((long) IMG_DATA.getBytes().length);
    when(mockedImg0.getByteArray()).thenReturn(IMG_DATA.getBytes());
    when(mockedImg0.getId()).thenReturn(IMG_ID0);
    when(mockedImg0.getImage()).thenReturn(img);
    when(mockedImg1.getId()).thenReturn(IMG_ID1);

    img = convertToImage(IMG_DATA.getBytes());
    if (img == null)
      throw new NullPointerException();
  }

  @Test
  public void testConstructor() {
    new StoreImage(IMG_ID0, img, ImageSizePreset.ICON.getSize());
    new StoreImage(IMG_ID0, IMG_DATA.getBytes(), ImageSizePreset.ICON.getSize());
    new StoreImage(mockedImg0);
  }

  @Test(expected = NullPointerException.class)
  public void testByteArrayConstructorNull() {
    new StoreImage(IMG_ID0, (byte[]) null, ImageSizePreset.ICON.getSize());
  }

  @Test(expected = NullPointerException.class)
  public void testImageConstructorNull() {
    new StoreImage(IMG_ID0, (BufferedImage) null, ImageSizePreset.ICON.getSize());
  }

  @Test(expected = NullPointerException.class)
  public void testByteArrayConstructorSizeNull() {
    new StoreImage(IMG_ID0, IMG_DATA.getBytes(), null);
  }

  @Test(expected = NullPointerException.class)
  public void testImageConstructorSizeNull() {
    new StoreImage(IMG_ID0, img, null);
  }

  @Test(expected = NullPointerException.class)
  public void testCopyConstructorNull() {
    new StoreImage(null);
  }

  @Test
  public void testGetID() {
    StoreImage uut = new StoreImage(IMG_ID1, img, ImageSizePreset.ICON.getSize());
    assertEquals(IMG_ID1, uut.getId());
  }

  @Test
  public void testGetImage() {
    StoreImage uut = new StoreImage(IMG_ID1, IMG_DATA.getBytes(), ImageSizePreset.ICON.getSize());
    BufferedImage bi = uut.getImage();
    if (bi == null)
      fail();
    assertEquals(bi.getWidth(), img.getWidth());
    assertEquals(bi.getHeight(), img.getHeight());
  }

  @Test
  public void testGetSize() {
    StoreImage uut = new StoreImage(IMG_ID1, IMG_DATA.getBytes(), ImageSizePreset.ICON.getSize());
    assertEquals(ImageSizePreset.ICON.getSize(), uut.getSize());
  }

  @Test
  public void testGetByteSize() {
    StoreImage uut = new StoreImage(IMG_ID1, IMG_DATA.getBytes(), ImageSizePreset.ICON.getSize());
    assertEquals(IMG_DATA.getBytes().length, uut.getByteSize());
  }

  @Test
  public void testGetBase64() {
    StoreImage uut = new StoreImage(IMG_ID1, IMG_DATA.getBytes(), ImageSizePreset.ICON.getSize());
    assertEquals(IMG_DATA, uut.getBase64());
  }

  @Test
  public void testGetByteArray() {
    StoreImage uut = new StoreImage(IMG_ID1, IMG_DATA.getBytes(), ImageSizePreset.ICON.getSize());
    assertArrayEquals(IMG_DATA.getBytes(), uut.getByteArray());
  }

  @Test
  public void testToString() {
    StoreImage uut = new StoreImage(IMG_ID1, IMG_DATA.getBytes(), ImageSizePreset.ICON.getSize());
    assertEquals(IMG_STRING + IMG_DATA, uut.toString());
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void testEquals() {
    StoreImage uut = new StoreImage(IMG_ID1, IMG_DATA.getBytes(), ImageSizePreset.ICON.getSize());
    StoreImage test = new StoreImage(uut);
    assertTrue(uut.equals(uut));
    assertFalse(uut.equals(null));
    assertFalse(uut.equals(mockedImg0));
    assertTrue(uut.equals(test));
    assertFalse(uut.equals(img));
  }

  @Test
  public void testHashCode() {
    StoreImage uut = new StoreImage(IMG_ID1, IMG_DATA.getBytes(), ImageSizePreset.ICON.getSize());
    int result = 31 + (int) (IMG_ID1 ^ (IMG_ID1 >>> 32));
    assertEquals(uut.hashCode(), result);
  }
}
