package br.com.webscanner.controller;

import static br.com.webscanner.util.WebscannerUtil.generateSHA1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.ExportException;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Content;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.MessageSupport;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.export.Exportable;
import br.com.webscanner.model.domain.image.ImageImported;
import br.com.webscanner.model.domain.image.ImageInfo;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.model.domain.typification.Recognition;
import br.com.webscanner.model.domain.validator.Validator;
import br.com.webscanner.util.FileManagement;
import br.com.webscanner.view.MainDesenv;
import br.com.webscanner.view.MessagePanel.MessageLevel;
import br.com.webscanner.view.ProductScanner;
import br.com.webscanner.view.listener.MessageListener;
import br.com.webscanner.view.model.DocumentTableModel;

public class ProductScannerController {
	private ProductScanner productScanner;
	private Product product;
	private Document document;
	private List<Document> selectedDocuments;
	private long documentsCount;
	private long captureSequence;
	private static Logger logger = LogManager.getLogger(ProductScannerController.class.getName());
	private List<Message> messages;
	private boolean loading;
	MainDesenv mainDesenv;

	public ProductScannerController(Product product, ProductScanner productScanner) {
		this.product = product;
		this.productScanner = productScanner;
		this.documentsCount = 1;
		this.captureSequence = 1;
		this.selectedDocuments = new ArrayList<Document>();
		this.loading = false;
	}
	
	public Product getProduct() {
		return this.product;
	}
	
	public void moveTo(JTable table, Integer from, Integer to) {
		DocumentTableModel model = (DocumentTableModel) table.getModel();
		model.reorder(from, to);
	}
	
	public void clearSelectedDocuments(){
		this.selectedDocuments.clear();
		if (product.getDocuments().isEmpty()) {
			this.documentsCount = 1;
			this.captureSequence = 1;
		}
	}
	
	public boolean addSelectedDocument(Document document){
		return selectedDocuments.add(document);
	}
	
	
	public void enableDocumentSelection(boolean flag){
		productScanner.enableDocumentSelection(flag);
	}
	
	public void showMessage(Message message){
		productScanner.showMessage(message);
	}
	
	public void showImportDialog(){
		productScanner.showImportDialog();
	}
	
	public void showConfigScannerDialog(){
		productScanner.showConfigScannerDialog();
	}
	
	public void showMoveToDialog(final JTable table, final Document document, final int row){
		productScanner.showMoveToDialog(table, document, row);
	}
	
	public boolean addDocument(Document document){
		documentsCount++;
		captureSequence++;
		return this.product.getDocuments().add(document);
	}

	public void updateTable() {
		this.productScanner.updateTable();
	}
	
	public void setViewerLastDocument() {
		this.productScanner.setViewerLastDocument();
	}
	
	public long getDocumentsCount(){
		return documentsCount;
	}
	
	public long getCaptureSequence(){
		return captureSequence;
	}

	public void setActualDocument(Document document) {
		this.document = document;
	}
	
	public Document getActualDocument(){
		return this.document;
	}
	
	public List<Document> getSelectedDocuments(){
		return this.selectedDocuments;
	}

	public void updateDocumentSelection() {
		productScanner.updateDocumentsCombobox();
	}
	
	public void showLoading(boolean flag){
		this.loading = flag;
		productScanner.showLoading(flag);
	}
	
	public void showLoading(boolean flag, String message){
		this.loading = flag;
		if(flag){
			productScanner.showLoading(message);
		}else{
			productScanner.showLoading(flag);
		}
	}
	
	public void updateMessage(String message) {
		productScanner.updateMessage(message);
	}
	
	public void clearLot() {
		logger.info("Iniciando limpeza do Lote");
		productScanner.clearDocumentTableSelection();
		updateDocumentSelection();
		updateTable();
		setActualDocument(null);
		productScanner.updateDocumentsCombobox();
		productScanner.updateLeftPanel();
		productScanner.clearImagePanel();
		productScanner.enableDocumentSelection(false);
		productScanner.setCloseLotButtonEnabled(false);
		this.captureSequence = 1;
		this.documentsCount = 1;
		mainDesenv = new MainDesenv();
		mainDesenv.contadorDePaginas = 0;
		deleteImages();
		this.product.getDocuments().clear();
	}

	private void deleteImages() {
		logger.info("Deletando imagens escaneadas.");
		for(Document document : product.getDocuments()){
			for(File file : document.getContent().getContents()){
				FileManagement.delete(file);
			}
		}
	}
	
	public void export() throws ExportException{
		messages = new ArrayList<Message>();
		
		logger.info("Iniciando exportações do produto.");
		
		MessageSupport support = new MessageSupport(new MessageListener(this));
		
		try {
			if(this.product.getModel().getExports() != null){
				product.sequentializeDocuments();
				for(Exportable export : this.product.getModel().getExports()){
					if(!export.isExported()){
						try {
							export.setMessageSupport(support);
							export.export(this.product);
							export.exportCompleted();
							messages.addAll(export.getMessages());
						} catch (ExportException e) {
							messages.addAll(export.getMessages());
							throw e;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("StackTrace {}", e);
			throw new ExportException(e.getMessage());
		} catch (Error e) {
			logger.fatal(e);
			messages.add(new Message(" Erro na comunicação com Servidor, por gentileza enviar novamente ", MessageLevel.ERROR));
		}
	}
	
	public List<DocumentModel> getDocumentModels() {
		return this.product.getModel().getDocumentModels();
	}
	

	public boolean isProductValid() {
		logger.info("Iniciando validações do produto");
		
		logger.info("Validando documentos. (Tipificação e Fields)");
		int count = 0;
		for(Document document : product.getDocuments()){
			if (possuiTifInjection(document)) {
				showMessage(new Message(Bundle.getString("sha1.validation.error"), MessageLevel.ERROR));
				this.productScanner.selectDocument(count);
				return false;
			}
			
			Validator validator = new Validator();
			document.validate(validator);
			if(validator.hasError()){
				for(String message : validator.getMessages()){
					showMessage(new Message(message, MessageLevel.ERROR));
				}
				this.productScanner.selectDocument(count);
				return false;
			}
			count ++;
		}
		
		logger.info("Iniciando validação específica");
		if(!product.isValid()){
			if(product.getModel().getValidatable().isForced()){
				return askConfirmation(product.getModel().getValidatable().getMessages());
			} else {
				for(Message message : product.getModel().getValidatable().getMessages()){
					showMessage(message);
				}
				
				if(product.getModel().getValidatable().getErrorDocument() != null){
					this.productScanner.selectDocument(product.getModel().getValidatable().getErrorDocument());
				}
				return false;
			}
		}
		return true;
	}

	private boolean possuiTifInjection(Document document) {
		Content content = document.getContent();
		
		boolean resultado = false;
		try {
			if (content instanceof ImageScanned) {
				ImageScanned imageScanned = (ImageScanned) document.getContent();
				ImageInfo frente = imageScanned.getTiff().getFront();
				resultado = resultado || !frente.getSha1().equals(generateSHA1(frente.getFile()));
				
				ImageInfo verso = imageScanned.getTiff().getRear();
				if (verso != null)
					resultado = resultado || !verso.getSha1().equals(generateSHA1(verso.getFile()));
				
				if (imageScanned.getJpg() != null) {
					ImageInfo frenteJpg = imageScanned.getJpg().getFront();
					resultado = resultado || !frenteJpg.getSha1().equals(generateSHA1(frenteJpg.getFile()));
					
					ImageInfo versoJpg = imageScanned.getJpg().getRear();
					if (versoJpg != null) 
						resultado = resultado || !versoJpg.getSha1().equals(generateSHA1(versoJpg.getFile()));
				}
			} else if (content instanceof ImageImported) {
				ImageImported imageImported = (ImageImported) content;
				ImageInfo frente = imageImported.getImage().getFront();
				resultado = resultado || !frente.getSha1().equals(generateSHA1(frente.getFile()));
				
				ImageInfo verso = imageImported.getImage().getRear();
				if (verso != null)
					resultado = resultado || !verso.getSha1().equals(generateSHA1(verso.getFile()));
			}
		} catch (RuntimeException e) {
			logger.error(e);
		}
		return resultado;
	}
	
	private boolean askConfirmation(Set<Message> messages) {
		return productScanner.askConfirmation(messages);
	}

	public void updateLeftPanel() {
		this.productScanner.updateLeftPanel();
	}

	public void clearDocumentTableSelection() {
		this.productScanner.clearDocumentTableSelection();
	}

	public void updateDocumentsCombobox() {
		this.productScanner.updateDocumentsCombobox();
	}

	public void clearImagePanel() {
		this.productScanner.clearImagePanel();
	}

	public void updateMetadataPanel() {
		this.productScanner.updateMetadataPanel();
	}
	
	public void setCloseLotButtonEnabled(boolean enabled){
		this.productScanner.setCloseLotButtonEnabled(enabled);
	}
	
	public boolean documentListIsEmpty(){
		return product.getDocuments().size() == 0;
	}

	public List<Recognition> getRecognitions() {
		return this.product.getModel().getRecognitions();
	}
	
	public List<Message> getMessages() {
		return messages;
	}
	public void setEnableActions(boolean b) {
		productScanner.setEnableActions(b);
	}
	
	public void updateScannerSelection(Scanner scanner){
		this.productScanner.setScannerSelected(scanner);
	}
	
	public Scanner getScannerSelected(){
		return this.productScanner.getScannerSelected();
	}
	
	public boolean isLoading() {
		return this.loading;
	}
	
	public void prepareClosingView() {
		productScanner.clearDocumentTableSelection();
		updateDocumentSelection();
		updateTable();
		setActualDocument(null);
		productScanner.updateDocumentsCombobox();
		productScanner.updateLeftPanel();
		productScanner.clearImagePanel();
	}
	
	//Implementado por Luis Fernando
	public boolean askResume(String message){
		return this.productScanner.askResume(message);
	}
	
	public void showIqfMessage(Set<String> message) {
		productScanner.showIqfMessage(message);
	}
}