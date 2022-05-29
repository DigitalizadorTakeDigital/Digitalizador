/**
 * 
 */
package br.com.webscanner.model.domain.typification;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que contém as coordenadas para extração da informação que será utilizada para 
 * identifcar um documento a partir de seu modelo de reconhecimento {@link DocumentRecognitionModel} 
 * @author Jonathan Camara
 */
public class Position {
	private int x;
	private int y;
	private int width;
	private int height;
	private List<DocumentRecognizable> documentRecognizables;
	
	public Position(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.documentRecognizables = new ArrayList<DocumentRecognizable>();
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public List<DocumentRecognizable> getDocumentRecognizables() {
		return documentRecognizables;
	}

	public boolean addDocumentRecognizable(DocumentRecognizable documentRecognizables){
		return this.getDocumentRecognizables().add(documentRecognizables);
	}

}