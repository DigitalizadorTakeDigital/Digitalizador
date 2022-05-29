package br.com.webscanner.exception;

public class CarregarArquivoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CarregarArquivoException(String mensagem, Throwable t) {
		super(mensagem, t);
	}
}