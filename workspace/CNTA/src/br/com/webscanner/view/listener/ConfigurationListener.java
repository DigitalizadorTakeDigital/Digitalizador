package br.com.webscanner.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.controller.ScannerChooserController;
import br.com.webscanner.view.ProductScanner;

/**
 * Classe que define a ação do botão de Configurações do Scanner.
 * @author Fernando Germano
 */
public class ConfigurationListener implements ActionListener {
	
	private Logger logger = LogManager.getLogger(ConfigurationListener.class.getName());
	private ProductScanner productScanner;
	private ProductScannerController controller;
	
	public ConfigurationListener(ProductScanner productScanner, ProductScannerController controller) {
		this.productScanner = productScanner;
		this.controller = controller;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		logger.info("Exibindo tela de configurações");
		
		controller.showLoading(true);
		ScannerChooserController scannerChooserController = new ScannerChooserController(productScanner, this.controller);
		scannerChooserController.showScannerChooser();
	}
}
