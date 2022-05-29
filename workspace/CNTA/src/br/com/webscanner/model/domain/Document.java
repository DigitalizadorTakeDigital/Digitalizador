package br.com.webscanner.model.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.converter.FieldConverter;
import br.com.webscanner.model.domain.validator.Validator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Representa um documento digitalizado.
 * @author Jonathan Camara
 *
 */
@XStreamAlias("document")
public class Document {
	@XStreamAsAttribute
	private long id;
	@XStreamAsAttribute
	private long captureSequence;
	@XStreamAsAttribute
	private String name;
	@XStreamConverter(FieldConverter.class)
	private List<Field> fields;
	@XStreamAlias(value="imageScanned")
	private Content content;
	@XStreamAsAttribute
	private String dateCapture;
	@XStreamAsAttribute
	private int documentType = 20;
	@XStreamOmitField
	private DocumentModel documentModel;
	private boolean erroIqf;
	
	public Document(long id, long captureSequence) {
		this.id = id;
		this.captureSequence = captureSequence;
		this.fields = new ArrayList<Field>();
	}
	
	/**
	 * Verifica se o documento está tipificado corretamente e se todos os fields possuem preenchimentos válidos.
	 * @return
	 */
	public void validate(Validator validator){
		boolean valid = true;
		if(this.name != null && !this.name.isEmpty()){
			for(Field field : fields) {
				if (field.getFieldModel().isHidden()) {
					continue;
				}
				if(!validator.that(!field.isValid(), Bundle.getString("field.invalid", field.getName()))) {
					valid = false;
				}
			}
			
			if(valid){
				for(ValidationFunction function : this.documentModel.getValidationFunctions()){
					validator.that(!ScriptingValidation.validate(this, 	function.getFunction()), function.getMessage());
				}
			}
		}else{
			validator.that(true, Bundle.getString("document.notTyped", this.getId()));
		}
	}

	/**
	 * Busca um Field atráves do seu id. Retorna o Field se o encontrar e null caso contrário.
	 * @param id {@link int} id do Field a ser retornado.
	 * @return {@link Field}
	 */
	public Field getFieldById(int id){
		Collections.sort(fields);
		int index = Collections.binarySearch(fields, new Field(id));
		if(index == -1){
			return null;
		}
		return fields.get(index);
	}

	/**
	 * Busca um Field atráves do seu nome. Retorna o Field se o encontrar e null caso contrário.
	 * @param name
	 * @return {@link Field}
	 */
	public Field getFieldByName(String name){
		Comparator<Field> comparator = new Comparator<Field>() {
			@Override
			public int compare(Field o1, Field o2) {
				return o1.getFieldModel().getName().compareTo(o2.getFieldModel().getName());
			}
		};
		
		Collections.sort(fields, comparator);
		
		int index = Collections.binarySearch(fields, new Field(new FieldModel(name)), comparator);
		if(index < 0){
			return null;
		}
		return fields.get(index);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Field> getFields() {
		return fields;
	}
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public String getDateCapture() {
		return dateCapture;
	}
	
	public void setDateCapture(String dateCapture) {
		this.dateCapture = dateCapture;
	}
	public int getDocumentType() {
		return documentType;
	}
	
	public void setDocumentType(int documentType) {
		this.documentType = documentType;
	}
	
	public void setDocumentModel(DocumentModel documentModel){
		this.setName(documentModel.getDisplayName());
		
		for(FieldModel fieldModel : documentModel.getFields()){
			Field field = new Field(fieldModel);
			
			String value = ApplicationData.getParam(fieldModel.getName());
			if (value != null) {
				field.setValue(value);
				field.setValid(true);
			}
			this.addField(field);
		}
		this.documentModel  = documentModel;
	}
	
	public DocumentModel getDocumentModel(){
		return this.documentModel;
	}
	
	public long getCaptureSequence() {
		return captureSequence;
	}
	
	public void setCaptureSequence(long captureSequence) {
		this.captureSequence = captureSequence;
	}

	public boolean addField(Field field){
		return this.fields.add(field);
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public void setErroIqf(boolean erroIqf) {
		this.erroIqf = erroIqf;
	}
	public boolean temErroIqf() {
		return erroIqf;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
}