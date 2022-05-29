package br.com.webscanner.model.domain.preinitialization;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.InvalidParametersException;
import br.com.webscanner.exception.MissingParametersException;
import br.com.webscanner.exception.PreInitializationException;
import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.model.domain.extraction.ExtractionType;
import br.com.webscanner.model.domain.typification.Recognition;
import br.com.webscanner.model.domain.typification.specific.transp01.TRANSP01SpecificRecognition;

public class TRANSP01PreInitialization implements PreInitializable {
	private final static Logger LOGGER = LogManager.getLogger(TRANSP01PreInitialization.class.getName());
	
	@Override
	public void initialize(Product product) throws PreInitializationException, MissingParametersException, InvalidParametersException {
		LOGGER.info("Realizando pré-inicialização do produto");
		checkParameters();
		for (Recognition reconhecimento : product.getModel().getRecognitions()) {
			if (reconhecimento.getType() == ExtractionType.SPECIFIC) {
				TRANSP01SpecificRecognition adquReconhecimento = ((TRANSP01SpecificRecognition) reconhecimento.getRecognizable());
				
				DocumentModel documento = product.getModel().getDocumentModelById(adquReconhecimento.getIdDocumento());
				adquReconhecimento.setDocumento(documento);							
				return;
			}
		}		
		LOGGER.info("Pré-inicialização realizada com sucesso");	
	}

	@Override
	public void checkParameters() throws MissingParametersException, InvalidParametersException {
		
	}
	
	@Override
	public List<DocumentModel> filtroDocumentos(List<DocumentModel> documents) {
		return null;
	}
}
