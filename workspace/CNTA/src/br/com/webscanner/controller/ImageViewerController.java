package br.com.webscanner.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.media.jai.operator.TransposeType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.CarregarArquivoException;
import br.com.webscanner.exception.CopiarArquivoException;
import br.com.webscanner.exception.FecharStreamException;
import br.com.webscanner.exception.InvalidImageSHA1Exception;
import br.com.webscanner.exception.Sha1Exception;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Content;
import br.com.webscanner.model.domain.DocumentImported;
import br.com.webscanner.model.domain.Extension;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.image.Image;
import br.com.webscanner.model.domain.image.ImageImported;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.util.CommandExecution;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.view.ImageActionsBar;
import br.com.webscanner.view.ImagePanel;
import br.com.webscanner.view.MessagePanel.MessageLevel;

public class ImageViewerController {
	private static Logger logger = LogManager.getLogger(ImageViewerController.class.getName());
	private ImagePanel imagePanel;
	private ImageActionBarController actionBarController;
	private ProductScannerController productScannerController;
	private Content content;
	private Image image;
	private ImageSide imageSide;
	private Type type;
	
	public enum Type{
		TIF, JPG, DOCUMENT_IMPORTED, IMAGE_IMPORTED;
	}

	enum ImageSide{
		FRONT, REAR;
	}

	public ImageViewerController(ImagePanel imagePanel, ImageActionsBar imageActionsBar, ProductScannerController productScannerController) {
		this.imagePanel = imagePanel;
		this.actionBarController = new ImageActionBarController(imageActionsBar);
		this.productScannerController = productScannerController;
	}

	public void setContent(Content content) {
		this.content = content;
	}
	
	public void switchImage () {
		try {
			switch (imageSide) {
				case FRONT:
					this.imagePanel.setImage(ImageUtil.getFileImage(image.getRear().getFile()));
					this.imageSide = ImageSide.REAR;
					break;
				case REAR:
					this.imagePanel.setImage(ImageUtil.getFileImage(image.getFront().getFile()));
					this.imageSide = ImageSide.FRONT;
					break;
			}
			actionBarController.showActionBar(content, type);
		} catch (IOException e) {
			logger.error("Erro ao recuperar a imagem do do arquivo. {}", e.getMessage());
		}
	}
	
	public void showImage(Type type, Extension ... extension) throws IOException {
		this.type = type;
		switch (type) {
			case TIF:
				this.image = ((ImageScanned)content).getTiff();
				this.imageSide = ImageSide.FRONT;
				this.imagePanel.setImage(ImageUtil.getFileImage(image.getFront().getFile()));
				imagePanel.getParent().getParent().setVisible(true);
				break;
			case JPG:
				this.image = ((ImageScanned)content).getJpg();
				this.imageSide = ImageSide.FRONT;
				this.imagePanel.setImage(ImageUtil.getFileImage(image.getFront().getFile()));
				imagePanel.getParent().getParent().setVisible(true);
				break;
			case IMAGE_IMPORTED:
				this.image = ((ImageImported)content).getImage();
				this.imageSide = ImageSide.FRONT;
				imagePanel.getParent().getParent().setVisible(true);
				this.imagePanel.setImage(ImageUtil.getFileImage(image.getFront().getFile()));
				break;
			case DOCUMENT_IMPORTED:				
				BufferedImage documentImage = ImageUtil.getImageAsBuffered("document.png");
				ImageUtil.writeString(documentImage, extension[0].getDescription());
				this.imagePanel.setImage(documentImage);
				imagePanel.getParent().getParent().setVisible(true);//Linha adicionada para mostrar novamente o icone de doc importado em 29-06-2018
				break;
			default:
				break;
		}
		actionBarController.showActionBar(content, type);
	}
	
	public boolean isImageSmallerThanPanel(){
		return this.imagePanel.isImageSmallerThanPanel();
	}
	
	public boolean hasScaleDefined(){
		return this.imagePanel.hasScaleDefined();
	}

	public void openDocument() {		
		logger.info("Abrindo o documento importado");
		DocumentImported imported = (DocumentImported) this.content;				
		try {
			CommandExecution.execute(false, "cmd.exe /C " + imported.getFile().getAbsolutePath());			
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
	}

	public void rotateImage(TransposeType rotation) {
	 	logger.info("Rotacionando imagem pelo viewer controller");
		try {
			content.rotacionar(rotation);
		} catch (Sha1Exception e) {
			productScannerController.showMessage(new Message(Bundle.getString("sha1.validation.error"), MessageLevel.ERROR));
		} catch (InvalidImageSHA1Exception e) {
			productScannerController.showMessage(new Message(Bundle.getString("sha1.validation.error"), MessageLevel.ERROR));
		} catch (CopiarArquivoException e) {
			logger.error(e);
		} catch (CarregarArquivoException e) {
			logger.error(e);
		} catch (FecharStreamException e) {
			logger.error(e);
		}
	}
	
	public boolean zoomIn() {
		return imagePanel.zoomIn();
	}

	public boolean zoomOut() {
		return imagePanel.zoomOut();
	}

	public void setImageRealSize() {
		imagePanel.setImageRealSize();
	}

	public void setImageWindowsSize() {
		imagePanel.setImageWindowSize();
	}
}