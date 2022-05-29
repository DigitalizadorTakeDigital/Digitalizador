/**
 * 
 */
package br.com.webscanner.exception;

/**
 * Representa uma exceção lançada pelo produto CORR01.
 * @author Leonardo
 *
 */
public class CORR01Exception extends ExportException {

	private static final long serialVersionUID = 1L;

	public CORR01Exception() {
		super();
	}
	
	public CORR01Exception(String message){
		super(message);
	}
	
	public CORR01Exception(String message, Throwable t){
		super(message, t);
	}
}
