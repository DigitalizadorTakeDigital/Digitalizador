package br.com.webscanner.controller;

import java.io.IOException;

import br.com.webscanner.controller.ImageViewerController.Type;
import br.com.webscanner.model.domain.Content;
import br.com.webscanner.view.ImageActionsBar;

public class ImageActionBarController {
	private ImageActionsBar imageActionsBar;
	
	public ImageActionBarController(ImageActionsBar imageActionsBar) {
		this.imageActionsBar = imageActionsBar;
	}

	public void showActionBar(Content content, Type type) throws IOException {
		imageActionsBar.showActionBar(content, type);
	}
}