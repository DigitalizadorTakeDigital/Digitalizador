/**
 * 
 */
package br.com.webscanner.model.domain.export.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.lingala.zip4j.exception.ZipException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.ExportException;
import br.com.webscanner.exception.FileEncryptException;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.MessageSupport;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.model.domain.ScannerProperties;
import br.com.webscanner.model.domain.export.Exportable;
import br.com.webscanner.util.FileEncryptor;
import br.com.webscanner.util.FileManagement;
import br.com.webscanner.util.StringEncryptor;
import br.com.webscanner.util.ZipUtil;
import br.com.webscanner.util.xml.XmlUtil;
import br.com.webscanner.view.MessagePanel.MessageLevel;

/**
 * @author Diego
 *
 */
public class XMLExport implements Exportable {
	private static Logger logger = LogManager.getLogger(XMLExport.class.getName());
	private boolean exported;
	private List<DocumentXmlExportModel> documentXmlExportModels;
	private String path;
	private Set<Message> messages;
	private String triggerFile;
	private boolean encrypt;
	
	public XMLExport(String path, String triggerFile, boolean encrypt){
		this.path = path;
		this.triggerFile = triggerFile;
		this.encrypt = encrypt;
		this.documentXmlExportModels = new ArrayList<DocumentXmlExportModel>();
		this.messages = new HashSet<Message>();
	}
	
	@Override
	public void export(Product product) throws ExportException{
		messages.clear();
		logger.info("Iniciando exportação XML. {}", path);
		
		if(!new File(path).exists()){
			logger.error("Caminho de exportação inexistente.");
			messages.add(new Message(Bundle.getString("file.path.notFound", path), MessageLevel.ERROR));
			throw new ExportException("Caminho de exportação inexistente");
		}
		
		DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String idLot = StringEncryptor.removerAcentos(System.getenv("computername")) + formatter.format(new Date());
		File xmlFile;
		try {
			
			ArrayList<File> files = null;
			
			if(documentXmlExportModels.isEmpty()){
				files = product.getAllImagesFilesScanned();
			} else{
				files = product.getImagesByModels(documentXmlExportModels);
			}
			
			xmlFile = XmlUtil.generateXML(product, ApplicationData.getProcessAgency(), new ScannerProperties(), idLot, documentXmlExportModels);			

			files.add(xmlFile);
			File zip = ZipUtil.compact(idLot, files, path);
			
			String extension = ".zip";
			if(encrypt){
				try {
					extension = ".detr";
					FileEncryptor.encrypt(zip, new File(zip.getParent(), zip.getName().replace(".zip", extension)));
					FileManagement.delete(zip);
				} catch (FileEncryptException e) {
					logger.error(e.getMessage());
					messages.add(new Message(Bundle.getString("export.error", "XML", "Não foi possível criptografar o arquivo."), MessageLevel.INFO));
					throw new ExportException(e.getMessage());
				}
			}
			
			if(this.triggerFile.length() > 0){
				FileManagement.createFile(path, idLot + triggerFile);
			}
			
			messages.add(new Message(Bundle.getString("export.file.success", idLot + extension), MessageLevel.INFO));
			
			FileManagement.delete(xmlFile);
			
			this.exported = true;
		} catch (FileNotFoundException e) {
			logger.error("Caminho inexistente. Mensagem: " + e.getMessage());
			messages.add(new Message(Bundle.getString("file.path.notFound", path), MessageLevel.ERROR));
			throw new ExportException("Caminho inexistente. " + e.getMessage());
		} catch (ZipException e) {
			logger.error("Erro ao compactar arquivos. Mensagem: " + e.getMessage());
			messages.add(new Message(Bundle.getString("compression.error"), MessageLevel.ERROR));
			throw new ExportException("Erro ao compactar arquivos. " + e.getMessage());
		} catch (IOException e) {
			logger.error("Erro ao gerar arquivos de exportação. Mensagem: " + e.getMessage());
			messages.add(new Message(Bundle.getString("export.file.error"), MessageLevel.ERROR));
			throw new ExportException("Erro ao gerar arquivos de exportação. Mensagem: " + e.getMessage());
		}
		logger.info("Exportação XML concluída com sucesso.");
	}

	@Override
	public boolean isExported() {
		return this.exported;
	}

	@Override
	public void exportCompleted() {
		this.exported = false;
	}
	public List<DocumentXmlExportModel> getDocumentXmlExportModels() {
		return documentXmlExportModels;
	}
	
	public boolean addDocumentXmlExportModel(DocumentXmlExportModel model){
		return this.documentXmlExportModels.add(model);
	}
	
	@Override
	public Set<Message> getMessages() {
		return messages;
	}

	@Override
	public void setMessageSupport(MessageSupport support) {}
}