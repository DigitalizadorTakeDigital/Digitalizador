package br.com.webscanner.model.domain.scanner;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.libs.Kernel32;
import br.com.webscanner.model.domain.scanner.impl.twain.Twain;
import br.com.webscanner.util.WebscannerUtil;

import com.sun.jna.Native;

public class BridgeManager {
	private static Logger logger = LogManager.getLogger(BridgeManager.class.getName());
	
	private static Twain twain;
	private static Kernel32 kernel32;
	private static boolean LOADED = false;
	
	private BridgeManager(){}
	
	static{
		logger.info("Adicionando o diretorio temp no java.library.path");
		String path = System.getProperty("java.library.path");
		path += ";" + System.getProperty("java.io.tmpdir");
		System.setProperty("java.library.path", path);

		Field fieldSysPath;
		try {
			fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (SecurityException e) {
			logger.error(e.getMessage());
		} catch (NoSuchFieldException e) {
			logger.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		}
	}
	
	public static boolean load() {
		if (!LOADED) {
			try {
				String dllTwain = WebscannerUtil.getFileName(WebScannerConfig.getProperty("urlTwain"));
				logger.info("Carregando dll {}{}", System.getProperty("java.io.tmpdir"), dllTwain);
				
				twain = (Twain) Native.loadLibrary(System.getProperty("java.io.tmpdir") + dllTwain, Twain.class);
				
				if(twain == null){
					logger.error("Erro ao carregar a dll {}{}.", System.getProperty("java.io.tmpdir"), dllTwain);
				}
				
				logger.info("Carregando dll kernel32");
				kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
				
				if(kernel32 == null){
					logger.error("Erro ao carregar a dll kernel32");
				}
				
				LOADED = true;
			} catch (Error e) {
				logger.fatal("Não foi possível carregar as DLL's {}", e.getMessage());
			}
		}
		return LOADED;
	}
	
	public static Kernel32 getKernel32() {
		load();
		return kernel32;
	}
	
	public static Twain getTwain() {
		load();
		return twain;
	}
}
