package br.com.webscanner.model.domain.converter;

import java.util.ArrayList;

import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.Item;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FieldConverter implements Converter{

	@Override
	public boolean canConvert(Class clazz) {
		return clazz.equals(ArrayList.class);
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext arg2) {
//		writer.setValue(objetc.toString());
		ArrayList<Field> fields = (ArrayList<Field>) object;
		
		for (Field field : fields) {
			writer.startNode("field");
			writer.addAttribute("id", String.valueOf(field.getId()));
			writer.addAttribute("name", field.getName());
			writer.addAttribute("value", field.getValue().toString());
			writer.endNode();
		}
		
		writer.close();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
