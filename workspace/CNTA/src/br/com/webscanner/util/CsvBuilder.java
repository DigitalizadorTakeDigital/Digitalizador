/**
 * 
 */
package br.com.webscanner.util;


/**
 * 
 * Classe auxiliar para criação de arquivos do tipo CSV.
 * @author fernando.germano
 * 
 */
public class CsvBuilder {
	private StringBuilder layout;
	private char separator = ';';
	
	/**
	 * Utiliza o separador padrão de CSV (';')
	 */
	public CsvBuilder() {
		this.layout = new StringBuilder();
	}
	
	public CsvBuilder(char separator) {
		this();
		this.separator = separator;
	}
	
	public void addColumn(String value){
		this.layout.append(value);
		this.layout.append(this.separator);
	}
	
	public void addColumn(int value){
		this.layout.append(value);
		this.layout.append(this.separator);
	}
	
	public void addColumn(char value){
		this.layout.append(value);
		this.layout.append(this.separator);
	}
	
	public void addColumn(long value){
		this.layout.append(value);
		this.layout.append(this.separator);
	}
	
	public void newRow(){
		this.layout.append(System.getProperty("line.separator"));
	}
	
	public String getCsv(){
		return layout.toString();
	}
}