package br.com.webscanner.util.xml;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Classe representa um XML
 * @author Jonathan Camara
 *
 */
public class XMLData {
	private String root;
	private String encoding;
	private List<XMLTag> childrens;
	private Map<String, String> attributes;
	
	/**
	 * Cria um xml informando o root
	 * @param root - Root do xml
	 */
	XMLData(String root) {
		this.root = root;
		this.childrens = new LinkedList<XMLTag>();
	}
	
	/**
	 * Cria um xml informando o root e o enconding
	 * @param root - Root do xml
	 * @param encoding - Encoding do xml
	 */
	public XMLData(String root, String encoding) {
		this(root);
		this.encoding = encoding;
	}
	
	/**
	 * Adiciona uma tag ao root do xml
	 * @param tag - {@link XMLTag} que será adicionada
	 * @return - <b>true</b> - se a tag foi adicionada com sucesso
	 * <b>false</b> - se a tag não foi adicionada;
	 */
	public boolean appendTag(XMLTag tag){
		return childrens.add(tag);
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
	
	/**
	 * Constroi um xml com base nas tags e informações do {@link XMLData}
	 * @return - Retorna uma String com o conteúdo do xml.
	 */
	public String build(){
		StringBuilder builder = new StringBuilder();
		
		builder.append("<?xml version=\"1.0\" encoding=\"").append(encoding != null ? encoding : "UTF-8").append("\"?>").append("\n");
		builder.append("<").append(root).append(">").append("\n");
		
		build(builder, childrens, 1);
		
		builder.append("</").append(root).append(">");
		
		return builder.toString();
	}
	
	private void build(StringBuilder builder, List<XMLTag> tags, int tabCount){
		for(XMLTag tag : tags){
			String tab = String.format("%0" + tabCount + "d", 0).replace("0", "\t");
			builder.append(tab).append("<").append(tag.getTag());

			if(tag.getAttributes().size() > 0){
				for(Entry<String, String> entry : tag.getAttributes().entrySet()){
					builder.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
				}
			}
			
			if(tag.hasChildrens()){
				builder.append(">").append("\n");
				build(builder, tag.getChildrens(), tabCount + 1);
				builder.append(tab).append("</").append(tag.getTag()).append(">").append("\n");
			} else{
				builder.append(">").append(tag.getValue());
				builder.append("</").append(tag.getTag()).append(">").append("\n");
			}
		}
	}
}
