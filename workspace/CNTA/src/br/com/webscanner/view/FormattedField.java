/**
 * 
 */
package br.com.webscanner.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.UIManager;

import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.FieldModel;
import br.com.webscanner.model.domain.ScriptingValidation;
import br.com.webscanner.util.WebscannerUtil;
import br.com.webscanner.view.adapter.EnterKeyAdapter;

/**
 * @author Jonathan Camara
 *
 */
@SuppressWarnings("serial")
public class FormattedField extends JFormattedTextField {
	private static final int FIXED_FIELD_SIZE = 15;
	private Field field;
	private Document document;
	
	public FormattedField(Field field, Document document) {
		this.field = field;
		this.document = document;
		construct();
	}
	
	private void construct(){
		FieldModel model = this.field.getFieldModel();

		Font titleFont = UIManager.getFont("TitledBorder.font");
		Font fieldFont = this.getFont();

		FontMetrics metricsTitle = getFontMetrics(titleFont);
		FontMetrics metricsField = getFontMetrics(fieldFont); 
		
		if (model.isFixedSize()) {
			this.setPreferredSize(new Dimension(metricsField.stringWidth(WebscannerUtil.padLeft("0", FIXED_FIELD_SIZE, '0')) + 10, 40));
		} else if (model.getMaxlength() >= model.getDisplayName().length()) {
			this.setPreferredSize(new Dimension(metricsField.stringWidth(WebscannerUtil.padLeft("0", model.getMaxlength(), '0')) + 20, 40));
		} else {
			this.setPreferredSize(new Dimension(metricsTitle.stringWidth(model.getDisplayName()) + 20, 40));
		}
		
		this.setDocument(new FieldDocument(model, this));
		this.setName(model.getDisplayName());
		
		if (model.getMaxlength() > 0) {
			if (field.getValue().toString().length() > model.getMaxlength()) {
				this.setText(field.getValue().toString().substring(0, model.getMaxlength()));
			} else {
				this.setText(field.getValue().toString());
				field.setValue(this.getText());
			}
		} else {
			this.setText(field.getValue().toString());
			field.setValue(this.getText());
		}
		
		this.addKeyListener(new EnterKeyAdapter(this));
		
		if(model.isReadonly() || model.isHidden()){
			this.setEnabled(!model.isReadonly());
			this.setVisible(!model.isHidden());
			field.setValid(true);
		}

		if(model.isRequired() && field.getValue().toString().isEmpty()){
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
		}else if(!model.getValidationMethod().isEmpty() && !field.getValue().toString().isEmpty()){
			field.setValid(ScriptingValidation.validate(field.getFieldModel().getValidationMethod(), document));
			Collections.sort(document.getFields());
			field.setValid(ScriptingValidation.validate(field.getFieldModel().getValidationMethod(), document));
			Collections.sort(document.getFields());
			if(!field.isValid()){
				this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
			}else{
				this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
			}
		}else{
			this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
			field.setValid(true);
			transferFocus();
		}
		
		this.setInputVerifier(new FieldInputVerifier(field));
	}
	
	class FieldInputVerifier extends InputVerifier{
		private Field field;
		
		public FieldInputVerifier(Field field) {
			this.field = field;
		}
		
		@Override
		public boolean verify(JComponent input) {
			if(input instanceof JFormattedTextField){
				JFormattedTextField textField = (JFormattedTextField) input;
				FieldModel model = field.getFieldModel();
				if(model.isRequired() && textField.getText().isEmpty()){
					field.setValue(textField.getText());
					input.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
					input.setToolTipText("Este campo é obrigatório");
					field.setValid(false);
				}else if(!model.getValidationMethod().isEmpty() && (textField.getText() != null && !textField.getText().isEmpty())){
					field.setValue(textField.getText());
					if(fieldIsValid(field)){
						input.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
						field.setValid(true);
						input.setToolTipText("");
						transferFocus();
					}else{
						input.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
						field.setValid(false);
						input.setToolTipText(field.getName() + " inválido");
					}
				}else{
					field.setValue(textField.getText());
					input.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
					field.setValid(true);
					input.setToolTipText("");
					transferFocus();
				}
			}
			
			return true;
		}
		
		private boolean fieldIsValid(Field field) {
			String validationMethod = field.getFieldModel().getValidationMethod(); 
			if(validationMethod != null && !validationMethod.isEmpty()){
				boolean valid = ScriptingValidation.validate(validationMethod, document);
				Collections.sort(document.getFields());
				return valid;
			}else{
				return true;
			}
		}
	}
}