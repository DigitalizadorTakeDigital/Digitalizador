/**
 * 
 */
package br.com.webscanner.model.domain.typification;

import java.util.ArrayList;
import java.util.List;

import br.com.webscanner.model.domain.DocumentModel;

/**
 * Classe que possiu os padrões de identificação de um documento.
 * @author Jonathan Camara
 */
public class DocumentRecognitionModel implements DocumentRecognizable{
	private DocumentModel documentModel;
	private List<String> patterns;
	
	public DocumentRecognitionModel(DocumentModel model) {
		this.documentModel = model;
		this.patterns = new ArrayList<String>();
	}

	public DocumentModel getDocumentModel(){
		return this.documentModel;
	}
	public List<String> getPatterns() {
		return patterns;
	}
	public boolean addPattern(String pattern) {
		return this.patterns.add(pattern);
	}

	@Override
	public String toString() {
		return "DocumentRecognitionModel [documentModel=" + documentModel
				+ ", patterns=" + patterns + "]";
	}
}