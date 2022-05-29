/**
 * 
 */
package br.com.webscanner.model.domain;

import static br.com.webscanner.util.WebscannerUtil.isNull;
import static br.com.webscanner.util.WebscannerUtil.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;

import br.com.webscanner.model.domain.extraction.Extraction;
import br.com.webscanner.model.domain.rule.Rule;
import br.com.webscanner.model.domain.validator.Validator;

/**
 * Representa o modelo de documento do produto.
 * @author Jonathan Camara
 *
 */
public class DocumentModel {
	private long id;
	private String name;
	private String displayName;
	private String tableDisplay;
	private int icor;
	private List<FieldModel> fields;	
	private List<ValidationFunction> validationFunctions;
	private List<Extraction> extractions;
	private Group group;
	private Rule rule;
	private boolean obrigatorio;
	private boolean multiPage;
	
	public DocumentModel(long id, String name, String displayName, String tableDisplay, int icor, Group group){
		this.id = id;
		this.name = name;
		this.displayName = displayName;
		this.tableDisplay = tableDisplay;
		this.icor = icor;
		this.fields = new ArrayList<FieldModel>();
		this.validationFunctions = new ArrayList<ValidationFunction>();
		this.extractions = new ArrayList<Extraction>();
		this.group = group;
	}
	
	public void validate(Validator validator) {
		validator.that(isNullOrEmpty(name), "Nome do documento deve ser preenchido");
		validator.that(isNullOrEmpty(displayName), "DisplayName do documento deve ser preenchido");
		validator.that(isNull(group), "Grupo do documento deve ser preenchido");
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id){
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name){
		this.name =  name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName){
		this.displayName =  displayName;
	}
	public String getTableDisplay() {
		return tableDisplay;
	}
	public int getIcor() {
		return icor;
	}
	public List<FieldModel> getFields() {
		return fields;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public boolean addField(FieldModel fieldModel){
		return this.fields.add(fieldModel);
	}
	public List<ValidationFunction> getValidationFunctions(){
		return this.validationFunctions;
	}
	public boolean addValidationFunction(ValidationFunction function){
		return this.validationFunctions.add(function);
	}
	public List<Extraction> getExtractions() {
		return extractions;
	}
	public boolean addExtraction(Extraction extraction){
		return this.extractions.add(extraction);
	}
	public Rule getRule() {
		return rule;
	}
	public void setRule(Rule rule) {
		this.rule = rule;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		DocumentModel other = (DocumentModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.displayName;
	}

	public boolean isObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
	}

	public boolean isMultiPage() {
		return multiPage;
	}

	public void setMultiPage(boolean multiPage) {
		this.multiPage = multiPage;
	}
	
}