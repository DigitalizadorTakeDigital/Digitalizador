/**
 * 
 */
package br.com.webscanner.model.domain;

/**
 * Classe que representa uma função de validação.
 * @author Jonathan Camara
 *
 */
public class ValidationFunction {
	private String function;
	private String message;
	
	public ValidationFunction(String function, String message) {
		this.function = function;
		this.message = message;
	}

	public String getFunction() {
		return function;
	}

	public String getMessage() {
		return message;
	}
}