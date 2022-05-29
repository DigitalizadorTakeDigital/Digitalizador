package br.com.webscanner.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class DependentSupport {

	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	public DependentSupport(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	public void updateValidation(List<String> dependents) {
		support.firePropertyChange("validation", "", dependents);
	}
}
