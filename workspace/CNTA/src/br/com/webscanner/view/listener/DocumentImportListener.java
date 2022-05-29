/**
 * 
 */
package br.com.webscanner.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ProductScannerController;

/**
 * @author fernando.germano
 *
 */
public class DocumentImportListener implements ActionListener {
	private static Logger logger = LogManager.getLogger(DocumentImportListener.class.getName());

	private ProductScannerController controller;
	
	public DocumentImportListener(ProductScannerController controller) {
		this.controller = controller;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		controller.setEnableActions(false);
		controller.showImportDialog();
		controller.setEnableActions(true);
	}
}
