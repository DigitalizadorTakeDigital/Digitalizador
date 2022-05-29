package br.com.webscanner.util;

import java.math.BigInteger;
import java.text.Normalizer;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import br.com.webscanner.exception.EncryptorException;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class StringEncryptor {
	private static final String KEY = "BRADPSDSBRADPSDS"; 
	private static final String INIT_VECTOR = "RandomInitVector";
	
	@Deprecated
	public StringEncryptor() {
	}
	
	public static String encrypt(String value) {
		return encrypt(KEY, INIT_VECTOR, value);
	}
	
	public static String encrypt(String key, String initVector, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());

			return new BigInteger(Base64.encode(encrypted).getBytes()).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	public static String decrypt(String value) throws EncryptorException {
		return decrypt(KEY, INIT_VECTOR, value );
	}
	
	public static String decrypt(String key, String initVector, String value) throws EncryptorException {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(Base64.decode(new String(new BigInteger(value).toByteArray())));

			return new String(original);
		} catch (Exception e) {
			throw new EncryptorException("Erro ao descriptografar a senha");
		}
    }
	
	public static String removerAcentos(String str) {
		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
}