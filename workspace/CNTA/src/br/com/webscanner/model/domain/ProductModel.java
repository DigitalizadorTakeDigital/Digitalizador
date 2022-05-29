
package br.com.webscanner.model.domain;

import java.util.List;

import br.com.webscanner.controller.ImageViewerController.Type;
import br.com.webscanner.model.domain.Scanner.DriverScanner;
import br.com.webscanner.model.domain.export.Exportable;
import br.com.webscanner.model.domain.preinitialization.PreInitializable;
import br.com.webscanner.model.domain.typification.Recognition;
import br.com.webscanner.model.domain.validatable.Validatable;

public class ProductModel {
	private boolean splitDuplex;
	private boolean exportJpg;
	private boolean exitAfterProcessed;
	private double scale;
	private List<DocumentModel> documentModels;
	private List<DocumentGeneric> documentsGeneric;
	private List<Exportable> exports;
	private List<Recognition> recognitions;
	private Validatable validatable;
	private PreInitializable preinitializable;
	private List<Extension> extensions;

	private List<Scanner.DriverScanner> drivers;
	private boolean importable;
	private ScannerConfig scannerConfigurable;
	private boolean brightness;
	private Type type;

	public ProductModel(boolean splitDuplex, boolean exportJpg, boolean exitAfterProcessed, double scale,
			List<DocumentModel> documentModels, List<DocumentGeneric> documentGenericModels, List<Exportable> exports,
			List<Recognition> recognitions, Validatable validatable, PreInitializable preinitializable,
			List<DriverScanner> drivers, boolean isImportable, ScannerConfig scannerConfigurable,
			boolean isBrightness) {
		super();
		this.splitDuplex = splitDuplex;
		this.exportJpg = exportJpg;
		this.exitAfterProcessed = exitAfterProcessed;
		this.scale = scale;
		this.documentModels = documentModels;
		this.documentsGeneric = documentGenericModels;
		this.exports = exports;
		this.recognitions = recognitions;
		this.validatable = validatable;
		this.preinitializable = preinitializable;
		this.drivers = drivers;
		this.importable = isImportable;
		this.scannerConfigurable = scannerConfigurable;
		this.brightness = isBrightness;
	}

	public boolean isSplitDuplex() {
		return splitDuplex;
	}

	public void setSpliDuplex(boolean splitDuplex) {
		this.splitDuplex = splitDuplex;
	}

	public boolean isExitAfterProcessed() {
		return exitAfterProcessed;
	}

	public void setExitAfterProcessed(boolean exitAfterProcessed) {
		this.exitAfterProcessed = exitAfterProcessed;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public List<DocumentModel> getDocumentModels() {
		return documentModels;
	}

	public void setDocumentModels(List<DocumentModel> documentModels) {
		this.documentModels = documentModels;
	}

	public List<Exportable> getExports() {
		return exports;
	}

	public void setExports(List<Exportable> exports) {
		this.exports = exports;
	}

	public List<Recognition> getRecognitions() {
		return recognitions;
	}

	public void setRecognitions(List<Recognition> recognitions) {
		this.recognitions = recognitions;
	}

	public Validatable getValidatable() {
		return validatable;
	}

	public void setValidatable(Validatable validatable) {
		this.validatable = validatable;
	}

	public boolean isScannable() {
		return !drivers.isEmpty();
	}

	public boolean scannerPermitido(DriverScanner driver) {
		return drivers.contains(driver);
	}

	public List<Scanner.DriverScanner> getDrivers() {
		return drivers;
	}

	public boolean isImportable() {
		return importable;
	}

	public void setImportable(boolean importable) {
		this.importable = importable;
	}

	public ScannerConfig getScannerConfigurable() {
		return scannerConfigurable;
	}

	public void setScannerConfigurable(ScannerConfig scannerConfigurable) {
		this.scannerConfigurable = scannerConfigurable;
	}

	public boolean isBrightness() {
		return brightness;
	}

	public void setBrightness(boolean brightness) {
		this.brightness = brightness;
	}

	public List<DocumentGeneric> getDocumentsGeneric() {
		return documentsGeneric;
	}

	public PreInitializable getPreinitializable() {
		return preinitializable;
	}

	public void setPreinitializable(PreInitializable preinitializable) {
		this.preinitializable = preinitializable;
	}

	public void setExtensions(List<Extension> extensions) {
		this.extensions = extensions;
	}

	public List<Extension> getExtensions() {
		return extensions;
	}

	public boolean isExportJpg() {
		return this.exportJpg;
	}

	public void setExportJpg(boolean exportJpg) {
		this.exportJpg = exportJpg;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public boolean isScannerConfigurable() {
		if(scannerConfigurable.isBlackPageConfig() || scannerConfigurable.isDoubleSensor() || scannerConfigurable.isGrayScale()) {
			return true;
		}
		return false;
	}

	public DocumentGeneric getDocumentGenericByIcor(int icor) {
		for (DocumentGeneric documentGeneric : documentsGeneric) {
			if (documentGeneric.getIcor() == icor) {
				return documentGeneric;
			}
		}
		return null;
	}

	public DocumentModel getDocumentModelByIcor(int icor) {
		for (DocumentModel model : documentModels) {
			if (model.getIcor() == icor) {
				return model;
			}
		}
		return null;
	}

	public DocumentModel getDocumentModelById(int id) {
		for (DocumentModel model : documentModels) {
			if (model.getId() == id) {
				return model;
			}
		}
		return null;
	}
}
