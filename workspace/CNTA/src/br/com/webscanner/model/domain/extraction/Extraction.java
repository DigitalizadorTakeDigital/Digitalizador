/**
 * 
 */
package br.com.webscanner.model.domain.extraction;

import java.util.ArrayList;
import java.util.List;

import br.com.webscanner.model.domain.typification.ExtractableField;

/**
 * Classe responsável por armazenar informações referentes ao tipo (Code128, QRCode, Text) e área de extração de informações
 * para um campo do documento.
 * @author Diego
 *
 */
public class Extraction {
	private ExtractionType type;
	private int x;
	private int y;
	private int w;
	private int h;
	private List<ExtractableField> extractableFields;
	
	public Extraction(ExtractionType type, int x, int y, int w, int h){
		this.type = type;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.extractableFields = new ArrayList<ExtractableField>();
	}
	
	public ExtractionType getType() {
		return type;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getW() {
		return w;
	}
	public int getH() {
		return h;
	}
	public List<ExtractableField> getExtractableFields() {
		return extractableFields;
	}
	public boolean addExtractableField(ExtractableField extractableField){
		return this.extractableFields.add(extractableField);
	}
}