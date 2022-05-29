package br.com.webscanner.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import br.com.webscanner.controller.ContrastConfigurationController;
import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.controller.ScannerChooserController;
import br.com.webscanner.model.domain.Scanner;

public class ContrastConfigurationListener implements ActionListener {
	private ProductScannerController controller;
	private ScannerChooserController scannerChooserController;
	
	public ContrastConfigurationListener(ProductScannerController controller, ScannerChooserController scannerChooserController) {
		this.controller = controller;
		this.scannerChooserController = scannerChooserController;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		controller.showLoading(true);

		Scanner scanner = scannerChooserController.getValidScannerConfigured();
		if (scanner == null){
			controller.showLoading(true);
			scannerChooserController.showScannerChooser();
		} else {
			ContrastConfigurationController contrastConfigurationController = new ContrastConfigurationController(this.controller);
			contrastConfigurationController.showContrastConfiguration();
		}
	}
}
