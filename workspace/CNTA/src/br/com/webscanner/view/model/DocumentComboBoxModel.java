/**
 * 
 */
package br.com.webscanner.view.model;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.DocumentModel;

/**
 * @author Jonathan Camara
 *
 */
public class DocumentComboBoxModel extends DefaultComboBoxModel{
	private static final long serialVersionUID = 1L;
	private List<DocumentModel> documentModels;
	
	public DocumentComboBoxModel(List<DocumentModel> documents) {
		this.documentModels = documents;
		
		for(DocumentModel document : this.documentModels){
			addElement(document);
		}
	}
	
	@Override
	public void setSelectedItem(Object item) {
		if(item instanceof Document){
			Document document= (Document) item;
			
			for(DocumentModel model : documentModels){
				if(model.equals(document.getDocumentModel())){
					super.setSelectedItem(model);
					break;
				}
			}
		}else{
			super.setSelectedItem(item);
		}
	}

	@Override
	public String toString() {
		return "DocumentComboBoxModel [documentModels=" + documentModels + "]";
	}
}