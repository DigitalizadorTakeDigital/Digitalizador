package br.com.dao;

public class StartService  {
	
	public static boolean RegistrarQtd(String nomeTransportadora, int QtdDocs) {

		boolean sucesso = Consumer.registrarDigitalizacao(nomeTransportadora, QtdDocs);
		
		return sucesso;
			
	}

}