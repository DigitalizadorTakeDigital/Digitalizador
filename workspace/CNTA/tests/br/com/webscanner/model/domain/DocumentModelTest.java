package br.com.webscanner.model.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import br.com.webscanner.model.domain.validator.Validator;

public class DocumentModelTest {

	private DocumentModel model;
	private Validator validator;
	
	@Before
	public void setUp() throws Exception {
		validator = new Validator();
	}

	@Test
	public void documentModelShouldNotHaveErrors () {
		model = new DocumentModel(1, "teste", "teste", "", 6, new Group(1, "group"));
		model.validate(validator);
		assertFalse(validator.hasError());
	}

	@Test
	public void documentModelShouldHaveErrorByEmptyName () {
		model = new DocumentModel(1, "", "teste", "", 6, new Group(1, "group"));
		model.validate(validator);
		assertTrue(validator.getMessages().get(0).equalsIgnoreCase("Nome do documento deve ser preenchido"));
	}
	
	@Test
	public void documentModelShouldHaveErrorByNullName () {
		model = new DocumentModel(1, null, "teste", "", 6, new Group(1, "group"));
		model.validate(validator);
		assertTrue(validator.getMessages().get(0).equalsIgnoreCase("Nome do documento deve ser preenchido"));
	}
	
	@Test
	public void documentModelShouldHaveErrorByEmptyDisplayName () {
		model = new DocumentModel(1, "teste", "", "", 6, new Group(1, "group"));
		model.validate(validator);
		assertTrue(validator.getMessages().get(0).equalsIgnoreCase("DisplayName do documento deve ser preenchido"));
	}
	
	@Test
	public void documentModelShouldHaveErrorByNullDisplayName () {
		model = new DocumentModel(1, "teste", null, "", 6, new Group(1, "group"));
		model.validate(validator);
		assertTrue(validator.getMessages().get(0).equalsIgnoreCase("DisplayName do documento deve ser preenchido"));
	}
	
	@Test
	public void documentModelShouldHaveErrorByNullGroup () {
		model = new DocumentModel(1, "teste", "teste", "", 6, null);
		model.validate(validator);
		assertTrue(validator.getMessages().get(0).equalsIgnoreCase("Grupo do documento deve ser preenchido"));
	}
}
