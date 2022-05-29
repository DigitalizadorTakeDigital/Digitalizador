/**
 * 
 */
package br.com.webscanner.exception;

/**
 * @author Diego
 *
 */
@SuppressWarnings("serial")
public class ExportException extends Exception {
	public ExportException() {
		super();
	}
	
	public ExportException(String message) {
		super(message);
	}
	
	public ExportException(String message, Throwable t){
		super(message, t);
	}
}