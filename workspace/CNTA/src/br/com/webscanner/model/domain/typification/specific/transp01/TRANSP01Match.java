package br.com.webscanner.model.domain.typification.specific.transp01;

import java.util.ArrayList;
import java.util.List;

import javax.media.jai.operator.TransposeType;

import br.com.webscanner.model.domain.DocumentModel;

public class TRANSP01Match {
	
	private String pattern;
	private int documentModelId;
	private List<DocumentModel> model = new ArrayList<DocumentModel>();
	private TransposeType orientation;

	public TRANSP01Match(String pattern, int documentModelId, TransposeType orientation) {
		this.pattern = pattern;
		this.documentModelId = documentModelId;
		this.orientation = orientation;
	}


public String getPattern() {
	return pattern;
}
public void setPattern(String pattern) {
	this.pattern = pattern;
}
public int getDocumentModelId() {
	return documentModelId;
}
public void setDocumentModelId(int documentModelId) {
	this.documentModelId = documentModelId;
}
public List<DocumentModel> getModel() {
	return model;
}
public void setModel(List<DocumentModel> model) {
	this.model = model;
}
public boolean addModel(DocumentModel model) {
	return this.model.add(model);
}
public TransposeType getOrientation() {
	return orientation;
}
public void setOrientation(TransposeType orientation) {
	this.orientation = orientation;
}
}
