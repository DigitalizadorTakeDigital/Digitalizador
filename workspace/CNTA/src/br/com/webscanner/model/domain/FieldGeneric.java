/**
 * 
 */
package br.com.webscanner.model.domain;

import java.text.MessageFormat;
import java.util.List;

/**
 * Representa um modelo de field para um documento genérico.
 * @author Diego
 *
 */
public class FieldGeneric {
	private int id;
	private String name;
	private String displayName;
	private FieldType type;
	private int maxlength;
	List<Item> items;
	private boolean hidden;
	private boolean readonly;
	private boolean required;
	private boolean fixedSize;
	
	public FieldGeneric(int id, String name, String displayName, FieldType type, int maxLength, List<Item> items, boolean hidden,	boolean readonly, boolean required, boolean fixedSize){
		this.id = id;
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.maxlength = maxLength;
		this.items = items;
		this.hidden = hidden;
		this.readonly = readonly;
		this.required = required;
		this.fixedSize = fixedSize;
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

	public boolean isHidden() {
		return hidden;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public boolean isRequired() {
		return required;
	}
	
	public List<Item> getItems() {
		return items;
	}
	
	public boolean isFixedSize() {
		return fixedSize;
	}
	
	public String formatName(Object ... params){
		MessageFormat messageFormat = new MessageFormat(this.name);
		return messageFormat.format(params);
	}
	
	@Override
	public String toString() {
		return "".concat("id: ").concat(String.valueOf(id))
				.concat(" - name: ").concat(name)
				.concat(" - displayName: ").concat(displayName);
//				.concat(" - type: ").concat(type.toString())
//				.concat(" - maxlength: ").concat(String.valueOf(maxlength))
//				.concat(" - items: ").concat(items.toString())
//				.concat(" - hidden: ").concat(String.valueOf(hidden))
//				.concat(" - required: ").concat(String.valueOf(required))
//				.concat(" - readonly: ").concat(String.valueOf(readonly));
	}
}
