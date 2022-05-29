package br.com.webscanner.view.model;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import br.com.webscanner.model.domain.Item;

/**
 * Modelo do Combo Box dos fields;
 * @author Fernando Germano
 *
 */
@SuppressWarnings("serial")
public class ItemComboBoxModel extends DefaultComboBoxModel {
	private List<Item> items;
	
	
	public ItemComboBoxModel(List<Item> items) {
		this.items = items;
		
		for(Item item: this.items){
			addElement(item);
		}
	}
}
