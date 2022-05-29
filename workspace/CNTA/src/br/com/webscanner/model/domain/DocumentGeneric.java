/**
 * 
 */
package br.com.webscanner.model.domain;

import static br.com.webscanner.util.WebscannerUtil.isNull;
import static br.com.webscanner.util.WebscannerUtil.isNullOrEmpty;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.webscanner.model.domain.rule.Rule;
import br.com.webscanner.model.domain.validator.Validator;

/**
 * Representa um  documento genérico.
 * @author Diego
 *
 */
public class DocumentGeneric {
	private long id;
	private String name;
	private String displayName;
	private String tableDisplay;
	private int icor;
	private Group group;
	private List<FieldGeneric> fieldsGeneric;
	private List<ValidationFunction> validationFunctions;
	private Rule rule;
	
	public DocumentGeneric(long id, String name, String displayName, String tableDisplay, int icor, Group group){
		this.id = id;
		this.name = name;
		this.displayName = displayName;
		this.tableDisplay = tableDisplay;
		this.icor = icor;
		this.group = group;
		this.fieldsGeneric = new ArrayList<FieldGeneric>();
		this.validationFunctions = new ArrayList<ValidationFunction>();
	}
	
	public void validate(Validator validator) {
		validator.that(isNullOrEmpty(name), "Nome do documento deve ser preenchido");
		validator.that(isNullOrEmpty(displayName), "DisplayName do documento deve ser preenchido");
		validator.that(isNull(group), "Grupo do documento deve ser preenchido");
	}
	
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getTableDisplay() {
		return tableDisplay;
	}
	public int getIcor() {
		return icor;
	}
	public Group getGroup() {
		return group;
	}
	public List<FieldGeneric> getFields() {
		return fieldsGeneric;
	}
	public boolean addField(FieldGeneric fieldGeneric){
		return this.fieldsGeneric.add(fieldGeneric);
	}
	public String formatName(Object ... params){
		MessageFormat messageFormat = new MessageFormat(this.name);
		return messageFormat.format(params);
	}
	public List<ValidationFunction> getValidationFunctions(){
		return this.validationFunctions;
	}
	public boolean addValidationFunction(ValidationFunction validationFunction) {
		return this.validationFunctions.add(validationFunction);
	}
	public Rule getRule() {
		return rule;
	}
	public void setRule(Rule rule) {
		this.rule = rule;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("id: ").append(id).append(" - name: ").append(name).append(" - displayName: ").append(displayName)
			.append(" - icor: ").append(icor).append(" - group: ").append(group).append("\n");
		for (FieldGeneric fieldGeneric : fieldsGeneric) {
			builder.append(fieldGeneric.toString()).append("\n");
		}
		
		return builder.toString();
	}
}
