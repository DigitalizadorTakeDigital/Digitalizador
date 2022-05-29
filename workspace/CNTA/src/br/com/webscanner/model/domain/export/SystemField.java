package br.com.webscanner.model.domain.export;
	
/**
 * Representa um atributo de classe que poderá ser utilizado na exportação de um produto.
 * @author Diego
 *
 */
public class SystemField {
	private String attribute;
	private String alias;

	public SystemField(String attribute, String alias){
		this.attribute = attribute;
		this.alias = alias;
	}
	
	public String getAttribute() {
		return this.attribute;
	}
	
	public String getAlias(){
		return this.alias;
	}
}
