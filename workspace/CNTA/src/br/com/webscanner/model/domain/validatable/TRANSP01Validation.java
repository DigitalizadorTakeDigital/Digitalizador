package br.com.webscanner.model.domain.validatable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.view.MessagePanel.MessageLevel;

public class TRANSP01Validation implements Validatable {

	private static Logger logger = LogManager.getLogger(TRANSP01Validation.class.getName());
	private Set<Message> messages;
		
	public TRANSP01Validation(){
		this.messages = new HashSet<Message>();
	}
	
	@Override
	public boolean validate(Product product) {
		int countCapas = 0;
		int countDocument = 0;
		int countBlankPage = 0;
		
		logger.info("Iniciando validação do produto.");
		messages.clear();	
		
		
		Map<Document,List<Document>> listaDeLotes = new LinkedHashMap<Document, List<Document>>();
		Document key = null;		
		for (Document document : product.getDocuments()) {
			if(document.getDocumentModel().getName().equals("NF")||document.getDocumentModel().getName().equals("NF2")  ){
				listaDeLotes.put(document, new ArrayList<Document>());
				key = document;
				countCapas++;
			}else{
				if( document.getDocumentModel().getName().equals("romaneio")){
					countDocument++;
				}else if (document.getDocumentModel().getName().equals("documento")){
					if(null != key){
						key =  null;						
					}else {
						messages.add(new Message(
								Bundle.getString("H? Documentos n?o reconhecidos no Lote, por favor digite manualmente o protocolo ou exclua-os do mesmo"),
								MessageLevel.ERROR));
						return false;
					}
				}
			}
		}	
		
		List<String> protocolos = new ArrayList<>();
		int protocolosDuplicados = 0;
		for (Document docNF : product.getDocuments()) {
			if(docNF.getDocumentModel().getName().equals("NF") || docNF.getDocumentModel().getName().equals("NF2")) {
				String valorProtocolo = docNF.getFieldByName("numeroNota").getValue().toString();
				if(protocolos.size() > 0) {
					for (String protocoloAtual : protocolos) {
						if(valorProtocolo.equals(protocoloAtual)) 
						{
							protocolosDuplicados ++;
						}
					}
					}
				
				
				protocolos.add(valorProtocolo);
				
			}
		}
		if(protocolosDuplicados > 0)
			JOptionPane.showMessageDialog(null, "Voce possui " + protocolosDuplicados + " documentos duplicados, sera exportada apenas a primeira pagina do mesmo");
		
		int countDocumentos = product.getDocuments().size();
		
		countCapas = countCapas - protocolosDuplicados;
		
		int valida = JOptionPane.showConfirmDialog(null, "Voce possui "+countCapas+" Notas Fiscais, "+ countDocument +" romaneios ", "Contador", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (valida != JOptionPane.OK_OPTION) {
			return false;
		}
						
		logger.info("Produto validado com sucesso.");
		return true;
	}
	
	@Override
	public Set<Message> getMessages() {
		return this.messages;
	}
	@Override
	public Document getErrorDocument() {
		return null;
	}
	@Override
	public boolean isForced() {
		return false;
		
	}
}
