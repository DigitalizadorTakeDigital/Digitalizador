package br.com.webscanner.view;

import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import br.com.webscanner.model.domain.FieldModel;
import br.com.webscanner.model.domain.FieldType;

/**
 * Classe que implementa as funcionalidades de maxLength do campo e
 * caracteres válidos para o field de acordo com o {@link FieldModel}
 * @author Jonathan Camara
 *
 */
@SuppressWarnings("serial")
public class FieldDocument extends PlainDocument{
	private FieldModel model;
	private Component component;
	
	public FieldDocument(FieldModel model, Component component) {
		this.model = model;
		this.component = component;
	}
	
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if(checkMaxLenght(str)){
			StringBuilder builder = new StringBuilder(getText(0, getLength()));
			builder.insert(offs, str);
			if(model.getType() == FieldType.NUMERIC_LEADING_ZERO){
				Pattern pattern = Pattern.compile("^0+");
				Matcher matcher = pattern.matcher(builder.toString());
				try {
					long value = Long.parseLong(builder.toString());
					if(getLength() > 0){
						if(!matcher.find()){
							super.insertString(offs, str, a);
						}
					}else{
						super.insertString(offs, String.valueOf(value), a);
					}
				} catch (NumberFormatException e) {
					return;
				}
			}else if(model.getType() == FieldType.NUMERIC){
				if (str.matches("\\d+"))
					super.insertString(offs, str, a);
				else 
					return;
			}else{
				super.insertString(offs, str, a);
			}
		}
		
		if(getLength() == model.getMaxlength()){
			component.transferFocus();
		}
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
}