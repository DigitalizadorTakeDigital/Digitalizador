package br.com.webscanner.model.domain.typification.specific;

import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.typification.Recognizable;

public interface SpecificRecognizable extends Recognizable {
	
	boolean recognize(Document document);
}
