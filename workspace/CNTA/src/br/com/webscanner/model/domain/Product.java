package br.com.webscanner.model.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.webscanner.model.domain.export.xml.DocumentXmlExportModel;
import br.com.webscanner.model.domain.image.ImageScanned;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("product")
public class Product {
	@XStreamAsAttribute
	private String id;
	@XStreamAsAttribute
	private String name;
	private List<Document> documents;
	@XStreamOmitField
	private ProductModel model;
	

	/**
	 * @param id
	 * @param name
	 * @param documentModels
	 * @param exports
	 */
	public Product(String id, String name){
		this.id = id;
		this.name = name;
		this.documents = new ArrayList<Document>();
	}

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public List<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public boolean addDocument(Document document){
		return this.documents.add(document);
	}
	public ProductModel getModel() {
		return model;
	}
	public void setModel(ProductModel productModel) {
		this.model = productModel;
	}
	
	public ArrayList<File> getAllImagesFilesScanned(){
		ArrayList<File> imageFiles = new ArrayList<File>();
		for(Document document : documents){
			imageFiles.addAll(document.getContent().getContents());
		}
		return imageFiles;
	}
	
	public ArrayList<File> getOnlyJpgFilesScanned(){
		ArrayList<File> imageFiles = new ArrayList<File>();
		for(Document document : documents){
			ImageScanned jpgImage = (ImageScanned) document.getContent();
			imageFiles.add(jpgImage.getJpg().getFront().getFile());
			if(jpgImage.getJpg().getRear() != null){
				imageFiles.add(jpgImage.getJpg().getRear().getFile());
			}
		}
		return imageFiles;
	}
	
	public ArrayList<File> getImagesByModels(List<DocumentXmlExportModel> models){
		ArrayList<File> imageFiles = new ArrayList<File>();
		for(Document document : documents){
			if(models.contains(new DocumentXmlExportModel(document.getDocumentModel().getId()))){
				imageFiles.addAll(document.getContent().getContents());
			}
		}
		return imageFiles;
	}

	
	/**
	 * Percorre a lista de documentos, atribuindo uma valor sequencial ao atributo captureSequence dos mesmos.
	 */
	public void sequentializeDocuments(){
		long sequence = 1;
		for(Document document : documents){
			document.setCaptureSequence(sequence++);
		}
	}
	
	/**
	 * Retorna um {@link ArrayList} contendo todos os arquivos de imagem TIFF frontais do produto digitalizado.
	 * @return {@link ArrayList} de {@link File}
	 */
	public ArrayList<File> getOnlyImagesTiffFront(){
		ArrayList<File> imageFiles = new ArrayList<File>();
		for(Document document : documents){
			File imageFile = document.getContent().getMainFile();
			if(imageFile != null){
				imageFiles.add(imageFile);
			}
		}
		return imageFiles;
	}
	
	public boolean isValid(){
		if(model.getValidatable() != null){
			return model.getValidatable().validate(this);
		}
		return true;
	}
}