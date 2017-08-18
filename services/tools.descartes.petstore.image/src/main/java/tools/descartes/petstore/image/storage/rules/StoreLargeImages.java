package tools.descartes.petstore.image.storage.rules;

import java.util.function.Predicate;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.image.StoreImage;

public class StoreLargeImages implements Predicate<StoreImage> {

	@Override
	public boolean test(StoreImage t) {
		return t.getSize().equals(ImageSize.FULL);
	}

}
