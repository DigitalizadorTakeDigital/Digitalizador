package br.com.webscanner.model.domain;

public class Item implements Comparable<Item>{
	private String value;
	private String text;
	
	public Item(String value, String text) {
		this.value = value;
		this.text = text;
	}

	public String getValue() {
		return value;
	}
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return this.value;
	}

	@Override
	public int compareTo(Item item) {
		return this.text.compareTo(item.getText());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Item) {
			Item i2 = (Item) obj;
			return this.value.equals(i2.getValue()) && this.text.equals(i2.getText());
		}
		return false;
	}
}