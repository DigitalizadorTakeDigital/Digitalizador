/**
 * 
 */
package br.com.webscanner.view.listener;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import br.com.gcc.api.scanner.domain.model.TestId;
import br.com.webscanner.controller.ImageViewerController;
import br.com.webscanner.controller.ImageViewerController.Type;
import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Content;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.DocumentImported;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.image.ImageImported;
import br.com.webscanner.model.domain.image.ImageInfo;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.view.MessagePanel.MessageLevel;

/**
 * @author Jonathan Camara
 *
 */
public class TableDocumentSelectionListener implements ListSelectionListener {
	private JTable table;
	private ProductScannerController productController;
	private ImageViewerController imageViewerController;
	private Type defaultImageType;
	
	public TableDocumentSelectionListener(JTable table, ProductScannerController productController, ImageViewerController imageViewerController) {
		this.table = table;
		this.productController = productController;
		this.imageViewerController = imageViewerController;
		this.defaultImageType = productController.getProduct().getModel().getType();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
			productController.enableDocumentSelection(true);
			DefaultListSelectionModel model = (DefaultListSelectionModel) e.getSource();
			productController.clearSelectedDocuments();
			for (int rowIndex : table.getSelectedRows()) {
				productController.addSelectedDocument((Document) table.getValueAt(rowIndex, -1));
			}
			if (table.getSelectedRows().length == 1){
				Document document = (Document) table.getValueAt(model.getLeadSelectionIndex(), -1);
				productController.setActualDocument(document);

				Content content = document.getContent();
				imageViewerController.setContent(content);
				
				try {
					if(content instanceof ImageScanned){
						imageViewerController.showImage(defaultImageType);
					} else if(content instanceof DocumentImported){
						imageViewerController.showImage(Type.DOCUMENT_IMPORTED, ((DocumentImported) content).getExtension());						
					} else if(content instanceof ImageImported){
						imageViewerController.showImage(Type.IMAGE_IMPORTED);
					}
					
					if(!productController.isLoading()) {
						productController.setEnableActions(true);
						
						Set<String> messages = new LinkedHashSet<String>();
						if (document.temErroIqf()) {
							
							ImageInfo front = ((ImageScanned)content).getTiff().getFront();
							if (!front.getTestsId().isEmpty()) {
								messages.add("<b>Imagem Frente:</b>");
								for (TestId test : front.getTestsId()) {
									messages.add("<br/>&nbsp;&nbsp;" + Bundle.getString(test.toString()));
								}
							}
							
							ImageInfo rear = ((ImageScanned)content).getTiff().getRear();
							if (!rear.getTestsId().isEmpty()) {
								messages.add("<br/><b>Imagem Verso:</b>");
								for (TestId test : rear.getTestsId()) {
									messages.add("<br/>&nbsp;&nbsp;" + Bundle.getString(test.toString()) + " ");
								}
							}
						}
						if(!messages.isEmpty()){
							productController.showIqfMessage(messages);
						} 
					}
					
				} catch (IOException e1) {
					productController.showMessage(new Message(Bundle.getString("file.image.notFound"), MessageLevel.ERROR));
				}
				productController.updateDocumentSelection();
			}			
	}
}