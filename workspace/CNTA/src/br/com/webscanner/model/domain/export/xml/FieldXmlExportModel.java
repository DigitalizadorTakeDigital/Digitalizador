/**
 * 
 */
package br.com.webscanner.model.domain.export.xml;

/**
 * Representa o model de um field de um documento que será exportado para XML.
 * @author Diego
 *
 */
public class FieldXmlExportModel {
	private int id;
	
	public FieldXmlExportModel(int id){
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "FieldXmlExportModel [id=" + id + "]";
	}
}
