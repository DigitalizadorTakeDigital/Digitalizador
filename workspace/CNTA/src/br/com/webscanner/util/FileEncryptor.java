package br.com.webscanner.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import br.com.webscanner.exception.FileDecryptException;
import br.com.webscanner.exception.FileEncryptException;

public class FileEncryptor{
//	private static String ALGORITHM = "Blowfish";
//	private static String ALGORITHM = "RSA/ECB/PKCS1Padding";
//	private static String ALGORITHM = "AES/ECB/PKCS5Padding";
	private static String ALGORITHM = "AES/CBC/PKCS5Padding"; //16 bytes
//	private static String ALGORITHM = "AES/CFB8/NoPadding";
//	private static String ALGORITHM = "DESede/ECB/NoPadding";
//	private static String ALGORITHM = "DES/ECB/PKCS5Padding"; //8 bytes
	private static final String DEFAULT_PASSWORD = "36E085C31DB53B51";
	
	/**
	 * Realiza a criptografia de um arquivo utilizando a senha default
	 * @param in - Arquivo de origem
	 * @param out - Arquivo criptografado
	 * @throws FileEncryptException
	 */
	public static void encrypt(File in, File out) throws FileEncryptException {
		encrypt(in, out, DEFAULT_PASSWORD);
	}
	
	/**
	 * Realiza a criptografia de um arquivo
	 * @param in - Arquivo de origem
	 * @param out - Arquivo criptografado
	 * @param password - Senha para criptografar o arquivo
	 * @throws FileEncryptException
	 */
	public static void encrypt(File in, File out, String password) throws FileEncryptException {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		CipherOutputStream cipherOutputStream = null;
		try {
			inputStream = new FileInputStream(in);
			outputStream = new FileOutputStream(out);
			
			byte k[] = password.getBytes();
			SecretKeySpec key = new SecretKeySpec(k, ALGORITHM.split("/")[0]);  
			Cipher cipher =  Cipher.getInstance(ALGORITHM);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(k);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
			cipherOutputStream = new CipherOutputStream(outputStream, cipher);
			
			copy(inputStream, cipherOutputStream);
		} catch (FileNotFoundException e) {
			throw new FileEncryptException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new FileEncryptException(e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new FileEncryptException(e.getMessage());
		} catch (InvalidKeyException e) {
			throw new FileEncryptException(e.getMessage());
		} catch (IOException e) {
			throw new FileEncryptException(e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			throw new FileEncryptException(e.getMessage());
		} finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new FileEncryptException(e.getMessage());
				}
			}
			if(cipherOutputStream != null){
				try {
					cipherOutputStream.close();
				} catch (IOException e) {
					throw new FileEncryptException(e.getMessage());
				}
			}
		
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					throw new FileEncryptException(e.getMessage());
				}
			}
			
		}
	}

	/**
	 * Realiza a descriptografia de um arquivo utilizando a senha default.
	 * @param in - Arquivo criptografado
	 * @param out - Arquivo de destino.
	 * @throws FileDecryptException
	 */
	public static void decrypt(File in, File out) throws FileDecryptException {
		decrypt(in, out, DEFAULT_PASSWORD);
	}
	
	/**
	 * Realiza a descriptografia de um arquivo.
	 * @param in - Arquivo criptografado
	 * @param out - Arquivo de destino.
	 * @param password - Senha para descriptografar o arquivo.
	 * @throws FileDecryptException
	 */
	public static void decrypt(File in, File out, String password) throws FileDecryptException {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		CipherInputStream cipherInputStream = null;
		
		try {
			inputStream = new FileInputStream(in);
			outputStream = new FileOutputStream(out);
			
			byte k[] = password.getBytes();   
			SecretKeySpec key = new SecretKeySpec(k, ALGORITHM.split("/")[0]);  
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(k);
			cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);  
			cipherInputStream = new CipherInputStream(inputStream, cipher);
			
			copy(cipherInputStream, outputStream);
		} catch (FileNotFoundException e) {
			throw new FileDecryptException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new FileDecryptException(e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new FileDecryptException(e.getMessage());
		} catch (InvalidKeyException e) {
			throw new FileDecryptException(e.getMessage());
		} catch (IOException e) {
			throw new FileDecryptException(e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			throw new FileDecryptException(e.getMessage());
		} finally{
			if(cipherInputStream != null){
				try {
					cipherInputStream.close();
				} catch (IOException e) {
					throw new FileDecryptException(e.getMessage());
				}
			}
			
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new FileDecryptException(e.getMessage());
				}
			}
			
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					throw new FileDecryptException(e.getMessage());
				}
			}
		
		}
	}
	
	/**
	 * Realiza a copia de um inputStream para o outputStream;
	 * @param inputStream
	 * @param outputStream
	 * @throws IOException
	 */
	private static void copy(InputStream inputStream, OutputStream outputStream) throws IOException{
		int buffer = 1024;
		
		int read;
		byte[] buf = new byte[buffer];
		while((read = inputStream.read(buf)) != -1){
			outputStream.write(buf,0,read);
		}
		outputStream.flush();
	}
	
	/**
	 * Método auxiliar para geração de senhas criptografadas de acordo com o algoritmo.
	 * @param password
	 * @return
	 */
	private String getKey(String password){
		MessageDigest algorithm;
		try {
			algorithm = MessageDigest.getInstance("SHA-1");
			byte messageDigest[] = algorithm.digest(password.getBytes("UTF-8"));

			StringBuilder hexString = new StringBuilder();
			for (byte b : messageDigest) {
				hexString.append(String.format("%02X", 0xFF & b));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
}
