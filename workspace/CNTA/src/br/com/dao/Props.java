package br.com.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Props {
	
	public static Properties getProp() throws IOException {
		Properties props = new Properties();
		String path = System.getProperty("user.dir") + "\\config.properties";
		FileInputStream file = new FileInputStream(
				"C:\\BGCC-ClientCaptura\\dao\\config.properties");
		props.load(file);
		return props;
	}
	
}
