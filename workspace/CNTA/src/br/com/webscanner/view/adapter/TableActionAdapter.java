/**
 * 
 */
package br.com.webscanner.view.adapter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.util.FileManagement;
import br.com.webscanner.view.model.DocumentTableModel;

/**
 * @author Jonathan Camara
 *
 */
public class TableActionAdapter extends MouseAdapter {
	private ProductScannerController controller;
	private static Logger logger = LogManager.getLogger(TableActionAdapter.class.getName());
	
	public TableActionAdapter(ProductScannerController controller) {
		this.controller = controller;
	}

	private JPopupMenu createPopUp(final JTable table, final Document document, final int row){
		UIManager.put("MenuItem.selectionBackground", new Color(178, 0, 0));
		UIManager.put("MenuItem.selectionForeground", Color.white);
		
		JPopupMenu menu = new JPopupMenu();
		menu.setBackground(Color.white);
		
		JMenuItem moveTopItem = new JMenuItem("Mover para o topo");
		moveTopItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				logger.info("Movendo documento para o topo {}", document.getCaptureSequence());
				DocumentTableModel model = (DocumentTableModel) table.getModel();
				model.reorder(row, 0);
				table.setRowSelectionInterval(0, 0);
				controller.updateLeftPanel();
			}
		});
		moveTopItem.setBackground(Color.white);
		
		JMenuItem moveBottomItem = new JMenuItem("Mover para o final");
		moveBottomItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				logger.info("Movendo documento para o fim {}", document.getCaptureSequence());
				DocumentTableModel model = (DocumentTableModel) table.getModel();
				int rowTo = model.getRowCount() - 1;
				
				model.reorder(row, rowTo);
				table.setRowSelectionInterval(rowTo, rowTo);
				controller.updateLeftPanel();
			}
		});
		moveBottomItem.setBackground(Color.white);
		
		JMenuItem removeItem = new JMenuItem("Remover");
		removeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DocumentTableModel model = (DocumentTableModel) table.getModel();
				for (Document document : controller.getSelectedDocuments()) {
					logger.info("Deletando documento : {}", document.getCaptureSequence());
					for (File file : document.getContent().getContents()) {
						FileManagement.delete(file);
					}
					model.removeRow(document);
				}
				controller.clearDocumentTableSelection();
				controller.setActualDocument(null);
				controller.updateDocumentsCombobox();
				controller.updateTable();
				controller.updateLeftPanel();
				controller.clearImagePanel();
				controller.updateMetadataPanel();
				if(controller.documentListIsEmpty()){
					controller.setCloseLotButtonEnabled(false);
				}
			}
		});
		removeItem.setBackground(Color.white);
		
		JMenuItem moveToItem = new JMenuItem("Mover para");
		moveToItem.addActionListener(
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						controller.showMoveToDialog(table, document, row);
											
//						String to = JOptionPane.showInputDialog(null,"Qual o número?","Mover para", JOptionPane.PLAIN_MESSAGE);
//						if (to != null) {
//							if(!to.isEmpty()) {
//								DocumentTableModel model = (DocumentTableModel) table.getModel();
//								model.reorder(row, Integer.parseInt(to)-1);
//							}
//						}
						controller.clearDocumentTableSelection();
						controller.setActualDocument(null);
						controller.updateDocumentsCombobox();
						controller.updateTable();
						controller.updateLeftPanel();
						controller.clearImagePanel();
						controller.updateMetadataPanel();
						if(controller.documentListIsEmpty()){
							controller.setCloseLotButtonEnabled(false);
						}
					}
				}
				);
//		removeItem.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				DocumentTableModel model = (DocumentTableModel) table.getModel();
//				for (Document document : controller.getSelectedDocuments()) {
//					logger.info("Deletando documento : {}", document.getCaptureSequence());
//					for (File file : document.getContent().getContents()) {
//						FileManagement.delete(file);
//					}
//					model.removeRow(document);
//				}
//				controller.clearDocumentTableSelection();
//				controller.setActualDocument(null);
//				controller.updateDocumentsCombobox();
//				controller.updateTable();
//				controller.updateLeftPanel();
//				controller.clearImagePanel();
//				controller.updateMetadataPanel();
//				if(controller.documentListIsEmpty()){
//					controller.setCloseLotButtonEnabled(false);
//				}
//			}
//		});
		moveToItem.setBackground(Color.white);
		
		if (table.getSelectedRows().length == 1){
			if(row == 0){
				menu.add(moveBottomItem);
			}else if(row == table.getRowCount() - 1){
				menu.add(moveTopItem);
			}else{
				menu.add(moveTopItem);
				menu.add(moveBottomItem);
			}
		}

		menu.add(removeItem);
		menu.add(moveToItem);
		return menu;
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		super.mouseReleased(event);
		if(event.isPopupTrigger()){
			JTable source = (JTable)event.getSource();
			
			int row = source.rowAtPoint(event.getPoint());
            int column = source.columnAtPoint(event.getPoint());
            
            if (! source.isRowSelected(row)){
            	source.changeSelection(row, column, false, false);
            }

            Document document = (Document)source.getValueAt(row, column);
            
            JPopupMenu popup = createPopUp(source, document, row);
            popup.show(event.getComponent(), event.getX(), event.getY());
		}
	}
}