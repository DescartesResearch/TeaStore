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
package tools.descartes.petstore.image.setup;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.stream.Stream;

import tools.descartes.petstore.entities.ImageSize;

public class ImageCreator {
	
	public static final long STD_SEED = 12345;
	public static final int STD_NR_OF_SHAPES_PER_IMAGE = 10;
	public static final int MAX_RGB = 255;
	public static final int MAX_FONT_SIZE = 200;
	public static final int MAX_TEXT_LENGTH = 30;
	public static final int MAX_CHAR_SIZE = 255;
	
	private Random rand = new Random();
	private int shapesPerImage = STD_NR_OF_SHAPES_PER_IMAGE;
	
	public ImageCreator() {
		this(STD_NR_OF_SHAPES_PER_IMAGE);
	}
	
	public ImageCreator(int shapesPerImage) {
		this(shapesPerImage, STD_SEED);		
	}
	
	public ImageCreator(int shapesPerImage, long seed) {
		if (shapesPerImage < 1)
			throw new IllegalArgumentException("Number of shapes per image is below 1.");
		
		this.shapesPerImage = shapesPerImage;
		rand.setSeed(seed);
	}
	
	public BufferedImage createImage(ImageSize size) {
		BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.OPAQUE);
		Graphics2D graphics = img.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		switchColor(graphics);
		graphics.fillRect(0, 0, size.width, size.height);
		
		for (int i = 0; i < shapesPerImage; i++) {
			switch (rand.nextInt(4)) {
				case 0: makeRectangle(graphics, size); break;
				case 1: makeLine(graphics, size); break;
				case 2: makeOval(graphics, size); break;
				case 3: makeText(graphics, size); break;
			}
		}
		
		graphics.dispose();
		return img;
	}
	
	private void switchColor(Graphics2D graphics) {
		graphics.setColor(new Color(rand.nextInt(MAX_RGB + 1), rand.nextInt(MAX_RGB + 1), rand.nextInt(MAX_RGB + 1)));
	}
	
	private void makeRectangle(Graphics2D graphics, ImageSize maxSize) {
		switchColor(graphics);
		
		int x = rand.nextInt(maxSize.width);
		int y = rand.nextInt(maxSize.height);
		
		Rectangle r = new Rectangle(x, y, rand.nextInt(maxSize.width - x) + 1, 
				rand.nextInt(maxSize.height - y) + 1);
		
		if (rand.nextBoolean())
			graphics.fill(r);
		
		graphics.draw(r);
	}
	
	private void makeLine(Graphics2D graphics, ImageSize maxSize) {
		switchColor(graphics);
		
		graphics.drawLine(rand.nextInt(maxSize.width), rand.nextInt(maxSize.height), 
				rand.nextInt(maxSize.width), rand.nextInt(maxSize.height));
	}
	
	private void makeOval(Graphics2D graphics, ImageSize maxSize) {
		switchColor(graphics);
		
		int x = rand.nextInt(maxSize.width);
		int y = rand.nextInt(maxSize.height);
		int width = rand.nextInt(maxSize.width - x) + 1;
		int height = rand.nextInt(maxSize.height - y) + 1;
	
		if (rand.nextBoolean())
			graphics.fillOval(x, y, width, height);

		graphics.drawOval(x, y, width, height);
	}
	
	private void makeText(Graphics2D graphics, ImageSize maxSize) {
		switchColor(graphics);
		
		String fontName = Font.SANS_SERIF;
		switch (rand.nextInt(4)) {
			case 0: fontName = Font.SANS_SERIF; break;
			case 1: fontName = Font.MONOSPACED; break;
			case 2: fontName = Font.SERIF; break;
			case 3: fontName = Font.DIALOG; break;
		}
		
		int fontStyle = Font.PLAIN;
		switch (rand.nextInt(3)) {
			case 0: fontStyle = Font.PLAIN; break;
			case 1: fontStyle = Font.BOLD; break;
			case 2: fontStyle = Font.ITALIC; break;
		}
		
		int fontSize = rand.nextInt(MAX_FONT_SIZE + 1);
		
		graphics.setFont(new Font(fontName, fontStyle, fontSize));
		
		int textLength = rand.nextInt(MAX_TEXT_LENGTH + 1);
		String str = Stream.generate(() -> rand.nextInt(MAX_CHAR_SIZE))
				.limit(textLength)
				.map(i -> (char)i.intValue())
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
		
		graphics.drawString(str, rand.nextInt(maxSize.width), rand.nextInt(maxSize.height));
	}

}
