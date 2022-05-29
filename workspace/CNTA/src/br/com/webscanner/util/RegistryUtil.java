package br.com.webscanner.util;
/**
 * 
 */


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;

/**
 * Classe responsável por Gerenciar informacoes no registro do Windows
 * @author Fernando Germano
 */
public class RegistryUtil {
	private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(RegistryUtil.class.getName());
	
	/**
	 * @param pathLeft 
	 * @param pathRight 
	 * @param keyToCompare
	 * @return TRUE se as chaves forem iguais ou False caso não sejam.
	 */
	public static boolean CompareRegistry(String pathLeft, String pathRight, String keyToCompare){
		try {
			Process process = Runtime.getRuntime().exec(new String[]{"reg", "compare", pathLeft, pathRight, "/v", keyToCompare});
			process.waitFor();
			if (process.exitValue() == 0){
				return true;
			}
			return false;
		} catch (IOException e) {
			logger.error("Erro ao comparar o registro: {}", e.getMessage());
			return false;
		} catch (InterruptedException e) {
			logger.error("Erro ao comparar o registro: {}", e.getMessage());
			return false;
		}
	}
	
	/**
	 * @param path - Path do Registro
	 * @param key - Chave do registro que irá adicionar ou atualizar o valor.
	 * @param type - Tipo de Registro
	 * @param value - valor
	 * @return True se gravou com sucesso, ou false caso tenha falhado.
	 */
	public static boolean setRegistry(String path, String key, String type, String value){
		try {
			Process process = Runtime.getRuntime().exec(new String[]{"reg", "add", path, "/v", key , "/f", "/t", type, "/d", value});
			process.waitFor(); 
			
			if(process.exitValue() != 0){
				System.err.println("Erro ao verificar o registro " + path + "\\" + key + ". " + printEcho(process.getErrorStream()));
				return false;
			}

			return true;
		} catch (IOException e) {
			logger.error("Erro ao gravar no registro: {}", e.getMessage());
			return false;
		} catch (InterruptedException e) {
			logger.error("Erro ao gravar no registro: {}", e.getMessage());
			return false;
		}
	}
		
	/**
	 * Recupera as informa��es do console
	 * @param is - InputStream
	 * @return - Dados que s�o impressos no console.
	 */
	private static String printEcho(InputStream is){
		StringWriter sw = null;
		
		try {
			sw = new StringWriter();
			int i = -1;
			while((i = is.read()) != -1){
				sw.write(i);
			}
			
			return sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Recupera o valor da chave no registro.
	 * @param key Caminho da chave
	 * @param valueName Nome da chave
	 * @return Valor da chave caso exista ou nome da chave se não for possível recuperá-lo.
	 */
	public static String getValue (String key, String valueName) {
		try {
			return WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, key, valueName);
		} catch (IllegalArgumentException e) {
			return "";
		} catch (IllegalAccessException e) {
			return "";
		} catch (InvocationTargetException e) {
			return "";
		}
	}
}


