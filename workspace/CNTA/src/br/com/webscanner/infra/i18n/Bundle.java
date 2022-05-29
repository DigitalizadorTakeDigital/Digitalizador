/**
 * 
 */
package br.com.webscanner.infra.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cria um Bundle para internacionalização da aplicação.
 * @author Jonathan Camara
 */
public class Bundle {
	private static Logger logger = LogManager.getLogger(Bundle.class.getName());
	private static ResourceBundle bundle;
	
	static {
		bundle = ResourceBundle.getBundle("messages", new Locale("pt", "BR"));
	}
	
	/**
	 * Recupera uma string a partir de uma chave;
	 * @param key - chave
	 * @return - Valor da chave configurada.
	 */
	public static String getString(String key){
		try {
			return bundle.getString(key);
		} catch (NullPointerException e) {
			logger.error("A chave {} nao existe no arquivo de mensagens", key);
			return "";
		} catch (MissingResourceException e) {
			logger.error(e);
			return key;
		}
	}
	
	/**
	 * Recupera um string a partir de uma chave. Este método possibilita a passagem de parametros.
	 * Ex. Erro ao executar o arquivo {0}. O diretorio {1} não será criado.
	 * O exemplo acima referencia dois parametros passados dinamicamente, os valores entre chaves representam
	 * a ordem dos parametros no array de objetos.
	 * @param key - chave
	 * @param params - parametros para formatação do valor da chave.
	 * @return Valor da chave configurada
	 */
	public static String getString(String key, Object ... params){
		String string = bundle.getString(key);
		MessageFormat format = new MessageFormat(string);
		return format.format(params);
	}
}