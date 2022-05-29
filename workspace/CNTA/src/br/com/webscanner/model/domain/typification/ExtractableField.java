package br.com.webscanner.model.domain.typification;

import br.com.webscanner.model.domain.FieldModel;

/**
 * Representa um dado extraído de um QRCode onde o mesmo é inserido em um field de um documento.
 * @author Jonathan Camara
 *
 */
public class ExtractableField {
	private int index;
	private int length;
	private FieldModel fieldModel;
	private int fieldId;
	
	public ExtractableField() { }
	
	public ExtractableField(int fieldId) {
		this.fieldId = fieldId;
	}
	
	public int getFieldId() {
		return fieldId;
	}
	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public FieldModel getFieldModel() {
		return fieldModel;
	}
	public void setFieldModel(FieldModel fieldModel) {
		this.fieldModel = fieldModel;
	}
}
