/**
 * 
 */
package br.com.webscanner.model.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o field de modelo de um documento.
 * @author Jonathan Camara
 *
 */
public class FieldModel {
	private int id;
	private String name;
	private String displayName;
	private FieldType type;
	private int maxlength;
	private List<Item> items;
	private boolean hidden;
	private boolean readonly;
	private boolean required;
	private boolean fixedSize;
	private String validationMethod;
	private boolean greaterThanZero;
	private List<String> dependents;

	public FieldModel(String name){
		this.name = name;
	}
	
	public FieldModel(int id, String name, String displayName, FieldType type, int maxlength, List<Item> items, boolean hidden, 
			boolean readonly, boolean required, String validationMethod, boolean fixedSize){
		this.id = id;
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.maxlength = maxlength;
		this.items = items;
		this.hidden = hidden;
		this.readonly = readonly;
		this.required = required;
		this.validationMethod = validationMethod;
		this.fixedSize = fixedSize;
		this.greaterThanZero = false;
		this.dependents = new ArrayList<String>();
	}
	
	public FieldModel(int id, String name, String displayName, FieldType type, int maxlength, List<Item> items, boolean hidden, 
			boolean readonly, boolean required, String validationMethod, boolean fixedSize, boolean greaterThanZero, List<String> dependents){
		this.id = id;
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.maxlength = maxlength;
		this.items = items;
		this.hidden = hidden;
		this.readonly = readonly;
		this.required = required;
		this.validationMethod = validationMethod;
		this.fixedSize = fixedSize;
		this.greaterThanZero = greaterThanZero;
		this.dependents = dependents;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public FieldType getType() {
		return type;
	}
	public int getMaxlength() {
		return maxlength;
	}
	public List<Item> getItems() {
		return items;
	}
	public boolean isHidden() {
		return hidden;
	}
	public boolean isReadonly() {
		return readonly;
	}
	public boolean isRequired() {
		return required;
	}
	public String getValidationMethod() {
		return validationMethod;
	}
	public boolean isFixedSize() {
		return fixedSize;
	}
	public boolean isGreaterThanZero() {
		return greaterThanZero;
	}
	public void setGreaterThanZero(boolean greaterThanZero) {
		this.greaterThanZero = greaterThanZero;
	}
	public List<String> getDependents() {
		return dependents;
	}
	public void setDependents(List<String> dependents) {
		this.dependents = dependents;
	}

	@Override
	public String toString() {
		return "FieldModel [id=" + id + ", name=" + name + ", displayName="
				+ displayName + ", type=" + type + ", maxlength=" + maxlength
				+ ", items=" + items + ", hidden=" + hidden + ", readonly="
				+ readonly + ", required=" + required + ", validationMethod="
				+ validationMethod + "]";
	}
}