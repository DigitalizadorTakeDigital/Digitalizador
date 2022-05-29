package br.com.webscanner.exception;

public class InvalidImageSHA1Exception extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidImageSHA1Exception(String mensagem) {
		super(mensagem);
	}

	public InvalidImageSHA1Exception(String mensagem, Throwable t) {
		super(mensagem, t);
	}
}