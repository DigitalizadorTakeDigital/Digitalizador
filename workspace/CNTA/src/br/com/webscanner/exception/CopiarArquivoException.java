package br.com.webscanner.exception;

import java.io.File;
import java.io.IOException;

public class CopiarArquivoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CopiarArquivoException(File origem, File destino, IOException e) {
		super(String.format("Erro ao copiar o arquivo origem [%s] para o destino [%d]", origem.getAbsolutePath(), destino.getAbsolutePath()), e);
	}
}