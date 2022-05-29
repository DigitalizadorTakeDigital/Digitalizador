package br.com.webscanner.view.listener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import br.com.webscanner.controller.MetadataController;

public class ValidationListener implements PropertyChangeListener {

	private MetadataController controller;

	public ValidationListener(MetadataController controller) {
		this.controller = controller;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("validation")) {
			List<String> dependents = (List<String>) evt.getNewValue();
			if (dependents != null) {
				for (String dependent : dependents) {
					controller.validateField(dependent);
				}
			}
		}
	}
}
