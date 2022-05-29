package br.com.webscanner.model.domain.typification;

import br.com.webscanner.model.domain.DocumentModel;

public class BlankPageRecognitionModel implements Recognizable {
	private DocumentModel documentModel;
	private double threshold;
	private double size;

	public BlankPageRecognitionModel(DocumentModel model, double threshold, double size) {
		this.documentModel = model;
		this.threshold = threshold;
		this.size = size;
	}

	public DocumentModel getDocumentModel() {
		return this.documentModel;
	}

	public double getThreshold() {
		return threshold;
	}

	public double getSize() {
		return size;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public void setSize(double size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "BlankPageRecognitionModel [documentModel=" + documentModel + "]";
	}
}