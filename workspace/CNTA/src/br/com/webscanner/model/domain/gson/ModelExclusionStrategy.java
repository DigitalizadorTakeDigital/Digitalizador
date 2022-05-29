package br.com.webscanner.model.domain.gson;

import java.util.ArrayList;
import java.util.List;

import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.domain.FieldModel;
import br.com.webscanner.model.domain.ProductModel;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ModelExclusionStrategy implements ExclusionStrategy {

	private static List<Class<?>> clazzes;
	
	static {
		clazzes = new ArrayList<Class<?>>();
		clazzes.add(FieldModel.class);
		clazzes.add(ProductModel.class);
		clazzes.add(DocumentModel.class);
	}
	
	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return clazzes.contains(clazz);
	}

	@Override
	public boolean shouldSkipField(FieldAttributes arg0) {
		return false;
	}

}
