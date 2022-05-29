/**
 * 
 */
package br.com.webscanner.model.domain.converter;

import br.com.webscanner.model.domain.image.ImageInfo;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author Diego
 *
 */
public class ImageConverter implements Converter {

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz.equals(ImageInfo.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		ImageInfo image = (ImageInfo) value;
		if(image.getFile() != null){
			writer.addAttribute("imageFile", image.getFile().getName());
		}else{
			writer.addAttribute("imageFile", "");
		}
		writer.close();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
		return null;
	}

}
