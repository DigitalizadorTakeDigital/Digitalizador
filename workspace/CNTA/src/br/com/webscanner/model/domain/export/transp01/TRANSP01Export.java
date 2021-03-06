package br.com.webscanner.model.domain.export.transp01;

import static br.com.webscanner.util.FileManagement.writeUtf8;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.ExportException;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.MessageSupport;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.model.domain.export.Exportable;
import br.com.webscanner.util.CsvBuilder;
import br.com.webscanner.util.FileManagement;
import br.com.webscanner.util.WebscannerUtil;   
import br.com.webscanner.view.MessagePanel.MessageLevel;

public class TRANSP01Export implements Exportable {

	private String path = null;
	private String pathCSV = null;
	private String pathRomaneio = null;
	
	MessageSupport suporte = null;

	private static Logger logger = LogManager.getLogger(TRANSP01Export.class.getName());
	DateFormat ano = new SimpleDateFormat("yyyy");
	DateFormat dia = new SimpleDateFormat("dd");
	DateFormat mes = new SimpleDateFormat("MM");
	private boolean exported;
	private Set<Message> messages = new HashSet<Message>();
	private File tempFolder = null;
	private static final String BLANK_PAGE = "paginaEmBranco";
	File destinoRomaneios = null;
	boolean notaDuplicada = false;
	public static String numeroVerso = null;

	public TRANSP01Export(String path, String pathCSV, String pathRomaneio) {
		this.path = path;
		this.pathCSV = pathCSV;
		this.pathRomaneio = pathRomaneio;
    	this.tempFolder = new File("C:" + System.getProperty("file.separator") + "Nota Fiscal");
	}

	@Override
	public void export(Product product) throws ExportException {
		logger.info("Iniciando a exporta????o do produto TRANSP01");
		messages.clear();
		List<Document> allDocs = WebscannerUtil.removerPaginasEmBranco(product.getDocuments(), BLANK_PAGE);;
		List <String> protocols =  new ArrayList<>();    
	    
	    verifyAndCreateFolder(tempFolder);
		verifyAndCreateFolder(new File( tempFolder + System.getProperty("file.separator") +  "NFs"));
	    verifyAndCreateFolder(new File( tempFolder + System.getProperty("file.separator") +  "CSVs" +System.getProperty("file.separator") + "Fila"));
	    verifyAndCreateFolder(new File( tempFolder + System.getProperty("file.separator") +  "Romaneios"));
	
	
		try {	
			int contRomaneio = 0;
			for (Document currentDoc : allDocs) {
                    String protocolValue  = getProtocol(currentDoc);
					String romaneioValue = getProtocol(currentDoc);
					
					notaDuplicada = isNFDuplicated(protocols, currentDoc, protocolValue);

					if (protocolValue != null) {
						protocols.add(protocolValue);
					}					
					if(!notaDuplicada) {
					String imageName = getImageFinalName(currentDoc,  protocolValue, romaneioValue);				
					exportImage(currentDoc, contRomaneio, tempFolder + System.getProperty("file.separator") +  "NFs", imageName);
					contRomaneio --;
					exportImage(currentDoc, contRomaneio, path, imageName);	
					}
			
	}
	generateCSV(allDocs,new File( tempFolder + System.getProperty("file.separator") +  "CSVs" +System.getProperty("file.separator") + "Fila"));
	generateCSV(allDocs, new File(pathCSV));
	messages.add(new Message(Bundle.getString("LOTE EXPORTADO COM SUCESSO!"),MessageLevel.INFO));
 	}catch (Exception e) {
 		messages.add(new Message(Bundle.getString("ERRO AO EXPORTAR DOCUMENTOS : " + String.valueOf(e)),
				MessageLevel.ERROR));
		logger.error("Error. ", e);
	}
}

	private String getProtocol(Document currentDoc) {
		if (currentDoc.getDocumentModel().getName().equals("NF") || currentDoc.getDocumentModel().getName().equals("NF2")){ 
			numeroVerso = currentDoc.getFieldByName("numeroNota").getValue().toString();
				int tamanhoNumeroNota = numeroVerso.length();
				String zerosNotas = null;
				while (tamanhoNumeroNota < 9) {
					if (zerosNotas == null) {
						zerosNotas = "0";
					}else {
						zerosNotas += "0";	
					}					
					tamanhoNumeroNota++;
				}
				if (zerosNotas != null) {
				 numeroVerso = zerosNotas + numeroVerso;
				}	
				return numeroVerso;						
		} else if (currentDoc.getDocumentModel().getName().equals("romaneio")) {
			return currentDoc.getFieldByName("protocolo").getValue().toString();
		}
		return null;
	}
	
	private Boolean isNFDuplicated (List<String> protocolos, Document currentDoc,String protocolValue){
		if(protocolos.size() > 0 && !currentDoc.getDocumentModel().getName().equals("romaneio") ) {
			for (String protocoloAtual : protocolos) {
				if(null != protocolValue && protocolValue.equals(protocoloAtual)) 
				{
					return true;
				}
			}
		}
		return false;		
	}
	
	public void verifyAndCreateFolder(File folder) {
	  if (!folder.exists()) {
			FileManagement.createFolder(String.valueOf(folder));
		}
  }
	
	public String getImageFinalName(Document currentDoc, String protocolValue, String romaneioValue){
		String imageName = "";
		if (currentDoc.getDocumentModel().getName().equals("NF") || currentDoc.getDocumentModel().getName().equals("NF2") ) {
			imageName = protocolValue+".jpg";
		}else if (currentDoc.getDocumentModel().getName().equals("romaneio")){
			imageName = romaneioValue;
		}else{
			imageName = numeroVerso + "_V.jpg";
		}
		return imageName;
	}

	private void exportImage(Document  currentDoc, int countRomaneio, String path, String nomeImagem){
		try {
			if (currentDoc.getDocumentModel().getName().equals("NF") || currentDoc.getDocumentModel().getName().equals("NF2")){
		    logger.info("INICIANDO EXPORTA????O DA NOTA FISCAL PARA PASTA LOCAL");
			FileManagement.copy(currentDoc.getContent().getJpgFile(), new File(path+System.getProperty("file.separator")+nomeImagem));
			logger.info("NOTA FISCAL: "+ currentDoc.getFieldById(1).getValue().toString() +"EXPORTADA COM SUCESSO");
			}else if (currentDoc.getDocumentModel().getName().equals("romaneio")) {
				logger.info("INICIANDO EXPORTA????O DO ROMANEIO PARA PASTA LOCAL");
				countRomaneio++;
				FileManagement.copy(currentDoc.getContent().getJpgFile(), new File(pathRomaneio+System.getProperty("file.separator")+nomeImagem + "_"+countRomaneio+".jpg"));
				logger.info("ROMANEIO: "+ currentDoc.getFieldById(1).getValue().toString() +"EXPORTADA COM SUCESSO");
			}
			else{
	          	   logger.info("INICIANDO EXPORTA????O DO VERSO PARA PASTA LOCAL");
					FileManagement.copy(currentDoc.getContent().getJpgFile(), new File(path+System.getProperty("file.separator")+nomeImagem));
			}
			}catch (Exception e) {
			logger.error("ERRO AO EXPORTAR A NOTA FISCAL : " + currentDoc.getFieldByName("numeroNota").getValue().toString() + "PARA A PASTA LOCAL," + e);
			}
	}
	
	private File generateCSV(List<Document> documents, File dirDocs) throws IOException, ParseException {

		DateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmssSSS");

		String firstDocument = "Csv_Notas_Fiscais_"+formatter.format(new Date() );
		
		CsvBuilder csv = new CsvBuilder();
		csv.addColumn("Numero de Protocolo");
		csv.addColumn("Numero de Nota Fiscal");
		csv.addColumn("Data de Digitalizacao");
		csv.addColumn("Status");
		csv.addColumn("Horario da Digitalizacao");
		csv.newRow();

		for (int i = 0; i < documents.size(); i++) {
			if(!documents.get(i).getDocumentModel().getName().toString().equals("romaneio")) {			
				if(documents.get(i).getDocumentModel().getName().equals("NF") || documents.get(i).getDocumentModel().getName().equals("NF2")){
					String valorProtocolo = documents.get(i).getFieldById(1).getValue().toString();
					int tamanhoNumeroNota = valorProtocolo.length();
					String zerosNotas = null;
						while (tamanhoNumeroNota < 9) {
							if (zerosNotas == null)
								zerosNotas = "0";
							else
								zerosNotas += "0";			
			
							tamanhoNumeroNota++;
			}
			if (zerosNotas != null) valorProtocolo = zerosNotas + valorProtocolo;
            	
			csv.addColumn("P" + "000000000000000000000000" + valorProtocolo + "00000000000");
			csv.addColumn("P" + valorProtocolo);
			csv.addColumn(documents.get(i).getDateCapture());
			numeroVerso =  valorProtocolo;
			}else {
			String finalVerso = "_V";
			csv.addColumn("P" + "000000000000000000000000" + numeroVerso + "00000000000" + finalVerso); 
			csv.addColumn("P" + numeroVerso + finalVerso); 
			csv.addColumn(documents.get(i).getDateCapture());
			}
			
			csv.newRow();
			}
		}
		String textoCSV = csv.getCsv();
		return writeUtf8(textoCSV, new File(dirDocs + System.getProperty("file.separator") + firstDocument
				+  ".csv"), Charset.forName("UTF-8"));
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
