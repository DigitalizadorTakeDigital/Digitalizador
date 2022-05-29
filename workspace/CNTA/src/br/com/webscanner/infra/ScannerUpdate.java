package br.com.webscanner.infra;

import java.io.PrintStream;
import java.net.Socket;

import br.com.webscanner.exception.ScannerUpdateException;
import br.com.webscanner.model.domain.ApplicationStatus;

public class ScannerUpdate {

	private static Server server;
	
	private ScannerUpdate () {}
	
	public static Server conectar(String ip, int porta) throws ScannerUpdateException {
		if (server == null) {
			try {
				Socket client = new Socket(ip, porta);
				PrintStream saida = new PrintStream(client.getOutputStream());
				server = new Server(saida);
			} catch (Exception e) {
				throw new ScannerUpdateException(e);
			}
		}
		return server;
	}
	
	public static class Server {
		private PrintStream stream;

		private Server (PrintStream stream) {
			this.stream = stream;
		}
		
		public void respoder(ApplicationStatus status) throws ScannerUpdateException {
			try {
				stream.println(status.getStatus());
			} catch (Exception e) {
				throw new ScannerUpdateException(e);
			} finally {
				server = null;
			}
		}
	}
}