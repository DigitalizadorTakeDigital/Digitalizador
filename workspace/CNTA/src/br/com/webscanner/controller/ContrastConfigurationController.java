package br.com.webscanner.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.view.ContrastConfigurationDialog;

public class ContrastConfigurationController {
	
	private static Logger logger = LogManager.getLogger(ContrastConfigurationController.class.getName());
	private ProductScannerController productScannerController;
	
	public ContrastConfigurationController(ProductScannerController productScannerController) {
		this.productScannerController = productScannerController;
	}
	
	public void showContrastConfiguration(){
		new ContrastConfigurationDialog(this.productScannerController, this);	
	}
	
	public void saveContrastBrightness(double valueContrast, double valueBrightness){
		Scanner scanner = this.productScannerController.getScannerSelected();
		
		if (scanner.getContrast() != null){
			scanner.getContrast().setCurrentValue((int)valueContrast);
		}else {
			scanner.getContrast().setCurrentValue(0);
			
		}
		
		if (scanner.getBrightness() != null){
			scanner.getBrightness().setCurrentValue((int)valueBrightness);
		}else {
			scanner.getContrast().setCurrentValue(8);
			
		}
		
		this.productScannerController.updateScannerSelection(scanner);
		
	}
	
//	private void saveProperties(ScannerPropertiesRange properties, double value){
////		int minScanner = properties.getMinimumValue();
////		int maxScanner = properties.getMaximumValue();
////		
////		double indexMax = maxScanner - minScanner;
////		
////		double indexBarra = value/100;
////		
////		int currentValue= (int) (indexMax*indexBarra + minScanner);
//		
//		properties.setCurrentValue((int)value);
//	}
}
