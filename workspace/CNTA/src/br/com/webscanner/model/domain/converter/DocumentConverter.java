package br.com.webscanner.model.domain.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.export.xml.DocumentXmlExportModel;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DocumentConverter implements Converter{
	private static Logger logger = LogManager.getLogger(DocumentConverter.class.getName());
	private List<DocumentXmlExportModel> models;
	
	public DocumentConverter(List<DocumentXmlExportModel> models) {
		this.models = models;
	}

	@Override
	public boolean canConvert(Class clazz) {
		return clazz.equals(ArrayList.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		List<Document> documents = (ArrayList<Document>) value;
		
		if(!models.isEmpty()){
			for(Document document : documents){
				if(models.contains(new DocumentXmlExportModel(document.getDocumentModel().getId()))){
					writer.startNode("document");
					context.convertAnother(document);
					writer.endNode();
				}
			}
		} else{
			for(Document document : documents){
				writer.startNode("document");
				context.convertAnother(document);
				writer.endNode();
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
