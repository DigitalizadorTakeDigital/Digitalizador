package br.com.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.model.domain.export.transp01.TRANSP01Export;

public class Consumer {

	static Properties prop = new Properties();
	static String api = "";
	private static Logger logger = LogManager.getLogger(Consumer.class.getName());

	public static boolean registrarDigitalizacao(String transportadora, int qtdeDigitacoes) {
		
		setApi();
		String token = getAuthToken();
		String transportadoraId = getTransportadoraId(transportadora, token);
			
		return postDigitacoes(transportadoraId, token, qtdeDigitacoes);
		
	}

	private static String getAuthToken() {
		String token = "";
		try {
			
			URL url = new URL(api+"/v1/api/usuarios/login");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			String input = "{\"email\":\"email@10email.com\",\"password\":\"senha123\"}";
			System.out.println(input);
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				token = output.substring(output.indexOf("token")).replace("token\":\"", "").replace("\"}}", "");
		     logger.info("Token Gerado" + token);
			}
			
			conn.disconnect();
		} catch (Exception e) {
			System.out.println("Erro: "+e.toString());
			logger.error("Erro: " + e.toString());
			if(e.toString().contains("Connection refused"))
				System.out.println("Erro de conex�o, provavelmente a API n�o est� rodando");
		}
		return token;
	}
	
	private	static String getTransportadoraId(String transportadora, String token) {
		
		String transportadoraId = "";
		try {
			URL url = new URL(api+"/v1/api/transportadoras/nome/" + transportadora);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			if (conn.getResponseCode() != 200) {
				System.out.print("deu erro... HTTP error code : " + conn.getResponseCode());
				logger.error("Erro: " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output, json = "";
			while ((output = br.readLine()) != null) {
				json += output;
			}
			transportadoraId = json.substring(json.indexOf("_id"), json.indexOf("_id") + 30).replace("_id\":\"", "");
			System.out.println(transportadoraId);
			conn.disconnect();
		} catch (Exception e) {
			System.out.println("Erro: "+e.toString());
			logger.error("Erro: " + e.toString());
		}
		return transportadoraId;
	}

	private static boolean postDigitacoes(String transportadoraId, String token, int i) {
		
		String digitalizacoes = "";

		try {
			URL url = new URL(api+"/v1/api/digitalizacoes/?transportadora="+transportadoraId+"&qtde_digitalizacoes=" + i);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Ecommercy " + token);

			String input = "{\"transportadora\":\"" + transportadoraId + "\",\"qtde_digitalizacoes\":\"" + i + "\"}";
			System.out.println(input);
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			System.out.println("Output from Server .... \n");
			String output = "";
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				digitalizacoes+=output;
			}

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Erro: "+e.toString());
			return false;
		}
		
		if(digitalizacoes.equals(""))
			return false;
		else
			return true;
		
	}

	private static void setApi() {
		
		try {
			prop = Props.getProp();
			api = prop.getProperty("api");
		} catch (IOException e) {
			System.out.println("Erro: " + e.toString());
			System.out.println("Falta arquivo .properties com caminho da API");
		}
		
	}

}