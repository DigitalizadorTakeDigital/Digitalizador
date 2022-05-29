/**
 * 
 */
package br.com.webscanner.model.domain.export.xml;

import java.util.ArrayList;
import java.util.List;

import br.com.webscanner.model.domain.export.SystemField;

/**
 * Representa um modelo de documento que poderá ser exportado para XML.
 * @author Diego
 */
public class DocumentXmlExportModel {
	private long id;
	List<FieldXmlExportModel> fieldXmlExportModels;
	List<SystemField> systemFields;
	
	public DocumentXmlExportModel(long id){
		this.id = id;
		this.fieldXmlExportModels = new ArrayList<FieldXmlExportModel>();
		this.systemFields = new ArrayList<SystemField>();
	}

	public List<FieldXmlExportModel> getFieldXmlExportModels() {
		return fieldXmlExportModels;
	}

	public void setFieldXmlExportModels(
			List<FieldXmlExportModel> fieldXmlExportModels) {
		this.fieldXmlExportModels = fieldXmlExportModels;
	}

	public List<SystemField> getSystemFields() {
		return systemFields;
	}

	public void setSystemFields(List<SystemField> systemFields) {
		this.systemFields = systemFields;
	}

	public long getId() {
		return id;
	}
	
	public boolean addSystemField(SystemField systemField){
		return this.systemFields.add(systemField);
	}
	
	public boolean addFieldXmlExportModel(FieldXmlExportModel model){
		return this.fieldXmlExportModels.add(model);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentXmlExportModel other = (DocumentXmlExportModel) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "DocumentXmlExportModel [id=" + id + ", fieldXmlExportModels="
				+ fieldXmlExportModels + ", systemFields=" + systemFields + "]";
	}
}
