/**
 * 
 */
package br.com.webscanner.controller;

import java.awt.Component;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.FieldModel;
import br.com.webscanner.model.domain.FieldType;
import br.com.webscanner.model.domain.rule.Rule;
import br.com.webscanner.view.ComboBoxField;
import br.com.webscanner.view.DateChooser;
import br.com.webscanner.view.FlagField;
import br.com.webscanner.view.FormattedField;
import br.com.webscanner.view.MonetaryTextField;

/**
 * Controladora responsável pela geração dos campos que serão exibidos no painel para preenchimento.
 * @author Jonathan Camara
 *
 */
public class MetadataController {
	private JPanel metadataPanel;
	private static Logger logger = LogManager.getLogger(MetadataController.class.getName());
	private Document document;
	private LinkedList<Component> componentes;

	public MetadataController(JPanel metadataPanel) {
		this.metadataPanel = metadataPanel;
	}

	public void showMetadata(final Document document) {
		this.document = document;
 		this.metadataPanel.removeAll();

		List<Field> fields = document.getFields();
		
		Collections.sort(fields);
		componentes = new LinkedList<Component>();
		
		br.com.webscanner.model.domain.rule.Rule rule;
		if ((rule = document.getDocumentModel().getRule()) !=  null) {
			rule.execute(document);
		}
		
		for(final Field field : fields){
			FieldModel model = field.getFieldModel();
			
			FieldType type = model.getType();
			
			switch (type) {
			case DATE:
				DateChooser dateChooser = new DateChooser(field, document);
				dateChooser.setName(model.getName());
				componentes.add(dateChooser);
				this.metadataPanel.add(dateChooser);				
				break;
			case COMBO:
				if(model.getItems().size() > 0){
					ComboBoxField comboBoxField = new ComboBoxField(field, model.getItems(), this);
					comboBoxField.setName(model.getName());
					componentes.add(comboBoxField);
					this.metadataPanel.add(comboBoxField);
				}else{
					logger.error("Lista de itens vazia");
				}
				break;
			case MONETARY:
				MonetaryTextField monetary = new MonetaryTextField(2, field);
				monetary.setLimit(21);
				monetary.setColumns(17);
				componentes.add(monetary);
				this.metadataPanel.add(monetary);
				break;
			case FLAG:
				FlagField flag = new FlagField(field);
				flag.setName(model.getName());
				componentes.add(flag);
				this.metadataPanel.add(flag);
				break;
			default:
				FormattedField formattedField = new FormattedField(field, document);
				formattedField.setName(model.getName());
				componentes.add(formattedField);
				this.metadataPanel.add(formattedField);
			}
		}

		int i = 0;
		while(this.metadataPanel.getComponents().length > i){
			Component component = this.metadataPanel.getComponent(i++);
			if(component.isVisible() && component.isEnabled()){
				component.requestFocus();
				break;
			}
		}

		this.metadataPanel.repaint();
		this.metadataPanel.revalidate();
	}

	public void removeFields() {
		this.metadataPanel.removeAll();
		this.metadataPanel.repaint();
		this.metadataPanel.revalidate();
	}
	
	//TODO 05/05
	public void executeRule () {
		Rule rule;
		if ((rule = document.getDocumentModel().getRule()) !=  null) {
			rule.execute(document);
		}
		showMetadata(document);
	}
	
	public void validateField(String dependent) {
		for (Component component : componentes) {
			if (component.getName().equals(dependent)) {
				component.validate();
			}
		}
	}
}