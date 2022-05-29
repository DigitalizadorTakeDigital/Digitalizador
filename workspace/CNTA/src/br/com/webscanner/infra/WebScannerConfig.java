/**
 * 
 */
package br.com.webscanner.infra;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.ApplicationData.Build;

/**
 * @author Jonathan Camara
 *
 */
public class WebScannerConfig {
	private static Properties properties;
	private static String sourcePath;
	
	private WebScannerConfig(){}
	
	public static String getProperty(String key){
		if(key.startsWith("url")){
			return sourcePath + properties.getProperty(key);
		}
		return properties.getProperty(key);
	}
	
	public static String getSourcePath(){
		return sourcePath;
	}

	public static void setPath(String path) {
		sourcePath = path;
	}
	
	public static void init(){
		try {
			properties = new Properties();
			if(ApplicationData.getBuild() == Build.DESENV){
				properties.load(WebScannerConfig.class.getClassLoader().getResourceAsStream("configuration.properties"));
				File configUser = new File(System.getProperty("user.home"), WebScannerConfig.getProperty("pathImage")+File.separator+"config"+File.separator+"configUser.properties");
				if(configUser.exists()) {
					properties.load(new FileInputStream(configUser));
				}
				File path = new File(WebScannerConfig.class.getProtectionDomain().getCodeSource().getLocation().getFile());
				sourcePath = new File(path.getParentFile().getParentFile().toURI().toString(), "WebContent").getPath();
				sourcePath += "/";
			}else if(ApplicationData.getBuild() == Build.DESKTOP){
				sourcePath = "file://";
				sourcePath += WebScannerConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf('/') + 1);
				properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream("config/configuration.properties"));
//				properties.load(new FileInputStream(new File("C:\\Users\\Jonathan\\workspace\\Trunk\\WebContent\\config", "configuration.properties")));
			} else if(ApplicationData.getBuild() == Build.EMBARCADO){
				
				properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream("configuration.properties"));
				
				File configUser = new File(System.getProperty("user.home"), WebScannerConfig.getProperty("pathImage")+File.separator+"config"+File.separator+"configUser.properties");
				if(configUser.exists()) {
					properties.load(new FileInputStream(configUser));
				}
				
				File path = new File(WebScannerConfig.class.getProtectionDomain().getCodeSource().getLocation().getFile());
			
				sourcePath = new File(path.toURI().toString().replace(WebScannerConfig.getProperty("nomeExecutavel"), "")).getPath();
			
				sourcePath += "/";
				
				
				
			}else{
				properties.load(new URL(sourcePath + "config/configuration.properties").openStream());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getImplementationVersion(){
		Package p = WebScannerConfig.class.getPackage();
		return p.getImplementationVersion();
	}
}
