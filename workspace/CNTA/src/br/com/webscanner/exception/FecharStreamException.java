package br.com.webscanner.exception;

public class FecharStreamException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FecharStreamException(String message, Throwable t) {
		super(message, t);
	}
}