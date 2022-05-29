package br.com.webscanner.view.listener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import br.com.webscanner.controller.ProductScannerController;

public class MessageListener implements PropertyChangeListener {

	private ProductScannerController controller;

	public MessageListener(ProductScannerController controller) {
		this.controller = controller;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("message")) {
			controller.updateMessage(evt.getNewValue().toString());
		}
	}
}
