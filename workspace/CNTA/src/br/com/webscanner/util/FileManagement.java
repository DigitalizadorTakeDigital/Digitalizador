/**
 * 
 */
package br.com.webscanner.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.FileAlreadyExistsException;
import br.com.webscanner.infra.WebScannerConfig;

/**
 * Classe responsável pelo gerenciamento de arquivos.
 * @author Jonathan Camara
 *
 */
public class FileManagement {
	private static Logger logger = LogManager.getLogger(FileManagement.class.getName());
	public static final String JAR_PATH;
	public static final String TEMP_DIR;

	static{
		File jar = new File(FileManagement.class.getProtectionDomain().getCodeSource().getLocation().getFile());
		JAR_PATH =jar.getParent();
		
		TEMP_DIR = System.getProperty("user.home") + System.getProperty("file.separator") + WebScannerConfig.getProperty("pathImage");
	}
	
	
	public static boolean delete(File file){
		if(file != null && file.exists()){
			logger.info("Deletando o arquivo: {}", file.getName());
			if(file.delete()){
				logger.info("Arquivo deletado com sucesso");
				return true;
			}else{
				logger.warn("Não foi possível deletar o arquivo");
			}
		}else{
			logger.warn("O arquivo é nulo ou não existe.");
		}
		return false;
	}
	
	
	/**
	 * Realiza o download de um arquivo do sistema.
	 * @param file - Caminho do arquivo.
	 */
	public static void downloadSystemFile(String file){
		String tempDir = System.getProperty("java.io.tmpdir");
		logger.info("Iniciando download do arquivo \"{}\" no diretório {}.", file, tempDir);
		
		InputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		
		try {
			String fileName = WebscannerUtil.getFileName(file);
			
//			inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(file);
			inputStream = new URL(file).openStream();
			File fileOutput = new File(tempDir, fileName);
			outputStream = new BufferedOutputStream(new FileOutputStream(fileOutput));

			byte[] buffer = new byte[8192];
			
			int n = -1;
			while ((n = inputStream.read(buffer)) != -1){
				outputStream.write(buffer, 0, n);
			}

			outputStream.flush();
			logger.info("Arquivo {} baixado com sucesso.", fileName);
		} catch (MalformedURLException e) {
			logger.error("URL {} está incorreta.", e.getMessage());
		} catch (FileNotFoundException e) {
			logger.error("O arquivo não foi encontrado: {}.", e.getMessage());
		} catch (IOException e) {
			logger.error("Falha ao baixar arquivo {}", e.getMessage());
		}finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("Falha ao fechar InputStream.");
				}
			}
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error("Falha ao fechar OutputStream.");
				}
			}
		}
	}
	
	/**
	 * Realiza o download de um arquivo, no diretório informado, a partir de sua URL.
	 * @param path - Caminho do arquivo
	 * @param outputPath - Caminho onde o arquivo será salvo
	 */
	public static void downloadSystemFile(String path, String outputPath){
		String fileName = WebscannerUtil.getFileName(path);
		logger.info("Iniciando download do arquivo \"{}\" no diretório {}.", fileName, outputPath);
		InputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		
		try {
//			inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
			inputStream = new URL(path).openStream();
			File fileOutput = new File(outputPath.replace("file:\\", "") + fileName);
			
			outputStream = new BufferedOutputStream(new FileOutputStream(fileOutput));

			byte[] buffer = new byte[8192];
			
			int n = -1;
			while ((n = inputStream.read(buffer)) != -1){
				outputStream.write(buffer, 0, n);
			}

			outputStream.flush();
			logger.info("Arquivo {} baixado com sucesso.", fileName);
		} catch (MalformedURLException e) {
			logger.error("URL {} está incorreta.", e.getMessage());
		} catch (FileNotFoundException e) {
			logger.error("O arquivo não foi encontrado: {}.", e.getMessage());
		} catch (IOException e) {
			logger.error("Falha ao baixar arquivo {}", e.getMessage());
		}finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("Falha ao fechar InputStream.");
				}
			}
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error("Falha ao fechar OutputStream.");
				}
			}
		}
	}
	
	
//	/**
//	 * Realiza o download de um arquivo a partir de seu endereço
//	 * @param url - {@link URL} onde o arquivo se encontra 
//	 */
//	public static void downloadFile(URL url){
//		String tempDir = System.getProperty("java.io.tmpdir");
//		String fileName = WebscannerUtil.getFileName(url);
//		logger.info("Iniciando download do arquivo \"{}\" no diretório {}.", fileName, tempDir);
//		BufferedInputStream inputStream = null;
//		BufferedOutputStream outputStream = null;
//		
//		try {
//			URLConnection conn = url.openConnection();
//			inputStream = new BufferedInputStream(conn.getInputStream());
//			File fileOutput = new File(tempDir, fileName);
//			outputStream = new BufferedOutputStream(new FileOutputStream(fileOutput));
//
//			byte[] buffer = new byte[8192];
//			
//			int n = -1;
//			while ((n = inputStream.read(buffer)) != -1){
//				outputStream.write(buffer, 0, n);
//			}
//
//			outputStream.flush();
//			logger.info("Arquivo {} baixado com sucesso.", fileName);
//		} catch (MalformedURLException e) {
//			logger.error("URL {} está incorreta.", e.getMessage());
//		} catch (FileNotFoundException e) {
//			logger.error("O arquivo não foi encontrado: {}.", e.getMessage());
//		} catch (IOException e) {
//			logger.error("Falha ao baixar arquivo {}", e.getMessage());
//		}finally{
//			if(inputStream != null){
//				try {
//					inputStream.close();
//				} catch (IOException e) {
//					logger.error("Falha ao fechar InputStream.");
//				}
//			}
//			if(outputStream != null){
//				try {
//					outputStream.close();
//				} catch (IOException e) {
//					logger.error("Falha ao fechar OutputStream.");
//				}
//			}
//		}
//	}

	//TODO Diego - Melhorar método.
	public static void createDirectory(String directory){
		logger.info("Criando diretório {} em {} do cliente.", directory, System.getProperty("java.io.tmpdir"));
		File file = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + WebScannerConfig.getProperty("dirTessTraineddata"));		
		if(file.mkdirs()){
			logger.info("Diretório {} criado com sucesso.", directory);
		} else{
			logger.info("Diretório {} já existe.", directory);			
		}
		
	}
	
	public static File copy(File source, File target) throws IOException{
		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(target);
		
		byte[] buf = new byte[1024];
		
		int len;
		
		try {
			while ((len = in.read(buf)) > 0) {
			   out.write(buf, 0, len);
			}
			return target;
		} catch (IOException e) {
		    logger.error("Erro ao copiar o arquivo '{}'", source.getAbsolutePath());
		    throw e;
		}
		finally{
			if (in != null){
				try {
					in.close();
				} catch (IOException e) {
					  logger.error("Erro ao fechar o arquivo '{}'", source.getAbsolutePath());
				}	
			}
			
			if (out != null){
				try {
					out.close();
				} catch (IOException e) {
					  logger.error("Erro ao fechar o arquivo '{}'", target.getAbsolutePath());
				}	
			}
		}
	}
	
	public File copiar(File source, File target) throws IOException{
		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(target);
		
		byte[] buf = new byte[1024];
		
		int len;
		
		try {
			while ((len = in.read(buf)) > 0) {
			   out.write(buf, 0, len);
			}
			return target;
		} catch (IOException e) {
		    logger.error("Erro ao copiar o arquivo '{}'", source.getAbsolutePath());
		    throw e;
		}
		finally{
			if (in != null){
				try {
					in.close();
				} catch (IOException e) {
					  logger.error("Erro ao fechar o arquivo '{}'", source.getAbsolutePath());
				}	
			}
			
			if (out != null){
				try {
					out.close();
				} catch (IOException e) {
					  logger.error("Erro ao fechar o arquivo '{}'", target.getAbsolutePath());
				}	
			}
		}
	}
	
	public static String getFileExtension(File file) {
        if (file == null) {
            return null;
        }

        String name = file.getName();
        int extIndex = name.lastIndexOf(".");

        if (extIndex == -1) {
            return "";
        } else {
            return name.substring(extIndex + 1);
        }
    }

	public static byte[] fileToByteArray (File file) throws IOException {
		logger.info("Iniciando conversão de arquivo para array de bytes");
		
	    InputStream inputStream = null;
	    byte[] buffer = null;
	    try{
	    	inputStream = new FileInputStream(file);
	    	buffer = new byte[inputStream.available()];
	    	inputStream.read(buffer);
	    } finally {
	    	if(inputStream != null){
	    		try {
	    			inputStream.close();		
	    		} catch (IOException e) {
	    			logger.error("Erro ao fechar stream");
	    		}
	    	}
	    }
	    logger.info("Término da conversão");
		return buffer; 
	}
	
	public static File write(String content, File file) throws IOException{
		logger.info("Criando arquivo {} em {}", file.getName(), file.getAbsolutePath());

		file.getParentFile().mkdirs();
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(content.getBytes());		
			fileOutputStream.flush();
			return file;
		} finally{
			if(fileOutputStream != null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar o stream de gravação. {}", e.getMessage());
				}
			}
		}
	}
	
	public static File write(byte[] bytes, String filePathName) throws IOException{
		logger.info("Criando arquivo : {}", filePathName);
		File file = new File(filePathName);
		
		file.getParentFile().mkdirs();
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(bytes);		
			fileOutputStream.flush();
			return file;
		} finally{
			if(fileOutputStream != null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar o stream de gravação");
				}
			}
		}
	}

	public static String readFile(String filePath) throws IOException {
		logger.info("Iniciando leitura do arquivo {}", filePath);
		BufferedReader bufferedReader = null;
		FileInputStream fileInputStream = null;
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			fileInputStream = new FileInputStream(filePath);
			bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
			String line;
			int count = 0;
			
			while((line = bufferedReader.readLine()) != null){
				stringBuilder.append(line);
				stringBuilder.append(System.getProperty("line.separator"));
				count++;
			}
			logger.info("Termino da leitura de arquivo. {} linhas encontradas", count);
		} finally{
			if (bufferedReader !=null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar o stream de leitura. {}", e.getMessage());
				}
			}
			if(fileInputStream != null){
				try{
					fileInputStream.close();
				} catch(IOException e){
					logger.error("Erro ao fechar o stream de leitura. {}", e.getMessage());
				}
			}
		}
		return stringBuilder.toString();
	}
	
	public static boolean createFile(String path, String fileName) throws IOException{
		File file = new File(path, fileName);
		return file.createNewFile();
	}
	
	public static boolean createFile(File file) throws IOException{
		if(file != null){
			return file.createNewFile();
		}
		return false;
	}
	
	public static boolean createFolder(String path) throws NullPointerException, SecurityException{
		return new File(path).mkdirs();
	}
	
	public static void deleteFolder(File file, boolean recursive){
		if(recursive){
			File[] files = file.listFiles();
			if(files != null){
				for(File f : files){
					if(f.isDirectory()){
						deleteFolder(f, true);
					} else {
						f.delete();
					}
				}
			}
		} else {
			file.delete();
		}
	}


	public static boolean rename(File file, String newName) {
		return file.renameTo(new File(file.getParent(), newName));
	}
	
	/**
	 * Escreve o conteúdo em arquivo com um charset específico.
	 * @param content - conteúdo do arquivo
	 * @param file - arquivo a ser escrito
	 * @param charset - charset de escrita
	 * @throws IOException caso não seja possível escrever o arquivo
	 */
	public static void write(String content, File file, Charset charset) throws IOException {
		logger.info("Iniciando escrita do arquivo {} com charset {}", file.getAbsolutePath(), charset.displayName());
		OutputStreamWriter outputWriter = null;
		try {
			outputWriter = new OutputStreamWriter(new FileOutputStream(file), charset);
			outputWriter.write(content);
			outputWriter.flush();
			logger.info("Arquivo escrito com sucesso");
		} finally {
			if (outputWriter != null) {
				try {
					outputWriter.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar stream de gravação: {}", e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Escreve o conteúdo em arquivo com um charset específico.
	 * @param content - conteúdo do arquivo
	 * @param file - arquivo a ser escrito
	 * @param charset - charset de escrita
	 * @throws IOException caso não seja possível escrever o arquivo
	 */
	public static File writeUtf8(String content, File file, Charset charset) throws IOException {
		logger.info("Iniciando escrita do arquivo {} com charset {}", file.getAbsolutePath(), charset.displayName());
		OutputStreamWriter outputWriter = null;
		try {
			outputWriter = new OutputStreamWriter(new FileOutputStream(file), charset);
			outputWriter.write(content);
			outputWriter.flush();
			logger.info("Arquivo escrito com sucesso");
			return file;
		} finally {
			if (outputWriter != null) {
				try {
					outputWriter.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar stream de gravação: {}", e.getMessage());
				}
			}
		}
		
	}
	
	/**
	 * Retorna o mime type do arquivo informado
	 * @param file - arquivo do qual deseja-se obter o mime type
	 * @return mime type do arquivo* 
     * @deprecated 
     * replaced by <code>MimeTypeUtil.get(File file)</code>.
	 */
	@Deprecated
	public static String getMimeType (File file) {
		logger.info("Iniciando obtenção de mime type do arquivo {}", file.getAbsolutePath());
		String mimeType =  URLConnection.guessContentTypeFromName(file.getAbsolutePath());
		logger.info("Mime type obtido: {}", mimeType);
		return mimeType;
	}
	
	public static boolean move (File source, String destination, boolean override) throws IOException, SecurityException, FileAlreadyExistsException {
		if (!source.exists()) {
			throw new FileNotFoundException("O arquivo " + source.getAbsolutePath() + " não existe");
		}

		File dest = new File(destination, source.getName());
		
		if (dest.exists()) {
			if (override) {
				if (!dest.delete()) {
					throw new IOException("Nao foi possivel sobrescrever o arquivo " + dest.getAbsolutePath());
				}
			} else {
				throw new FileAlreadyExistsException("O arquivo " + dest.getAbsolutePath() + " ja existe na pasta de destino");
			}
		}
		
		return source.renameTo(dest);
	}
	
	//Adicionado por Bruno Oliveira em 29-06-2018
	public static void deletarPasta(File pasta){
		if (pasta.exists() && pasta.isDirectory()) {
			File[] files = pasta.listFiles();
			if(files.length > 0){
				for(File f : files){
					if(f.isDirectory()){
						deletarPasta(f);
					} else {
						f.delete();
					}					
				}
				pasta.delete();
			} else {
				pasta.delete();
			}
		}
	}
	
}