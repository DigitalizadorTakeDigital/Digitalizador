package br.com.webscanner.view.model;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import br.com.webscanner.model.domain.Extension;

@SuppressWarnings("serial")
public class ExtensionComboBoxModel extends DefaultComboBoxModel {
	private List<Extension> extensions;
	
	public ExtensionComboBoxModel(List<Extension> extensions) {
		this.extensions = extensions;
		addElement("Selecione");
		for(Extension extension : extensions){
			addElement(extension);
		}
	}
}
