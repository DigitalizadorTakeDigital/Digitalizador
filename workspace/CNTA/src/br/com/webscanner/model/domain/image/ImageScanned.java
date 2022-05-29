package br.com.webscanner.model.domain.image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.operator.TransposeType;

import br.com.webscanner.exception.CarregarArquivoException;
import br.com.webscanner.exception.CopiarArquivoException;
import br.com.webscanner.exception.FecharStreamException;
import br.com.webscanner.exception.InvalidImageSHA1Exception;
import br.com.webscanner.exception.Sha1Exception;
import br.com.webscanner.model.domain.Content;

public class ImageScanned implements Content {
	private Image tiff;
	private Image jpg;

	public ImageScanned(Image tiff, Image jpg) {
		this.tiff = tiff;
		this.jpg = jpg;
	}
	
	public Image getTiff() {
		return tiff;
	}

	public Image getJpg() {
		return jpg;
	}

	@Override
	public void rotacionar(TransposeType rotacao) throws Sha1Exception, InvalidImageSHA1Exception, CopiarArquivoException, CarregarArquivoException, FecharStreamException {
		tiff.getFront().rotacionar(rotacao);
		ImageInfo versoTiff = tiff.getRear();
		if (versoTiff != null) 
			versoTiff.rotacionar(rotacao);
		
		if (jpg != null) {
			jpg.getFront().rotacionar(rotacao);
			ImageInfo versoJpg = jpg.getRear();
			if (versoJpg != null) {
				versoJpg.rotacionar(rotacao);
			}
		}
	}
	
	@Override
	public List<File> getContents() {
		List<File> documentImages = new ArrayList<File>(4);
		
		if(getTiff().getFront().getFile() != null){
			documentImages.add(getTiff().getFront().getFile());
		}
		
		if(getTiff().getRear() != null){
			if(getTiff().getRear().getFile() != null){
				documentImages.add(getTiff().getRear().getFile());
			}
		}
		
		if(getJpg() != null){
			if(getJpg().getFront().getFile() != null){
				documentImages.add(getJpg().getFront().getFile());
			}

			if(getJpg().getRear() != null){
				if(getJpg().getRear().getFile() != null){
					documentImages.add(getJpg().getRear().getFile());
				}
			}
		}
		
		return documentImages;
	}

	@Override
	public File getMainFile() {
		return tiff.getFront().getFile();
	}
	
	@Override
	public File getJpgFile() {
		// TODO Auto-generated method stub
		return jpg.getFront().getFile();
	}
}