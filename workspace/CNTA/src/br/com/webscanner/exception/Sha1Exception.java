package br.com.webscanner.exception;

public class Sha1Exception extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public Sha1Exception(Throwable t) {
		super("Erro ao gerar o SHA1", t);
	}
}
