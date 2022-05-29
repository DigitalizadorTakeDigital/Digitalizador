/**
 * 
 */
package br.com.webscanner.view;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.FieldModel;
import br.com.webscanner.view.adapter.EnterKeyAdapter;

/**
 * @author Jonathan Camara
 *
 */
@SuppressWarnings("serial")
public class MonetaryFormattedField extends JFormattedTextField{
	private Field field;

	public MonetaryFormattedField(Field field) {
		super(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")));
		this.field = field;
		construct();
	}

	private void construct() {
		final FieldModel model = this.field.getFieldModel();

		this.setText(field.getValue().toString());
		this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
		this.addKeyListener(new EnterKeyAdapter(this));
		
		if(model.isReadonly() || model.isHidden()){
			this.setEnabled(!model.isReadonly());
			this.setVisible(!model.isHidden());
			field.setValid(true);
		}
		
		if(model.isRequired() && field.getValue().toString().isEmpty()){
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
		}else if(!model.getValidationMethod().isEmpty() && !field.getValue().toString().isEmpty()){
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
		
		this.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {}

			@Override
			public void focusGained(FocusEvent event) {
				DecimalFormat format = new DecimalFormat("#.#", new DecimalFormatSymbols(new Locale("pt", "BR")));
				format.setMaximumFractionDigits(2);
				format.setMinimumFractionDigits(2);
				format.setMaximumIntegerDigits(19);
				format.setMinimumIntegerDigits(1);
				
				try {
					if(getValue() != null){
						Double value = (Double) getValue();
						setText(format.format(value.doubleValue()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				setCaretPosition(0);
			}
		});
		
		setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				try {
					String temp;
					
					if(getText().length() > 22){
						temp = getText().substring(0, 22).replaceAll(",", ".");
					}else{
						temp = getText().replaceAll(",", ".");
					}
					Double value = Double.parseDouble(temp);
					setText(value.toString());
					setValue(value);
					field.setValue(value.toString());
					field.setValid(true);
					setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
					return true;
				}catch(NumberFormatException ex) {
					setText("");
					field.setValue(null);
					field.setValid(false);
					setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
					return false;
				}
			}
		});

		setColumns(50);		
	}
	
	@Override
	public void setValue(Object value) {
		super.setValue(value);
	}
}
