package br.com.webscanner.model.domain.typification;

import java.util.ArrayList;
import java.util.List;

import javax.media.jai.operator.TransposeType;

import br.com.webscanner.model.domain.DocumentModel;

public class BarCodeRecognitionModel implements DocumentRecognizable{
	private TransposeType orientation;
	private DocumentModel documentModel;
	private List<String> patterns;
	private List<ExtractableField> extractableFields;
	private long documentId;

	public BarCodeRecognitionModel(DocumentModel model, TransposeType orientation) {
		this.orientation = orientation;
		this.documentModel = model;
		this.patterns = new ArrayList<String>();
		this.extractableFields = new ArrayList<ExtractableField>();
	}
	
	public BarCodeRecognitionModel (long documentId, TransposeType orientation) {
		this.documentId = documentId;
		this.orientation = orientation;
		this.patterns = new ArrayList<String>();
		this.extractableFields = new ArrayList<ExtractableField>();
	}
	
	public long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	public TransposeType getOrientation() {
		return orientation;
	}
	public DocumentModel getDocumentModel() {
		return documentModel;
	}
	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}
	public List<String> getPatterns() {
		return patterns;
	}
	public boolean addPattern(String pattern) {
		return this.patterns.add(pattern);
	}
	public List<ExtractableField> getFields() {
		return extractableFields;
	}
	public boolean addExtractableField(ExtractableField extractableField) {
		return this.extractableFields.add(extractableField);
	}

	@Override
	public String toString() {
		return "BarCodeRecognitionModel [documentModel=" + documentModel
				+ ", patterns=" + patterns + ", extractableFields="
				+ extractableFields + "]";
	}
}