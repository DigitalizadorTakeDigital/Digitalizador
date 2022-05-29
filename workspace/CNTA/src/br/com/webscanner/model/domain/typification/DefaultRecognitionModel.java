/**
 * 
 */
package br.com.webscanner.model.domain.typification;

import br.com.webscanner.model.domain.DocumentModel;

/**
 * Define um modelo de reconhecimento para um documento default.
 * @author Diego
 *
 */
public class DefaultRecognitionModel implements Recognizable{
	private DocumentModel model;
	
	public DefaultRecognitionModel(DocumentModel model) {
		this.model = model;
	}

	public DocumentModel getModel() {
		return model;
	}
}