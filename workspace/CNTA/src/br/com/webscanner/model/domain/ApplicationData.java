package br.com.webscanner.model.domain;

import java.util.HashMap;
import java.util.Map;

public class ApplicationData {
	private static ProcessAgency processAgency = new ProcessAgency(); ;
	private static Map<String, String> parameters = new HashMap<String, String>();
	private static String productId;
	private static Build build;
	
	private ApplicationData(){}
	
	
	public enum Build{
		DESKTOP, WEB, DESENV, EMBARCADO
	}
	/**
	 * Recupera o ID do produto selecionado
	 * @return
	 */
	public static String getProductId(){
		return productId;
	}
	
	/**
	 * Seta o ID do produto selecionado
	 * @return
	 */
	public static String setProductId(String productId){
		return ApplicationData.productId = productId;
	}
	
	/**
	 * Recupera o valor de um parâmetro recebido na inicialização da aplicação.
	 * @param key - Chave do parâmetro
	 * @return - Valor do parâmetro ou <b>null</b> se o parâmetro não existir
	 */
	public static String getParam(String key){
		return parameters.get(key);
	}
	
	/**
	 * Adiciona um parametro na aplicacão;
	 * @param key
	 * @param value
	 */
	public static void putParam(String key, String value){
		parameters.put(key, value);
	}
	
	/**
	 * Remove um parâmetro da aplicação
	 * @param key
	 * @return
	 */
	public static String removeParam(String key){
		return parameters.remove(key);
	}
	
	/**
	 * Verifica se o parâmetro buscado existe.
	 * @param key - Chave do parâmetro
	 * @return <b>true</b> - se o parâmetro foi recuperado na inicialização do aplicativo <br />
	 * <b>false</b> - se o parâmetro não existir
	 */
	public static boolean paramExists(String key){
		return parameters.get(key) != null;
	}
	
	/**
	 * Recupera os dados da agência processadora
	 * @return
	 */
	public static ProcessAgency getProcessAgency(){
		return processAgency;
	}
	
	/**
	 * Atualiza o Build de execução da aplicação
	 * @param build
	 */
	public static void setBuild(Build build){
		ApplicationData.build = build;
	}
	
	/**
	 * Recupera o Build de execução da aplicação
	 * @return
	 */
	public static Build getBuild(){
		return build;
	}
}
