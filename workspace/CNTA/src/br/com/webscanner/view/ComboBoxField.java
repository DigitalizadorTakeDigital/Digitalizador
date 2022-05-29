package br.com.webscanner.view;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import br.com.webscanner.controller.MetadataController;
import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.FieldModel;
import br.com.webscanner.model.domain.Item;
import br.com.webscanner.view.adapter.EnterKeyAdapter;
import br.com.webscanner.view.model.ItemComboBoxModel;
import br.com.webscanner.view.renderer.ItemRenderer;

@SuppressWarnings("serial")
public class ComboBoxField extends JComboBox {
	private final Field field;
	private List<Item> items;
	private final FieldModel model;
	private MetadataController metadataController;
	
	public ComboBoxField(Field field, List<Item> items, MetadataController metadataController) {
		this.field = field;
		this.items = items;
		this.model = field.getFieldModel();
		this.metadataController = metadataController;
		contruct();
	}

	private void contruct() {
		boolean selected = false;
			
		ItemComboBoxModel comboModel = new ItemComboBoxModel(this.items);
		
		final Object i;
		
		if (!model.isRequired()){
			i = new Item("", "Selecione");
			comboModel.addElement(i);
		}else{
			i = "Selecione";
		}
		this.setSelectedItem(i);
		
		this.setModel(comboModel);
		this.setRenderer(new ItemRenderer());
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createTitledBorder(field.getName()));
		this.addKeyListener(new EnterKeyAdapter(this));
		this.setVisible(!model.isHidden());
		
		this.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					JComboBox comboBox = (JComboBox) e.getSource();
					if(comboBox.getSelectedItem() instanceof Item){
						boolean refresh = false;
						if (field.getName().equalsIgnoreCase("modelo")) {
							refresh = !field.getValue().toString().equals(((Item)comboBox.getSelectedItem()).getValue());
						}
						
						field.setValue(((Item) comboBox.getSelectedItem()));
						setValid();
						comboBox.transferFocus();
						
						//TODO 05/05
						if (refresh) {
							metadataController.executeRule();
						}
					}
				}
			}
		});
						
		if(field.getValue().toString().isEmpty()){
			comboModel.setSelectedItem(i);
			if (model.isRequired()){
				setInvalid();
			}else{
				setValid();
			}
		}else{
			if (field.getValue() instanceof Item){
				this.setSelectedItem((Item)field.getValue());	
			}else{
				for (Item item : this.items) {
					if (item.getValue().equalsIgnoreCase(field.getValue().toString())){
						this.setSelectedItem(item);
						field.setValue(item);
						selected = true;
						setValid();
						break;
					}
				}
				if (!selected){
					comboModel.setSelectedItem(i);
					if (model.isRequired()){
						setInvalid();
					}else{
						setValid();
					}
				}
			}
		}
		
		this.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				if (input instanceof JComboBox){
					JComboBox comboBox = (JComboBox) input;
					if (!comboBox.getSelectedItem().equals(i) || !model.isRequired()) {
						setValid();
						return true;		
					}
				}
				setInvalid();
				return false;
			}
		});
	}

	private void setValid(){
		field.setValid(true);
		this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
		this.setToolTipText(null);
	}
	
	private void setInvalid(){
		field.setValid(false);
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
		this.setToolTipText("Este campo é obrigatório");
	}
}
