/**
 * 
 */
package br.com.webscanner.model.domain.export;

import java.util.Set;

import br.com.webscanner.exception.ExportException;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.MessageSupport;
import br.com.webscanner.model.domain.Product;

/**
 * Interface que define o comportamento de uma classe de exportação.
 * @author Diego
 *
 */
public interface Exportable {
	/**
	 * Executa exportação de um produto.
	 * @param product
	 */
	void export(Product product) throws ExportException;
	
	/**
	 * Flag que indica se a exportação foi concluída.
	 * @return true - se a exportação foi realizada. <br/>
	 * false - se a exportação não foi realizada.
	 */
	boolean isExported();
	
	/**
	 * Atualiza o status da exportação para true.
	 */
	void exportCompleted();
	
	/**
	 * Recupera as mensagens de exportação de um documento
	 * @return
	 */
	Set<Message> getMessages();
	
	void setMessageSupport(MessageSupport support);
}