package br.com.webscanner.model.domain.export.transp02;

import static br.com.webscanner.util.FileManagement.writeUtf8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.rpc.ServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import br.com.dao.StartService;
import br.com.webscanner.exception.ExportException;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.MessageSupport;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.model.domain.export.Exportable;
import br.com.webscanner.util.CalendarioUtils;
import br.com.webscanner.util.CsvBuilder;
import br.com.webscanner.util.FileManagement;
import br.com.webscanner.util.StringEncryptor;
import br.com.webscanner.util.TXTReader;
import br.com.webscanner.util.WebscannerUtil;
import br.com.webscanner.util.ZipUtil;
import br.com.webscanner.view.MainDesenv;
import br.com.webscanner.view.MessagePanel.MessageLevel;

public class TRANSP02Export implements Exportable {

	private String path = null;
	private static Logger logger = LogManager.getLogger(TRANSP02Export.class.getName());
	private boolean exported;
	private Set<Message> messages = new HashSet<Message>();
	private File tempFolder = null;
	MessageSupport suporte = null;
	private static final String BLANK_PAGE = "paginaEmBranco";
	

	public TRANSP02Export(String path) {
		this.path = path;
    	this.tempFolder = new File("C:" + System.getProperty("file.separator") + "Nota Fiscal" +  System.getProperty("file.separator") + "Multas");
	}

	@Override
	public void export(Product product) throws ExportException {
		logger.info("Iniciando a exportação do produto TRANSP01");
		FileManagement fm = new FileManagement();
		messages.clear();
		List<Document> documentosTodos = WebscannerUtil.removerPaginasEmBranco(product.getDocuments(), BLANK_PAGE);
    	
	    verificarECriarPasta(tempFolder);
		File pastaMultas = tempFolder;
			
			for (Document documentoAtual : documentosTodos) {	
			try {
				com.itextpdf.text.Document documentoPDF = new com.itextpdf.text.Document();
				String valorProtocolo = documentoAtual.getFieldByName("nomeMulta").getValue().toString();
				logger.info("INICIANDO EXPORTAÇÃO DA MULTA PARA PASTA LOCAL");
				Image img = Image.getInstance(documentoAtual.getContent().getMainFile() + "");
				img.scaleToFit(770, 523);
		        documentoPDF.setPageSize(PageSize.A4);
		        String destinoPDF = pastaMultas + System.getProperty("file.separator") + valorProtocolo + ".pdf";
				PdfWriter.getInstance(documentoPDF, new FileOutputStream(destinoPDF));				
				documentoPDF.open();
				documentoPDF.add(img);
			    documentoPDF.close();
				logger.info("MULTA: "+ documentoAtual.getFieldById(1).getValue().toString() +"EXPORTADA COM SUCESSO");
				}catch (Exception e) {
				// TODO: handle exception
				logger.error("ERRO AO EXPORTAR A MULTA : " + documentoAtual.getFieldByName("numeroNota").getValue().toString() + "PARA A PASTA LOCAL," + e);
				}		
			try {
				com.itextpdf.text.Document documentoPDF = new com.itextpdf.text.Document();
				String valorProtocolo = documentoAtual.getFieldByName("nomeMulta").getValue().toString();
				logger.info("INICIANDO EXPORTAÇÃO DA MULTA PARA PASTA Servidor");
				Image img = Image.getInstance(documentoAtual.getContent().getMainFile() + "");
				img.scaleToFit(770, 523);
		        documentoPDF.setPageSize(PageSize.A4);
		        String destinoPDF = path + System.getProperty("file.separator") + valorProtocolo + ".pdf";
				PdfWriter.getInstance(documentoPDF, new FileOutputStream(destinoPDF));				
				documentoPDF.open();
				documentoPDF.add(img);
			    documentoPDF.close();
				logger.info("MULTA: "+ documentoAtual.getFieldById(1).getValue().toString() +"EXPORTADA COM SUCESSO");
				}catch (Exception e) {
				// TODO: handle exception
				logger.error("ERRO AO EXPORTAR A MULTA : " + documentoAtual.getFieldByName("numeroNota").getValue().toString() + "PARA O SERVIDOR," + e);
				}
			}	
			messages.add(new Message(Bundle.getString("Lote exportado com sucesso!"),
					MessageLevel.INFO));
		

}
	
  public void verificarECriarPasta(File pasta) {
	  if (!pasta.exists()) {
			FileManagement.createFolder(String.valueOf(pasta));
		}
  }
	
	@Override
	public boolean isExported() {
		return this.exported;
	}

	@Override
	public void exportCompleted() {
		this.exported = false;
	}

	@Override
	public Set<Message> getMessages() {
		return this.messages;
	}

	@Override
	public void setMessageSupport(MessageSupport support) {
		this.suporte = support;
	}

}
