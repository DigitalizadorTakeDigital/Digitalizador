package br.com.webscanner.util;

import java.io.File;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MimeTypeUtil {

	private static final String FILE_EXTENSION = "[^\\s*^\\.]+$";
	private static Properties mimes;

	static {
		mimes = new Properties();
		mimes.put("doc", "application/msword");
		mimes.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		mimes.put("xls", "application/vnd.ms-excel");
		mimes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		mimes.put("txt", "text/plain");
		mimes.put("msg", "application/x-filenet-filetype-msg");
		mimes.put("jpg", "image/jpeg");
		mimes.put("jpeg", "image/jpeg");
		mimes.put("tif", "image/tiff");
		mimes.put("tiff", "image/tiff");
		mimes.put("png", "image/png");
		mimes.put("pdf", "application/pdf");
		mimes.put("xml", "text/xml");
		mimes.put("zip", "application/x-zip-compressed");
	}
	
	public static String get(File file){
		return get(file.getName());
	}
	
	public static String get(String fileName){
		String regex = FILE_EXTENSION;
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(fileName.toLowerCase());
		if(match.find()){
			return mimes.getProperty(match.group());
		}		
 		return null;
	}
	
	public static String getByExtension(String ext){
		return mimes.getProperty(ext.toLowerCase());
	}
}