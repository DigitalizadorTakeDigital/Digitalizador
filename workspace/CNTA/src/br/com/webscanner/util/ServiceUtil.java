package br.com.webscanner.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe responsavel pela iteração com serviços do windows.
 * @author Jonathan
 *
 */
public class ServiceUtil {
	private static Logger logger = LogManager.getLogger(ServiceUtil.class.getName());
	
	enum ServiceStatus {
		STOPPED, STARTED;
	}
	
	private ServiceUtil(){}
	
	/**
	 * Inicia um serviço do windows.
	 * @param serviceName - Nome do serviço
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void startService(String serviceName) throws IOException, InterruptedException{
		logger.info("Iniciando servico: {}", serviceName);
		if(!isServiceRunning(serviceName)){
			Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe",  "/c", "sc",  "start", serviceName});
			process.waitFor();
			
			if(process.exitValue() != 0){
				logger.warn(getEcho(process.getInputStream()));
				logger.warn(getEcho(process.getErrorStream()));
			} else{
				Thread.sleep(5000);
			}
		}
	}
	
	public static void startService(boolean wait, String serviceName) throws IOException, InterruptedException{
		logger.info("Iniciando servico: {}", serviceName);
		if(!isServiceRunning(serviceName)){
			Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe",  "/c", "sc",  "start", serviceName});
			if (wait) {
				process.waitFor();

				if(process.exitValue() != 0){
					logger.warn(getEcho(process.getInputStream()));
					logger.warn(getEcho(process.getErrorStream()));
				} else{
					Thread.sleep(5000);
				}
			}
		}
	}
	
	/**
	 * Para um serviço do windows. Verificando antes se o mesmo está rodando.
	 * @param serviceName - Nome do serviço
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void stopService(String serviceName) throws IOException, InterruptedException{
		logger.info("Parando serviço: {}", serviceName);
		if(isServiceRunning(serviceName)){
			Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe",  "/c", "net", "/Y",  "stop", serviceName});
			process.waitFor();
			
			if(process.exitValue() != 0){
				logger.error(getEcho(process.getInputStream()));
				logger.error(getEcho(process.getErrorStream()));
			} else{
				Thread.sleep(10000);
			}
		}
	}
	
	/**
	 * Para um serviço do windows. Não verifica se o serviço está em execução.
	 * @param wait - Aguarda ou não a execução do processo
	 * @param serviceName - Nome do serviço
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void stopService(boolean wait, String serviceName) throws IOException, InterruptedException{
		logger.info("Parando serviço: {}", serviceName);
		Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe",  "/c", "net", "/Y",  "stop", serviceName});
		
		if(wait){
			process.waitFor();
			
			if(process.exitValue() != 0){
				logger.error(getEcho(process.getInputStream()));
				logger.error(getEcho(process.getErrorStream()));
			} else{
				Thread.sleep(10000);
			}
		}
	}
	
	
	/**
	 * Retorna os detalhes de um servico
	 * @param serviceName
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String getServiceProperty(String serviceName, String property) throws IOException, InterruptedException{
		logger.info("Recuperando propriedade {} do servico {}", property, serviceName);
		
		Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe",  "/c", "wmic", "service", serviceName, "get", property});
		process.waitFor();
		
		if(process.exitValue() == 0){
			StringWriter sw = null;
			InputStream is = null;
			try{
				sw = new StringWriter();
				is = process.getInputStream();
				
				int b = -1;
				while((b = is.read()) != -1){
					sw.write(b);
				}
				
				String propertyValue = sw.toString();
				propertyValue = propertyValue.replace(property, "").trim();
				
				return propertyValue;
			} finally{
				if(sw !=  null){
					sw.close();
				}
				
				if(is != null){
					is.close();
				}
			}
		}
		return null;
	}
	
	/**
	 * ATHIC_ScannerServiceHost
	 * Verifica se um servico esta em execucao 
	 * @param serviceName
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static boolean isServiceRunning(String serviceName) throws IOException, InterruptedException{
		logger.info("Verificando se o servico {} esta rodando.", serviceName);
		Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe",  "/c", "sc",  "query", serviceName, "|", "find", "/C", "\"RUNNING\""});
		process.waitFor();
		
		if(process.exitValue() == 0){
			
			StringWriter sw = null;
			InputStream is = null;
			try{
				sw = new StringWriter();
				is = process.getInputStream();
				
				int b = -1;
				while((b = is.read()) != -1){
					sw.write(b);
				}
				
				if(sw.toString().trim().equals(String.valueOf(ServiceStatus.STARTED.ordinal()))){
					return true;
				}
			} finally{
				if(sw !=  null){
					sw.close();
				}
				
				if(is != null){
					is.close();
				}
			}
		}
		
		return false;
	}
	
	private static String getEcho(InputStream stream){
		try {
			byte [] b = new byte[stream.available()];
			stream.read(b);
			
			return new String(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void startDriver() {
		logger.debug("Iniciando driver do scanner CAPI");
		
//		Verificar se o processo está rodando
//		Process process = Runtime.getRuntime().exec("cmd.exe tasklist /fi \"imagename eq SmartSourceManager.exe\" /fi \"status eq running\"");
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Process process = Runtime.getRuntime().exec("cmd.exe /C C:\\SmartSource\\bin\\SmartSourceManager.exe /s");
					Thread.sleep(2000);
					InputStream errorStream = process.getErrorStream();
					InputStream infoStream = process.getInputStream();
					
					if (errorStream.available() != 0) {
						logger.debug(getLog(errorStream));
					} else if (infoStream.available() != 0) {
						logger.debug(getLog(infoStream));
					}
					Thread.sleep(200);
				} catch (IOException e) {
					logger.error("Erro ", e);
				} catch (InterruptedException e) {
					logger.error("Erro ", e);
				}
			}
		});
		try {
			thread.setPriority(10);
			thread.start();
			thread.join();
		} catch (InterruptedException e) {
			logger.error("Erro ", e);
		}
	}
	
	public static void stopDriver() {
		logger.debug("Fechando driver do scanner CAPI");
		try {
			Process process = Runtime.getRuntime().exec("cmd.exe /C taskkill /im smartsourceManager.exe /f");
			process.waitFor();
			if (process.getInputStream().available() != 0) {
				logger.debug(getLog(process.getInputStream()));
			} else if (process.getErrorStream().available() != 0) {
				logger.debug(getLog(process.getErrorStream()));
			}
		} catch (IOException e) {
			logger.error("Erro ", e);
		} catch (InterruptedException e) {
			logger.error("Erro ", e);
		}
	}
	
	public static String getLog(InputStream is) throws IOException{
		StringWriter sw = null;
		try {
			sw = new StringWriter();
			int i = -1;
			while((i = is.read()) != -1) {
				sw.write(i);
				
				if (is.available() != 0) {
					continue;
				}
				break;
			}
			sw.flush();
			return sw.toString().trim();
		} finally {
			is.close();
		}
	}
}