package br.com.webscanner.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.FieldModel;
import br.com.webscanner.model.domain.ScriptingValidation;
import br.com.webscanner.util.WebscannerUtil;
import br.com.webscanner.view.adapter.EnterKeyAdapter;
import br.com.webscanner.view.field.FieldValidatable;

public class Cmc7TextField extends JTextField implements FieldValidatable {

	private static final int FIXED_FIELD_SIZE = 15;
	private static final long serialVersionUID = 1L;  
	private static final String ACCEPT_REGEX = "[^\\d|!]";
	private Field field;
	private Document document;
	private DependentSupport dependentSupport;

	public Cmc7TextField(Field field, Document document) {
		this.field = field;
		this.document = document;
		construct();
	}  
	
	public FieldModel getFieldModel() {
		return field.getFieldModel();
	}

	private void construct() {  
		final FieldModel model = this.field.getFieldModel();

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

		if(!model.getValidationMethod().isEmpty()) {
			addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent arg0) {
					super.focusLost(arg0);
					String validationMethod = field.getFieldModel().getValidationMethod();
					if (validationMethod == null || validationMethod.trim().isEmpty()) {
						field.setValid(true);
						Cmc7TextField.this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
					} else {
						field.setValid(ScriptingValidation.validate(field.getFieldModel().getValidationMethod(), document));
						if(!field.isValid()){
							Cmc7TextField.this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
							field.setValid(false);
						} else {
							Cmc7TextField.this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
						}
					}
					dependentSupport.updateValidation(field.getFieldModel().getDependents());
				}
			});
		}

		setDocument(new Cmc7PlainDocument(this, model));  

		addCaretListener(new CaretListener() {  
			boolean update = true;
			@Override
			public void caretUpdate(CaretEvent e) {
				if (update) {
					update = false;
					if (getText().contains("!")) {
						int index = getText().indexOf("!");
						select(index, index + 1);
					}
					update = true;
				}
			}
		});  
		addKeyListener(new KeyAdapter() {  
			@Override  
			public void keyPressed(KeyEvent e) {  
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {  
//					setText("");
				}  
			}  
		});
		
		if(!field.getValue().toString().isEmpty()){
			setText(field.getValue().toString());
		}else{
			setText("");  
		}
	}

	class Cmc7PlainDocument extends PlainDocument {
		
		protected static final long serialVersionUID = 1L;  
		private Cmc7TextField textField;
		private FieldModel model;

		Cmc7PlainDocument(Cmc7TextField textField, FieldModel model) {
			this.textField = textField;
			this.model = model;
		}

		@Override  
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if(checkMaxLenght(str)) {
				StringBuilder text = new StringBuilder(Cmc7TextField.this.getText().replaceAll(ACCEPT_REGEX, "")).insert(offs, str.replaceAll(ACCEPT_REGEX, ""));  
				super.remove(0, getLength());  
				if (text.length() == 0) {
					text = new StringBuilder("");  
					Cmc7TextField.this.field.setValue("");
				}
				
				super.insertString(0, text.toString(), a); 
			}
			
			if(model.isRequired() && textField.getText().isEmpty()){
				field.setValue(textField.getText());
				textField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName(), TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
				textField.setToolTipText("Este campo é obrigatório");
				field.setValid(false);
			}else if(!model.getValidationMethod().isEmpty() && (textField.getText() != null && !textField.getText().isEmpty())){
				field.setValue(textField.getText());
				if(fieldIsValid(field)){
					textField.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
					field.setValid(true);
					textField.setToolTipText("");
					transferFocus();
				}else{
					textField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
					field.setValid(false);
					textField.setToolTipText(field.getName() + " inválido");
				}
			}else{
				field.setValue(textField.getText());
				textField.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
				field.setValid(true);
				textField.setToolTipText("");
			}
			
			if (textField.getText().contains("!")) {
				requestFocus();
			} else if (getLength() == model.getMaxlength()) {
				textField.transferFocus();
			}
		}  

		@Override  
		public void remove(int offs, int len) throws BadLocationException {  
			super.remove(offs, len);  
			if (len != getLength()) {  
				insertString(0, "", null);  
			}
			setCaretPosition(offs);
		}
		
		private boolean checkMaxLenght(String str) throws BadLocationException {
			int maxLength = model.getMaxlength();
			if(str.length() > maxLength){
				str = str.substring(0, maxLength);
			}
			
			int size = this.getLength() + str.length();  
		    
			if (size <= maxLength) {  
				return true;
		    }
			return false;
		}
		
		private boolean fieldIsValid(Field field) {
			String validationMethod = field.getFieldModel().getValidationMethod(); 
			if(validationMethod != null && !validationMethod.isEmpty()){
				boolean valid = ScriptingValidation.validate(validationMethod, document);
				return valid;
			}else{
				return true;
			}
		}
	}
	
	@Override
	public boolean hasError() {
		return !field.isValid();
	}

	public void setDependentSupport(DependentSupport dependentSupport) {
		this.dependentSupport = dependentSupport;
	}
	
	@Override
	public void validate() {
		super.validate();
		FieldModel model = field.getFieldModel();
		
		String validationMethod = field.getFieldModel().getValidationMethod(); 
		if(validationMethod != null && !validationMethod.isEmpty()){
			field.setValid(ScriptingValidation.validate(model.getValidationMethod(), document));
		}
		
		if(!field.isValid()){
			Cmc7TextField.this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
			field.setValid(false);
		}else{
			Cmc7TextField.this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
		}
	}
}