/**
 * 
 */
package br.com.webscanner.model.domain.typification;

import br.com.webscanner.model.domain.extraction.ExtractionType;

/**
 * Apresenta os identificadores possíveis para um documento.
 * @author Jonathan Camara
 */
public class Recognition {
	private ExtractionType type;
	private Recognizable recognizable;

	public Recognition(ExtractionType type, Recognizable recognizable) {
		this.type = type;
		this.recognizable = recognizable;
	}
	
	public ExtractionType getType() {
		return type;
	}
	
	public Recognizable getRecognizable(){
		return this.recognizable;
	}
}