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

public class TRANSP02Validation implements Validatable {

	private static Logger logger = LogManager.getLogger(TRANSP02Validation.class.getName());
	private Set<Message> messages;
		
	public TRANSP02Validation(){
		this.messages = new HashSet<Message>();
	}
	
	@Override
	public boolean validate(Product product) {
		logger.info("Iniciando validação do produto.");
		messages.clear();	
		
        int valida = JOptionPane.showConfirmDialog(null, "Você possui Multas ", "Contador", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
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
