package br.com.webscanner.model.domain.validator;

import java.util.ArrayList;
import java.util.List;

public class Validator {

	private List<String> messages = new ArrayList<String>();
	private boolean error;
	
	public boolean that(boolean condition, String message) {
		if (condition) {
			messages.add(message);
			error = true;
			return false;
		}
		
		return true;
	}
	
	public boolean hasError() {
		return error;
	}
	public List<String> getMessages() {
		return messages;
	}
}
