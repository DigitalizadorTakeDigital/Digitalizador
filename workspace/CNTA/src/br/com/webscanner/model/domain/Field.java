package br.com.webscanner.model.domain;

import java.util.Comparator;

/**
 * Representa o dado de um campo de um documento.
 * @author Jonathan Camara
 *
 */
public class Field implements Comparator<Field>, Comparable<Field>{
	private int id;
	private String name;
	private Object value;
	private boolean valid;
	private FieldModel fieldModel;
	
	public Field(FieldModel fieldModel){
		this.id = fieldModel.getId();
		this.name = fieldModel.getDisplayName();
		this.fieldModel = fieldModel;
		this.value = "";
	}
		
	public Field(int id){
		this.id = id;
	}
	
	public Field(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Object getValue() {
		return value;
	}
	
	public FieldModel getFieldModel() {
		return this.fieldModel;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public void setFieldModel(FieldModel fieldModel) {
		this.fieldModel = fieldModel;
	}
	
	@Override
	public int compare(Field o1, Field o2) {
		return o1.compareTo(o2);
	}

	@Override
	public int compareTo(Field o) {
		return new Integer(this.id).compareTo(o.getId());
//		return this.name.compareTo(o.getName());
	}
}