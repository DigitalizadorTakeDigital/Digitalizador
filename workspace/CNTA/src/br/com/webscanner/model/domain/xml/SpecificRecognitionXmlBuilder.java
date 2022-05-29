package br.com.webscanner.model.domain.xml;

import org.w3c.dom.Element;

import br.com.webscanner.exception.SpecificRecognitionXmlBuilderException;
import br.com.webscanner.model.domain.typification.Recognizable;

public interface SpecificRecognitionXmlBuilder {
	/**
	 * Lê um elemento do xml e converte em um reconhecimento específico.
	 * @param element - Nó do xml 
	 * @return
	 */
	Recognizable build(Element element) throws SpecificRecognitionXmlBuilderException;
}
