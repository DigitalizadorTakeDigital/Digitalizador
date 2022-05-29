/**
 * 
 */
package br.com.webscanner.model.domain.image;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.operator.TransposeType;

import br.com.webscanner.exception.CarregarArquivoException;
import br.com.webscanner.exception.CopiarArquivoException;
import br.com.webscanner.exception.FecharStreamException;
import br.com.webscanner.exception.InvalidImageSHA1Exception;
import br.com.webscanner.exception.Sha1Exception;
import br.com.webscanner.model.domain.Content;
import br.com.webscanner.model.domain.image.Image;
import br.com.webscanner.model.domain.image.ImageInfo;

/**
 * Representa uma imagem importada
 * @author Jonathan Camara
 *
 */
public class ImageImported implements Content, Type{
	private Image image;
	
	public ImageImported(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}
	
	@Override
	public void rotacionar(TransposeType rotacao) throws Sha1Exception, InvalidImageSHA1Exception, CopiarArquivoException, CarregarArquivoException, FecharStreamException {
		image.getFront().rotacionar(rotacao);
		
		ImageInfo verso = image.getRear();
		if (verso != null)
			verso.rotacionar(rotacao);
	}

	@Override
	public List<File> getContents() {
		List<File> files = new ArrayList<File>(2);
		files.add(image.getFront().getFile());
		
		if(image.getRear() != null){
			files.add(image.getRear().getFile());
		}
		return files;
	}

	@Override
	public File getMainFile() {
		return getImage().getFront().getFile();
	}

	@Override
	public File getJpgFile() {
		// TODO Auto-generated method stub
		return getImage().getFront().getFile();
	}
}
