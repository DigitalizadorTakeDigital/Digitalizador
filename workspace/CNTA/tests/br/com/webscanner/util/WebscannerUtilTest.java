package br.com.webscanner.util;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

public class WebscannerUtilTest {

	@Test
	public void fileNameShouldBeValid() {
		String path = "config/arquivo.txt";
		String fileName = "arquivo.txt";
		
		assertEquals(WebscannerUtil.getFileName(path), fileName);
	}
	
	@Test
	public void fileNameURLShouldBeValid() throws MalformedURLException, URISyntaxException{
		URL url = new URL("http://destroyer/config.a/reader.ini");
		String fileName = "reader.ini";
		
		assertEquals(WebscannerUtil.getFileName(url), fileName);
	}
}