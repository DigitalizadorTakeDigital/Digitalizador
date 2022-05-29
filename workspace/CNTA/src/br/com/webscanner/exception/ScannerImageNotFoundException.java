package br.com.webscanner.exception;

public class ScannerImageNotFoundException extends ScannerException {
	private static final long serialVersionUID = 1L;

	public ScannerImageNotFoundException(String message) {
		super(message);
	}
}
