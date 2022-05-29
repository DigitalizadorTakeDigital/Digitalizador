/**
 * 
 */
package br.com.webscanner.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import br.com.webscanner.model.domain.Field;

/**
 * Representa uma flag para indicar alguma confirmação de outro campo de um documento.
 * @author Jonathan Camara
 *
 */
@SuppressWarnings("serial")
public class FlagField extends JCheckBox {
	private Field field;
	
	public FlagField(Field field) {
		this.field = field;
		construct();
	}
	
	private void construct(){
		Boolean value = Boolean.valueOf(field.getValue().toString());
		field.setValid(true);
		field.setValue(value);
		setText(field.getName());
		setSelected(value);
		setBackground(new Color(255, 255, 255, 0));
		setOpaque(false);
			
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				field.setValue(String.valueOf(isSelected()));
				transferFocus();
			}
		});
	}
}
