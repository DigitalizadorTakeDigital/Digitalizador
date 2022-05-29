package br.com.webscanner.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.DocumentImported;
import br.com.webscanner.model.domain.Extension;
import br.com.webscanner.model.domain.image.Image;
import br.com.webscanner.model.domain.image.ImageImported;
import br.com.webscanner.model.domain.image.ImageInfo;
import br.com.webscanner.model.services.AutomaticTypingService;
import br.com.webscanner.util.FileManagement;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.StringEncryptor;

public class DocumentImportController {
	private static Logger logger = LogManager.getLogger(DocumentImportController.class.getName());
	private ProductScannerController controller;
	private String message;
	private static int count;	
	private static Map<String, String> nomeArquivos = new LinkedHashMap<String, String>();

	public DocumentImportController(ProductScannerController controller) {
		this.controller = controller;
	}
	
	public static Map<String, String> getNomeArquivos() {
		return nomeArquivos;
	}

	public boolean createImportedDocument(String file, Extension extension) {
		logger.info("Iniciando importação de documento");

		File imported = new File(file);
		if(!isSizeAllowed(imported, extension)){
			logger.info("Tamanho do arquivo maior que o permitido");
			message = Bundle.getString("import.document.maxSize.exceeded", "Arquivo");
			return false;
		}
		if(!isMinSizeAllowed(imported, extension)){
			logger.info("Tamanho do arquivo menor que o permitido");
			message = Bundle.getString("import.document.minSize.exceeded", "Arquivo");
			return false;
		}
		
		Document document = new Document(controller.getDocumentsCount(), controller.getCaptureSequence());
		document.setDateCapture(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date()));
		long id = System.currentTimeMillis();
		String ext = FileManagement.getFileExtension(imported);
		
		String nomeArquivo = null;
		StringBuilder imageName = null;
		
		nomeArquivo = imported.getName();
		imageName = new StringBuilder("F").append(id).append(StringEncryptor.removerAcentos(System.getenv("computername"))).append(count++).append(".").append(ext);
			
		nomeArquivos.put(imageName.toString(), nomeArquivo);
		
		File target = new File(System.getProperty("user.home") + System.getProperty("file.separator") + WebScannerConfig.getProperty("pathImage"), imageName.toString());
		
		try {
			FileManagement.copy(imported, target);
			document.setContent(new DocumentImported(target, extension));			
			
			if(!controller.addDocument(document)){
				logger.error("Falha ao importar o documento no lista");
			}
			controller.updateTable();
			controller.setViewerLastDocument();
			controller.setCloseLotButtonEnabled(true);
			
			return true;
		} catch (IOException e) {
			logger.info("Falha ao copiar o arquivo importado. {}", e.getMessage());
			message = Bundle.getString("import.failed.copyFile");
			return false;
		}
	}
	
	public boolean createImportedDocument(String fileFront, String fileRear, Extension extension) {
		logger.info("Iniciando importação de documento de imagem");
		File front = new File(fileFront);
		File rear = null;
		boolean frontImageMultipage = false;
		
		if(!isSizeAllowed(front, extension)){
			logger.info("Tamanho da imagem frente maior que o permitido");
			message = Bundle.getString("import.document.maxSize.exceeded", "Frente");
			return false;
		}else if(!isMinSizeAllowed(front, extension)){
			logger.info("Tamanho da imagem frente menor que o permitido");
			message = Bundle.getString("import.document.minSize.exceeded", "Frente");
			return false;
		}else{
			if(extension.getDescription().equalsIgnoreCase("tif")){
				if(!ImageUtil.isTiffStandard(front)){
					logger.info("Imagem fora do padrão tiff");
					message = Bundle.getString("import.image.unsupported", "Frente");
					return false;
				}
				frontImageMultipage = ImageUtil.isTiffMultipage(front);
			}
		}
		
		if(fileRear != null){
			if (frontImageMultipage) {
				logger.info("Não é permitido importar imagem multipaginada para documentos com frente e verso");
				message = Bundle.getString("import.image.multiPage.notAllowed");
				return false;
			}
			
			rear = new File(fileRear);
			if(!isSizeAllowed(rear, extension)){
				logger.info("Tamanho da imagem verso maior que o permitido");
				message = Bundle.getString("import.document.maxSize.exceeded", "Verso");
				return false;
			} else if(!isMinSizeAllowed(rear, extension)){
					logger.info("Tamanho da imagem verso menor que o permitido");
					message = Bundle.getString("import.document.minSize.exceeded", "Verso");
					return false;
			} else{
				if(extension.getDescription().equalsIgnoreCase("tif")){
					if(!ImageUtil.isTiffStandard(rear)){
						logger.info("Imagem fora do padrão tiff");
						message = Bundle.getString("import.image.unsupported", "Verso");
						return false;
					}
					if (ImageUtil.isTiffMultipage(rear)) {
						logger.info("Não é permitido importar imagem multipaginada para documentos com frente e verso");
						message = Bundle.getString("import.image.multiPage.notAllowed");
						return false;
					}
				}
			}
		}
		
		Document documentImported = new Document(controller.getDocumentsCount(), controller.getCaptureSequence());
		documentImported.setDateCapture(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date()));
		long id = System.currentTimeMillis();
		String ext = FileManagement.getFileExtension(front);
		
		//Pega o nome original da imagem importada: Adicionado por Bruno Oliveira em 27-06-2018
		String nomeArquivo = null;
		StringBuilder imageName = null;
		//Nome Original
		nomeArquivo = front.getName().trim();
		
		imageName = new StringBuilder("F").append(id).append(StringEncryptor.removerAcentos(System.getenv("computername"))).append(count++).append(".").append(ext);
		File targetFront = new File(System.getProperty("user.home") + System.getProperty("file.separator") + WebScannerConfig.getProperty("pathImage"), imageName.toString());
		//Amarra com chave e valor no Map: nome interno, original.
		nomeArquivos.put(imageName.toString(), nomeArquivo);
		
		imageName = new StringBuilder("V").append(id).append(StringEncryptor.removerAcentos(System.getenv("computername"))).append(count++).append(".").append(ext);
		File targetRear = new File(System.getProperty("user.home") + System.getProperty("file.separator") + WebScannerConfig.getProperty("pathImage"), imageName.toString());

		try {
			FileManagement.copy(front, targetFront);
			
			if(rear != null){
				FileManagement.copy(rear, targetRear);
				documentImported.setContent(new ImageImported(new Image(new ImageInfo(id, targetFront), new ImageInfo(id, targetRear))));
			} else{
				if (frontImageMultipage) {
					documentImported.setContent(new DocumentImported(targetFront, extension));
				} else {
					documentImported.setContent(new ImageImported(new Image(new ImageInfo(id, targetFront), null)));
				}
			}

			if(controller.addDocument(documentImported)){
				AutomaticTypingService.typing(controller.getRecognitions(), controller.getDocumentModels(), "", documentImported);
			}
			controller.updateTable();
			controller.setViewerLastDocument();
			controller.setCloseLotButtonEnabled(true);
			
			return true;
		} catch (IOException e) {
			logger.info("Falha ao copiar o arquivo importado. {}", e.getMessage());
			message = Bundle.getString("import.failed.copyFile");
			return false;
		}
	}
		
	private boolean isSizeAllowed(File file, Extension extension){
		if(file != null){
			double length = file.length() / 1024.0;
			
			if(length > extension.getMaxSize()){
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isMinSizeAllowed(File file, Extension extension){
		if(file != null){
			double length = file.length() / 1024.0;
			
			if(length < extension.getMinSize()){
				return false;
			}
		}
		
		return true;
	}
	
	public String getMessage() {
		return message;
	}
}
