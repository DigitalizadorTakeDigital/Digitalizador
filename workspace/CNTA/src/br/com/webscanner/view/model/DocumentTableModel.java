package br.com.webscanner.view.model;

import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.view.Reorderable;

/**
 * Modelo da tabela que conterá os dados das imagens capturadas.
 * 
 * @author Jonathan Camara
 *
 */
public class DocumentTableModel extends AbstractTableModel implements Reorderable {
	private static final long serialVersionUID = 1L;
	private String[] columns;
	private List<Document> documents;

	public DocumentTableModel(String[] columns, Product product) {
		this.columns = columns;
		this.documents = product.getDocuments();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public int getRowCount() {
		return documents.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (documents.size() > 0) {
			return documents.get(row);
		}
		return null;
	}

	public void addRow(Document document) {
		this.documents.add(document);
	}

	public void removeRow(Document document) {
		for(int i = (int)document.getId(); i < documents.size(); i++) {
			documents.get(i).setId(i);
		}
		this.documents.remove(document);
	}

	@Override
	public void reorder(int from, int to) {
		if (from != to) {
			Document document = documents.remove(from);
			documents.add(to, document);
			for(int i = 0; i < documents.size(); i++) {
				documents.get(i).setId(i+1);
			}
		}
	}

	@Override
	public String toString() {
		return "DocumentTableModel [columns=" + Arrays.toString(columns) + ", documents=" + documents + "]";
	}
}