package br.com.webscanner.view;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import javax.swing.BorderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.FieldModel;
import br.com.webscanner.model.domain.ScriptingValidation;
import br.com.webscanner.view.adapter.EnterKeyAdapter;

import com.toedter.calendar.JDateChooser;

public class DateChooser extends JDateChooser{

	private static final Logger LOGGER = LogManager.getLogger(DateChooser.class.getName());
	private static final long serialVersionUID = 1L;
	private Field field;
	private Document document;

	public DateChooser(Field field, Document document) {
		super("dd/MM/yyyy", "##/##/####", ' ');
		this.field = field;
		this.document = document;
		construct();
	}

	private void construct() {
		this.setBorder(BorderFactory.createTitledBorder(field.getName()));
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");

		final FieldModel model = field.getFieldModel();
		
		if(!field.getValue().toString().isEmpty()){
			try {
				Date date = new SimpleDateFormat("ddMMyyyy").parse(field.getValue().toString());
				this.setDate(date);
			} catch (ParseException e) {
				LOGGER.error("Erro ao converter data do field {}. Mensagem: {}", field.getName(), e.getMessage());
			}
		}

		if(!model.isRequired()){
			field.setValid(true);
		}

		if(model.isReadonly() || model.isHidden()){
			this.setEnabled(!model.isReadonly());
			this.setVisible(!model.isHidden());
			field.setValid(true);
			this.transferFocus();
		}

		this.setPreferredSize(new Dimension(104, 43));
		this.setBackground(new Color(255, 255, 255, 0));
		this.setOpaque(false);
		
		this.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (!event.getPropertyName().equalsIgnoreCase("border")) {
					if(DateChooser.this.getDate() != null){
						field.setValue(simpleDateFormat.format(DateChooser.this.getDate()));
						if (fieldIsValid(field)) {
							field.setValid(true);
							DateChooser.this.transferFocus();
							DateChooser.this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
						} else {
							field.setValid(false);
							DateChooser.this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
							DateChooser.this.setToolTipText(field.getName() + " inválido");
						}
					}else{
						if(field.getFieldModel().isRequired()){
							field.setValid(false);
							DateChooser.this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
							DateChooser.this.setToolTipText("Este campo é obrigatório");
						}else{
							field.setValid(true);
							DateChooser.this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
						}
						field.setValue("");
					}
				}
			}
		});
		this.addKeyListener(new EnterKeyAdapter(this));
	}
	
	private boolean fieldIsValid(Field field) {
		String validationMethod = field.getFieldModel().getValidationMethod();
		String value = (String) field.getValue();
		
		if (value.matches("\\d{2}\\d{2}(19|20)\\d{2}")) {
			if (validationMethod != null && !validationMethod.isEmpty()) {
				boolean valid = ScriptingValidation.validate(validationMethod, document);
				Collections.sort(document.getFields());
				return valid;
			}
			return true;
		} else {
			return false;
		}
	}
}
