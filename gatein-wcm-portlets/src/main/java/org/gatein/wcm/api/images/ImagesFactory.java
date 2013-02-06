package org.gatein.wcm.api.images;

public class ImagesFactory {

	public static ImagesAPI getImages() {
		// return MemoryImagesImpl.init();
		return new JcrImagesImpl();
	}

}
