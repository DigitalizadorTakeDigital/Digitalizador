package br.com.webscanner.util.xml;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Classe que representa uma tag em um xml
 * @author Jonathan Camara
 *
 */
public class XMLTag{
	private String tag;
	private String value;
	private List<XMLTag> childrens;
	private Map<String, String> attributes;
	
	/**
	 * Cria uma tag a partir do nome da tag
	 * @param tag - Nome da tag
	 */
	public XMLTag(String tag) {
		this.tag = tag;
		this.childrens = new LinkedList<XMLTag>();
		this.attributes = new LinkedHashMap<String, String>();
	}
	
	/** Cria uma tag a partir do nome e valor da tag
	 * @param tag - Nome da tag
	 * @param value - Valor da tag
	 */
	public XMLTag(String tag, String value) {
		this(tag);
		this.value = value;
	}
	
	/**
	 * Adiciona uma tag filha a esta tag
	 * @param tag - Tag Filha
	 * @return
	 */
	public boolean appendTag(XMLTag tag){
		return this.childrens.add(tag);
	}
	
	/** Adiciona atributos na tag
	 * @param attribute - Nome do atributo
	 * @param value - Valor do atributo
	 * @return <b>true</b> - se o atributo foi adicionado com sucesso </br>
	 * <b>false</b> - se o atributo já existe;
	 */
	public boolean addAttribute(String attribute, String value){
		if(attributes.containsKey(attribute)){
			return false;
		}else{
			attributes.put(attribute, value);
			return true;
		}
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getTag() {
		return tag;
	}
	
	Map<String, String> getAttributes() {
		return attributes;
	}
	
	public boolean hasChildrens() {
		return childrens != null && childrens.size() > 0;
	}
	
	List<XMLTag> getChildrens() {
		return childrens;
	}
}