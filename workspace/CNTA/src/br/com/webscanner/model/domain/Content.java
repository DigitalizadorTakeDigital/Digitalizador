package br.com.webscanner.model.domain;

import java.io.File;
import java.util.List;

import javax.media.jai.operator.TransposeType;

import br.com.webscanner.exception.CarregarArquivoException;
import br.com.webscanner.exception.CopiarArquivoException;
import br.com.webscanner.exception.FecharStreamException;
import br.com.webscanner.exception.InvalidImageSHA1Exception;
import br.com.webscanner.exception.Sha1Exception;

public interface Content {
	List<File> getContents();
	
	File getMainFile();
	
	File getJpgFile();
	
	void rotacionar(TransposeType rotacao) throws Sha1Exception, InvalidImageSHA1Exception, CopiarArquivoException, CarregarArquivoException, FecharStreamException;
}
