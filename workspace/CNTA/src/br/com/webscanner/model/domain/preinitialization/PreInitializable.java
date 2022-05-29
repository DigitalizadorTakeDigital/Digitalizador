/**
 * 
 */
package br.com.webscanner.model.domain.preinitialization;

import java.util.List;

import br.com.webscanner.exception.InvalidParametersException;
import br.com.webscanner.exception.MissingParametersException;
import br.com.webscanner.exception.PreInitializationException;
import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.domain.Product;

/**
 * Representa o comportamento de uma classe de pré-inicialização de produto.
 * @author Diego
 *
 */
public interface PreInitializable {
	
	void initialize(Product product) throws PreInitializationException, MissingParametersException, InvalidParametersException;
	
	void checkParameters() throws MissingParametersException, InvalidParametersException;
	
	List<DocumentModel> filtroDocumentos(List<DocumentModel> documents);
}
