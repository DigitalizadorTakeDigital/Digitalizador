/**
 * 
 */
package br.com.webscanner.util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.text.MaskFormatter;

import br.com.webscanner.exception.Sha1Exception;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.Document;

/**
 * 
 * @author Diego
 *
 */
public class WebscannerUtil {

	/**
	 * Faz a conversão de uma data do formato A para o formato B.
	 * 
	 * @param date
	 *            {@link String} data a ser convertida.
	 * @param inputFormat
	 *            {@link String} formato da data de entrada.
	 * @param outputFormat
	 *            {@link String} formato da data de saída.
	 * @return {@link String} data formatada.
	 * @throws ParseException
	 */
	public static String formatDate(String date, String inputFormat,
			String outputFormat) throws ParseException {
		Date datetime = new SimpleDateFormat(inputFormat).parse(date);
		DateFormat dateFormat = new SimpleDateFormat(outputFormat);
		return dateFormat.format(datetime);
	}

	public static String padRight(String s, int n, char newChar) {
		return String.format("%1$-" + n + "s", s).replace(' ', newChar);
	}

	public static String padLeft(String s, int n, char newChar) {
		return String.format("%1$" + n + "s", s).replace(' ', newChar);
	}

	public static String padLeft(long s, int n, char newChar) {
		return padLeft(String.valueOf(s), n, newChar);
	}

	/**
	 * Recupera o nome de um arquivo a partir de uma caminho completo do
	 * arquivo.
	 * 
	 * @param path
	 *            - Caminho completo do arquivo.
	 * @return nome do arquivo com a extensão
	 */
	public static String getFileName(String path) {
		File file = new File(path);
		return file.getName();
	}

	/**
	 * Recupera o nome de um arquivo a partir de uma {@link URL}.
	 * 
	 * @param url
	 *            - URL do arquivo
	 * @return nome do arquivo com a extensão
	 */
	public static String getFileName(URL url) {
		File file = new File(url.getFile());
		return file.getName();
	}

	/**
	 * Remove caracteres '0' localizados à esquerda em uma String. <br>
	 * Se a String for composta apenas por '0', mantém apenas um '0'.
	 * 
	 * @param value
	 *            - String a ser alterada
	 * @return valor sem '0' à esqueda
	 */
	public static String removeLeadingZeros(String value) {
		return value.replaceFirst("^0+(?!$)", "");
	}

	/**
	 * Verifica se a String é nula ou vazia
	 * 
	 * @param value
	 * @return true se é nula ou vazia<br>
	 */
	public static boolean isNullOrEmpty(String value) {
		return (value != null && !value.trim().isEmpty()) ? false : true;
	}

	/**
	 * Verifica se o objecto é nulo
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isNull(Object object) {
		return object == null;
	}

	public static String maskIt(String mask, String text) throws ParseException {
		MaskFormatter formatter = new MaskFormatter(mask);
		formatter.setValueContainsLiteralCharacters(false);
		return formatter.valueToString(text);
	}

	public static String generateSHA1(byte[] bytes)
			throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte[] hashBytes = digest.digest(bytes);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hashBytes.length; i++) {
			sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		return sb.toString();
	}

	public static String generateSHA1(File file) throws Sha1Exception {
		MessageDigest digest;
		InputStream fis = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
			fis = new FileInputStream(file);
			int n = 0;
			byte[] buffer = new byte[8192];
			while (n != -1) {
				n = fis.read(buffer);
				if (n > 0) {
					digest.update(buffer, 0, n);
				}
			}

			byte[] hashBytes = digest.digest();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < hashBytes.length; i++) {
				sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}

			return sb.toString();
		} catch (FileNotFoundException e) {
			throw new Sha1Exception(e);
		} catch (IOException e) {
			throw new Sha1Exception(e);
		} catch (NoSuchAlgorithmException e) {
			throw new Sha1Exception(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw new Sha1Exception(e);
				}
			}
		}
	}

	public static String getParametroOpcional(String nomeParametro) {

		return WebscannerUtil.isNullOrEmpty(ApplicationData
				.getParam(nomeParametro)) ? "" : ApplicationData
				.getParam(nomeParametro);
	}

	public static String getParametroDataOpcionalFormatada(String parametro)
			throws ParseException {

		String data = ApplicationData.getParam(parametro);

		if (WebscannerUtil.isNullOrEmpty(data))
			return "";

		SimpleDateFormat formatadorAssinatura = new SimpleDateFormat("ddMMyyyy");

		Date dataAssinatura = formatadorAssinatura.parse(data);

		formatadorAssinatura.applyPattern("yyyyMMdd 030000");
		String dataAssinaturaFormatada = formatadorAssinatura.format(
				dataAssinatura).trim();

		return dataAssinaturaFormatada;
	}

	public static void abrirUrl(String url) throws IOException {
		Desktop desktop = Desktop.getDesktop();

		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		desktop.browse(uri);
	}
	
	public static List<Document> removerPaginasEmBranco (List<Document> documents, String paginaEmBranco) {
        List<Document> novaLista = new ArrayList<Document>(documents.size());
        
        for (Document document : documents) {
              if (!document.getDocumentModel().getName().equalsIgnoreCase(paginaEmBranco)) {
                    novaLista.add(document);
              }
        }
        
        return novaLista;
	}
	
}