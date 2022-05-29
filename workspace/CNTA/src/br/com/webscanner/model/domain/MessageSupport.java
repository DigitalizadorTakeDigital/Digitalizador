package br.com.webscanner.model.domain;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MessageSupport {

	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	public MessageSupport(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	public void updateMessage(String message) {
		support.firePropertyChange("message", "", message);
	}
	
	public void updateMessage(String message, Object ... args) {
		support.firePropertyChange("message", "", String.format(message, args));
	}
}
