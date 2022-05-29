package br.com.webscanner.model.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.operator.TransposeType;

import br.com.webscanner.exception.CarregarArquivoException;
import br.com.webscanner.exception.CopiarArquivoException;
import br.com.webscanner.exception.FecharStreamException;
import br.com.webscanner.exception.InvalidImageSHA1Exception;
import br.com.webscanner.exception.NaoImplementadoException;
import br.com.webscanner.exception.Sha1Exception;

public class DocumentImported implements Content {
	private File file;
	private Extension extension;
	
	public DocumentImported(File file, Extension extension) {
		this.file = file;
		this.extension = extension;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	public Extension getExtension() {
		return extension;
	}
	
	@Override
	public List<File> getContents() {
		List<File> files = new ArrayList<File>(1);
		files.add(this.file);
		return files;
	}

	@Override
	public File getMainFile() {
		return file;
	}
	
	@Override
	public void rotacionar(TransposeType rotacao) throws Sha1Exception,
			InvalidImageSHA1Exception, CopiarArquivoException,
			CarregarArquivoException, FecharStreamException {
		throw new NaoImplementadoException();
	}

	@Override
	public File getJpgFile() {
		// TODO Auto-generated method stub
		return file;
	}
}
