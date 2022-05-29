/**
 * 
 */
package br.com.webscanner.exception;

/**
 * @author Jonathan Camara
 *
 */
@SuppressWarnings("serial")
public class ScannerException extends Exception {
	public ScannerException() {
		super();
	}

	public ScannerException(String message){
		super(message);
	}
}