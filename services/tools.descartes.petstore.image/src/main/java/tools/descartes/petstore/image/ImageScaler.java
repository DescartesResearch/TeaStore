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
package tools.descartes.petstore.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import tools.descartes.petstore.entities.ImageSize;

public class ImageScaler {
	
	public static BufferedImage scale(BufferedImage image, ImageSize size) {
		return scale(image, size.width, size.height);
	}
	
	public static BufferedImage scale(BufferedImage image, double scalingFactor) {
		return scale(image, scalingFactor, scalingFactor);
	}
	
	public static BufferedImage scale(BufferedImage image, double widthScaling, double heightScaling) {
		return scale(image, (int)(image.getWidth() * widthScaling), (int)(image.getHeight() * heightScaling));
	}
	
	public static BufferedImage scale(BufferedImage image, int size) {
		return scale(image, size, size);
	}

	/**
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage scale(BufferedImage image, int width, int height) {
		BufferedImage scaledImg = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics2D graphics = scaledImg.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics.drawImage(image, 0, 0, width, height, null);
		graphics.dispose();
		return scaledImg;
	}
}
