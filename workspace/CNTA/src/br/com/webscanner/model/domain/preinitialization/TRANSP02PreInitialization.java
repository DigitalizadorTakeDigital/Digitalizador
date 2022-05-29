package br.com.webscanner.model.domain.preinitialization;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.InvalidParametersException;
import br.com.webscanner.exception.MissingParametersException;
import br.com.webscanner.exception.PreInitializationException;
import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.domain.Product;

public class TRANSP02PreInitialization implements PreInitializable {
	private final static Logger LOGGER = LogManager.getLogger(TRANSP02PreInitialization.class.getName());
	
	@Override
	public void initialize(Product product) throws PreInitializationException, MissingParametersException, InvalidParametersException {
		LOGGER.info("Realizando pré-inicialização do produto");
		LOGGER.info("Pré-inicialização realizada com sucesso");	
	}
	
	@Override
	public List<DocumentModel> filtroDocumentos(List<DocumentModel> documents) {
		return null;
	}

	@Override
	public void checkParameters() throws MissingParametersException,
			InvalidParametersException {
		// TODO Auto-generated method stub
		
	}
}
