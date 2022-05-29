package br.com.webscanner.view.task;

import static br.com.webscanner.infra.WebScannerConfig.getProperty;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.gcc.api.capi.exception.CapiException;
import br.com.gcc.api.capi.library.CapiReturnValue;
import br.com.gcc.api.capi.library.CapiScanner;
import br.com.gcc.api.capi.library.CapiTransportInfo;
import br.com.gcc.api.ranger.library.FeedSource;
import br.com.gcc.api.scanner.ExceptionType;
import br.com.gcc.api.scanner.ImageListener;
import br.com.gcc.api.scanner.ReturnValue;
import br.com.gcc.api.scanner.domain.model.Document;
import br.com.gcc.api.scanner.domain.model.FileImage;
import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.Content;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.image.ImageInfo;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.model.services.AutomaticTypingService;
import br.com.webscanner.view.MessagePanel.MessageLevel;

public class CapiScannTask extends SwingWorker<Void, Void> {

	private static final Logger LOGGER = LogManager.getLogger(CapiScannTask.class.getName());
	
	private ProductScannerController controller;
	private Set<Message> messages;
	private static int quantidadePocket = 1;
	private List<String> cmc7Rejeitados;
	private static CapiScanner scanner = null;
	private boolean splitDuplex;


	public CapiScannTask(ProductScannerController controller) throws IOException {
		this.controller = controller;
		this.messages = new LinkedHashSet<Message>();
		this.cmc7Rejeitados = new ArrayList<String>();
		this.splitDuplex = controller.getProduct().getModel().isSplitDuplex();
	}
	
	static {
		try {
			scanner = CapiScanner.getInstance();
			quantidadePocket = Integer.parseInt(scanner.getTransportInfo("StackerPockets"));
			ApplicationData.putParam("serialNumber", scanner.getTransportInfo(CapiTransportInfo.SERIAL_NUMBER));
		} catch (NumberFormatException e) {
			LOGGER.error("Erro ao recuperar a quantidade de pockets do scanner");
		} catch (CapiException e) {
			LOGGER.error("Erro ao recuperar as informacoes do scanner");
		}
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		LOGGER.info("Configurando digitalizacao");
		controller.showLoading(true);
		messages.clear();
		try {
			scanner.enableLog(false);
			scanner.enableIqa(false);
			
			scanner.saveJpg(controller.getProduct().getModel().isExportJpg());
			scanner.setImageFolder(System.getProperty("user.home") + System.getProperty("file.separator") + getProperty("pathImage"));
			
			scanner.setCallback(new ImageReceiver(FileImage.class));
			
			try {
				LOGGER.info("Iniciando digitalizacao das imagens");
				
				ReturnValue acquireReturnValue = scanner.acquire(FeedSource.MAIN_HOPPER, 0);
				if (acquireReturnValue != CapiReturnValue.SUCCESS) {
					LOGGER.error("Erro ao realizar a digitalizacao das imagens {}", acquireReturnValue);
					messages.add(new Message(Bundle.getString("scanner.scann.failed"), MessageLevel.ERROR));
					if (acquireReturnValue == CapiReturnValue.EXCEPTION) {
						ExceptionType exceptionType = scanner.getException();
						LOGGER.error("Exception {}", exceptionType);
					}
				}
			} catch (CapiException e) {
				LOGGER.error(e);
				messages.add(new Message(Bundle.getString("scanner.scann.failed"), MessageLevel.ERROR));
			}
			LOGGER.info("Digitalizacao de imagens finalizada");
		} catch (Error e) {
			LOGGER.fatal("Error {}", e);
		} catch (Exception e) {
			LOGGER.error("Exception {}", e);
		}

		return null;
	}
	
	@Override
	protected void done() {
		super.done();
		LOGGER.info("Finalizando digitalização");
		
		controller.setEnableActions(true);
		controller.showLoading(false);
		
		controller.clearDocumentTableSelection();
		controller.setViewerLastDocument();
		
		for(Message message : messages){
			controller.showMessage(message);
		}
	}
	
	class ImageReceiver extends ImageListener<FileImage> {
		
		private static final String CMC7_REGEX = "(c(.{1,8})c){0,1}\\s*((.{1,10})e){0,1}\\s*((.{1,12})a){0,1}";
		
		public ImageReceiver(Class<FileImage> tipo) {
			super(tipo);
		}

		@Override
		public void receive(Document<FileImage> documento) {
			LOGGER.info("Recebendo Imagem digitalizada");
			
			if (cmc7Rejeitados.remove(documento.getCmc7())) {
				return;
			}
			
			if (splitDuplex) {
				br.com.webscanner.model.domain.Document documentoFrente = novoDocumento(documento, documento.getFrontTif(), documento.getFrontJpg());
				controller.addDocument(documentoFrente);
				
				br.com.webscanner.model.domain.Document documentoVerso = novoDocumento(documento, documento.getRearTif(), documento.getRearJpg());
				controller.addDocument(documentoVerso);
			} else {
				br.com.webscanner.model.domain.Document document = novoDocumento(documento);
				controller.addDocument(document);
			}
			controller.setViewerLastDocument();
		}

		private br.com.webscanner.model.domain.Document novoDocumento(Document<FileImage> documento, FileImage frontTif, FileImage frontJpg) {
			br.com.webscanner.model.domain.Document document = new br.com.webscanner.model.domain.Document(controller.getDocumentsCount(), controller.getCaptureSequence());
			document.setDateCapture(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date()));
			
			document.setContent(toImageScanned(frontTif, frontJpg));
			document.setErroIqf(documento.isIqaFailed());
			
			String cmc7 = documento.getCmc7();
			if (cmc7 != null) {
				Pattern pattern = Pattern.compile(CMC7_REGEX);
				Matcher matcher = pattern.matcher(cmc7);
				if (matcher.find()) {
					String cmc71 = matcher.group(2) == null ? "" : matcher.group(2);
					String cmc72 = matcher.group(4) == null ? "" : matcher.group(4);
					String cmc73 = matcher.group(6) == null ? "" : matcher.group(6);
					cmc7 = String.format("%s %s %s", cmc71, cmc72, cmc73);
					cmc7 = cmc7.replaceAll("[^\\d|^\\s|!]", "!");
				}
			} else {
				cmc7 = "";
			}
			
			AutomaticTypingService.typing(controller.getRecognitions(), controller.getDocumentModels(), cmc7, document);
			return document;
		}

		private br.com.webscanner.model.domain.Document novoDocumento(Document<FileImage> documento) {
			br.com.webscanner.model.domain.Document document = new br.com.webscanner.model.domain.Document(controller.getDocumentsCount(), controller.getCaptureSequence());
			document.setDateCapture(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date()));
			
			document.setContent(toImageScanned(documento));
			document.setErroIqf(documento.isIqaFailed());
			
			String cmc7 = documento.getCmc7();
			if (cmc7 != null) {
				Pattern pattern = Pattern.compile(CMC7_REGEX);
				Matcher matcher = pattern.matcher(cmc7);
				if (matcher.find()) {
					String cmc71 = matcher.group(2) == null ? "" : matcher.group(2);
					String cmc72 = matcher.group(4) == null ? "" : matcher.group(4);
					String cmc73 = matcher.group(6) == null ? "" : matcher.group(6);
					cmc7 = String.format("%s %s %s", cmc71, cmc72, cmc73);
					cmc7 = cmc7.replaceAll("[^\\d|^\\s|!]", "!");
				}
			} else {
				cmc7 = "";
			}
			
			AutomaticTypingService.typing(controller.getRecognitions(), controller.getDocumentModels(), cmc7, document);
			return document;
		}
		
		private Content toImageScanned(FileImage frontTif, FileImage frontJpg) {
			br.com.webscanner.model.domain.image.Image tif = toImage(frontTif, null);
			
			br.com.webscanner.model.domain.image.Image jpg = null;
			if (frontJpg != null) {
				jpg = toImage(frontJpg, null);
			}
			
			return new ImageScanned(tif, jpg);
		}
		
		private ImageScanned toImageScanned(Document<FileImage> rangerDocument) {
			br.com.webscanner.model.domain.image.Image tif = toImage(rangerDocument.getFrontTif(), rangerDocument.getRearTif());
			
			br.com.webscanner.model.domain.image.Image jpg = null;
			if (rangerDocument.getFrontJpg() != null) {
				jpg = toImage(rangerDocument.getFrontJpg(), rangerDocument.getRearJpg());
			}
			
			return new ImageScanned(tif, jpg);
		}
		
		public br.com.webscanner.model.domain.image.Image toImage(FileImage front, FileImage rear) {
			
			ImageInfo frontInfo = new ImageInfo(System.currentTimeMillis() + 1, new File(front.getFile()));
			if (front.isIqaFailed()) {
				frontInfo.setTestsId(front.getTestIds());
			}
			
			ImageInfo rearInfo = null;
			if (rear != null) {
				rearInfo = new ImageInfo(System.currentTimeMillis() + 2, new File(rear.getFile()));
				if (rear.isIqaFailed()) {
					rearInfo.setTestsId(rear.getTestIds());
				}
			}
			return new br.com.webscanner.model.domain.image.Image(frontInfo, rearInfo);
		}
	}
}