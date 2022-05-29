package br.com.webscanner.model.domain;

import br.com.webscanner.view.MessagePanel.MessageLevel;

/**
 * Classe representa uma mensagem que será exibida na tela
 * @author Jonathan Camara
 *
 */
public class Message {
	private String text;
	private MessageLevel level;

	public Message(String text, MessageLevel level) {
		this.text = text;
		this.level = level;
	}
	
	public String getText() {
		return text;
	}
	public MessageLevel getLevel() {
		return level;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		Message other = (Message) obj;
		if (level != other.level)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return text;
	}
}