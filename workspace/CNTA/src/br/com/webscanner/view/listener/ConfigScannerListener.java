package br.com.webscanner.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ProductScannerController;

public class ConfigScannerListener implements ActionListener {
	private static Logger logger = LogManager.getLogger(DocumentImportListener.class.getName());

	private ProductScannerController controller;
	
	public ConfigScannerListener(ProductScannerController controller) {
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.setEnableActions(false);
		controller.showConfigScannerDialog();
		controller.setEnableActions(true);
	}
}
