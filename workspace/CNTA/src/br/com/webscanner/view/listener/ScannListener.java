/**
 * 
 */
package br.com.webscanner.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.controller.ScannerChooserController;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.Scanner.DriverScanner;
import br.com.webscanner.view.task.CapiScannTask;
import br.com.webscanner.view.task.RangerScannTask;
import br.com.webscanner.view.task.ScannTask;
import br.com.webscanner.view.task.WindowsScanTask;

/**
 * Classe que define a ação do botão de digitalizar.
 * @author Jonathan Camara
 */
public class ScannListener implements ActionListener {
	private ProductScannerController controller;
	private ScannerChooserController scannerChooserController;
	
	public ScannListener(ProductScannerController controller, ScannerChooserController scannerChooserController) {
		this.controller = controller;
		this.scannerChooserController = scannerChooserController;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			Scanner scanner = scannerChooserController.getValidScannerConfigured();
			
			if (scanner == null){
				controller.showLoading(true);
				scannerChooserController.showScannerChooser();
			}else{
				controller.setEnableActions(false);
				Scanner scannerSelected = this.controller.getScannerSelected();
				
				if (scannerSelected.getDriver() == DriverScanner.RANGER) {
					RangerScannTask task = new RangerScannTask(controller);
					task.execute();
				} else if (scannerSelected.getDriver() == DriverScanner.CAPI) {
					CapiScannTask task = new CapiScannTask(controller);
					task.execute();
				} else if (scannerSelected.getName().contains("KODAK")){
					WindowsScanTask task = new WindowsScanTask(controller);
					task.execute();
				}else {
					ScannTask task = new ScannTask(controller);
					task.execute();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}