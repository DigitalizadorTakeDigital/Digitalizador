package br.com.webscanner.model.domain.scanner.impl.hp;

import javax.xml.bind.annotation.XmlAttribute;

import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement
public class Tag implements Cloneable {
	
	private String value;
	
	public Tag() {}
	
	public Tag(String value) {
		this.value = value;
	}
	
	@XmlAttribute
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Tag tag = new Tag();
		tag.value = this.value;
		return tag;
	}
}
