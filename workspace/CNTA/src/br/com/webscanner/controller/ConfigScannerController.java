package br.com.webscanner.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.model.domain.ScannerConfig;
import br.com.webscanner.model.domain.extraction.ExtractionType;
import br.com.webscanner.model.domain.typification.BlankPageRecognitionModel;
import br.com.webscanner.model.domain.typification.Recognition;

public class ConfigScannerController {
	private static Logger logger = LogManager.getLogger(ConfigScannerController.class.getName());
	private ProductScannerController controller;
	
	public ConfigScannerController(ProductScannerController controller) {
		super();
		this.controller = controller;
	}

	public void setConfigScannerColor(String cod) {
		controller.getScannerSelected().setScannerColor(cod);
	}
	
	public String getConfigScannerColor() {
		return controller.getScannerSelected().getScannerColor();
	}
	
	public ScannerConfig getScannerConfig () {
		return controller.getProduct().getModel().getScannerConfigurable();
	}
	
	public BlankPageRecognitionModel getConfigBlankPage() {
		for(Recognition recognition : controller.getProduct().getModel().getRecognitions()) {
			if(recognition.getType().equals(ExtractionType.BLANKPAGE)) {
				BlankPageRecognitionModel blankPageRecognitionModel = (BlankPageRecognitionModel) recognition.getRecognizable();
				return blankPageRecognitionModel;
			}
			return null;
		}
		return null;
	}
	
	public void setConfigBlankPage(double size, double cover) {
		
		for(Recognition recognition : controller.getProduct().getModel().getRecognitions()) {
			if(recognition.getType().equals(ExtractionType.BLANKPAGE)) {
				BlankPageRecognitionModel blankPageRecognitionModel = (BlankPageRecognitionModel) recognition.getRecognizable();
				blankPageRecognitionModel.setSize(size);
				blankPageRecognitionModel.setThreshold(cover);
			}
		}
		
	}

}
