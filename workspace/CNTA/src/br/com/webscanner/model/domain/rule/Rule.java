package br.com.webscanner.model.domain.rule;

import br.com.webscanner.model.domain.Document;

public interface Rule {
	void execute (Document document);
}
