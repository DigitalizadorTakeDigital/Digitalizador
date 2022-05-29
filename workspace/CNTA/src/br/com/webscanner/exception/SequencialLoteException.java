package br.com.webscanner.exception;


public class SequencialLoteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SequencialLoteException(String mensagem, Throwable t) {
		super(mensagem, t);
	}
}
