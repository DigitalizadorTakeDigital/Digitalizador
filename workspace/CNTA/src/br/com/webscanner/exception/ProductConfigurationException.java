package br.com.webscanner.exception;

public class ProductConfigurationException extends Exception {

	private static final long serialVersionUID = 2625955743670515266L;

	public ProductConfigurationException() {
		super();
	}
	
	public ProductConfigurationException(String message){
		super(message);
	}
}
