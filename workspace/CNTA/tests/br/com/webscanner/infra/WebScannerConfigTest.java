package br.com.webscanner.infra;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WebScannerConfigTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void getPropertyShouldReturnCorrectValue() {
		String key = "xml";
		String value = WebScannerConfig.getProperty(key);
		String expected = "products.xml";
		
		
		assertEquals(expected, value);
	}
}