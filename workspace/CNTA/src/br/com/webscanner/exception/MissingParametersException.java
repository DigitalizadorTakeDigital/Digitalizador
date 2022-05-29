package br.com.webscanner.exception;

/**
 * Exception lançada quando algum parametro da aplicação não é enviado
 * @author Jonathan Camara
 *
 */
public class MissingParametersException extends PreInitializationException {

	private static final long serialVersionUID = 1L;

	public MissingParametersException(String message) {
		super(message);
	}
}
