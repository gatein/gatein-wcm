package org.gatein.wcm.api.images;

import java.util.List;

import org.apache.commons.fileupload.FileItem;

public interface ImagesAPI {
	
	Image getImage(String key);
	void setImage(String key, FileItem file);
	void removeImage(String key);
	public List<String> getImages();
	
}
