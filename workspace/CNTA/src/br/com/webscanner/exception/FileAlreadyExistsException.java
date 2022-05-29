package br.com.webscanner.exception;

public class FileAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public FileAlreadyExistsException(String message) {
		super(message);
	}
	
	public FileAlreadyExistsException() {
		super();
	}
}
