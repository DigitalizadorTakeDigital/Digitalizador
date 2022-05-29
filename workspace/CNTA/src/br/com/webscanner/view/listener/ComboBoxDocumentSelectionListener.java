/**
 * 
 */
package br.com.webscanner.view.listener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import br.com.webscanner.controller.MetadataController;
import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.services.OCRService;

/**
 * @author Jonathan Camara
 *
 */
public class ComboBoxDocumentSelectionListener implements ItemListener {
	private ProductScannerController controller;
	private MetadataController metadataController;
	
	public ComboBoxDocumentSelectionListener(ProductScannerController controller, MetadataController metadataController) {
		this.controller = controller;
		this.metadataController = metadataController;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED){
			JComboBox comboBox = (JComboBox) e.getSource();
			if(comboBox.getSelectedItem() instanceof DocumentModel){
				DocumentModel documentModel = (DocumentModel)comboBox.getSelectedItem();
			
				for (Document document: this.controller.getSelectedDocuments()) {
					if(!documentModel.equals(document.getDocumentModel())){
						document.getFields().clear();
						document.setDocumentModel(documentModel);
						OCRService.extractFields(document);
						this.metadataController.showMetadata(document);
					}
				}
				Document actualDocument = this.controller.getActualDocument();

				this.metadataController.showMetadata(actualDocument);
				this.controller.updateTable();
			}else{
				this.metadataController.removeFields();
			}
		}else{
			this.metadataController.removeFields();
		}
	}
}