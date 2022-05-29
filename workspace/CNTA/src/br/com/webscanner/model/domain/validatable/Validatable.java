/**
 * 
 */
package br.com.webscanner.model.domain.validatable;

import java.util.Set;

import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.Product;

/**
 * Interface que define o comportamento de uma classe de validação.
 * @author fernando.germano
 *
 */
public interface Validatable {
	
	boolean validate(Product product);

	Set<Message> getMessages();
	
	Document getErrorDocument();
	
	boolean isForced();
}
