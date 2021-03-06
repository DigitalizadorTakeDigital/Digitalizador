/**
 * 
 */
package br.com.webscanner.view.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.domain.export.transp01.TRANSP01Export;
import br.com.webscanner.model.domain.validator.Validator;
import br.com.webscanner.view.model.DocumentTableModel;

/**
 * @author Jonathan Camara
 *
 */
public class TableRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 5417525525375434250L;

	public TableRenderer() {
		setOpaque(true);
	}
//	public static final int OBJECT = -1;
	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		if(isSelected){
			setBackground(Color.LIGHT_GRAY);
		}else{
			setBackground(Color.WHITE);
		}
		
		
		if(value instanceof Document){
			Document document = (Document) value;
			Validator validator = new Validator();
			document.validate(validator);
			
			if(validator.hasError()){
				setForeground(Color.RED);
			} else if (document.temErroIqf()) {
				setForeground(Color.MAGENTA);
			} else {
				setForeground(Color.BLACK);
			}
			
			if(document.getDocumentModel() != null) {
				if(document.getDocumentModel().getName().equals("romaneio")) {
					Color myWhite = new Color(173, 216, 230);
					setBackground(myWhite);
				
				}if(document.getDocumentModel().getName().equals("documento") && null == TRANSP01Export.numeroVerso) {
					setBackground(Color.orange);
				
				}
			}
			if(isSelected){
				setBackground(Color.LIGHT_GRAY);
			}
			
			switch (column) {
				case 0:
					setText(String.valueOf(document.getId()));
					
					break;
				case 1:
					DocumentModel model = document.getDocumentModel();
					
					String display = "";
					
					if(model != null){
						display = document.getDocumentModel().getTableDisplay();
						if(document.getDocumentModel().getName().equals("multa")) {
							display = document.getFieldByName("nomeMulta").getValue().toString();
						}else if(document.getDocumentModel().getName().equals("NF") || document.getDocumentModel().getName().equals("NF2")) {
							display = document.getFieldByName("numeroNota").getValue().toString();	
							TRANSP01Export.numeroVerso = display;
						}else if(document.getDocumentModel().getName().equals("documento") &&  null != TRANSP01Export.numeroVerso ){
							display = TRANSP01Export.numeroVerso + "_V";
							TRANSP01Export.numeroVerso =  null;
						}
						
						if(display.trim().isEmpty()){
							display = document.getDocumentModel().getDisplayName();
						}
					}
					
					String regex = "\\{(.*?)\\}";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(display);

					while(matcher.find()){
						String replace = matcher.group();
						String fieldName = matcher.group(1);

						String fieldValue = document.getFieldByName(fieldName).getValue().toString();
						
						display = display.replace(replace, fieldValue);
					}
					
					setToolTipText(display);
					setText(document.getName() != null ? display : "");

					break;
			}
		}
		
		return this;
	}

}
