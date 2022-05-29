/**
 * 
 */
package br.com.webscanner.exception;

/**
 * Exceção gerada quando encontrados problemas na pré-inicialização.
 * @author Diego
 *
 */
@SuppressWarnings("serial")
public class PreInitializationException extends Exception {
	public PreInitializationException(){
		super();
	}
	
	public PreInitializationException(String message){
		super(message);
	}
}
