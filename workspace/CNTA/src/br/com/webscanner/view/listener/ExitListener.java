package br.com.webscanner.view.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.gcc.api.capi.library.CapiScanner;
import br.com.gcc.api.scanner.exception.ScannerException;
import br.com.webscanner.exception.ScannerUpdateException;
import br.com.webscanner.infra.ScannerUpdate;
import br.com.webscanner.infra.ScannerUpdate.Server;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.ApplicationStatus;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.util.FileManagement;

public class ExitListener implements ActionListener {
	
	private static final Logger LOGGER = LogManager.getLogger(ExitListener.class);
	
	private Component parente;
	private Product produto;

	public ExitListener(Component parente, Product produto) {
		this.parente = parente;
		this.produto = produto;
	}

	@Override
	public void actionPerformed(ActionEvent evento) {
		if(JOptionPane.showConfirmDialog(parente, Bundle.getString("dialog.exit"), "Sair", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
			LOGGER.info("Finalizando aplicação");
			
			LOGGER.info("Apagando imagens digitalizadas");
			for(File file : produto.getAllImagesFilesScanned()){
				FileManagement.delete(file);
			}
			
			LOGGER.info("Fechando conexao com scanner");
			try {
				CapiScanner capiScanner = CapiScanner.getInstance();
				capiScanner.closeConnection();
				try {
					capiScanner.dispose();
				} catch (ScannerException e) {
					LOGGER.error(e);
				}
			} catch (Error e) {
				LOGGER.error(e);
			} catch (Exception e) {
				LOGGER.error(e);
			}

			try {
				String ip = WebScannerConfig.getProperty("ip").trim();
				Integer porta = Integer.valueOf(WebScannerConfig.getProperty("porta").trim());
				
				LOGGER.info("Conectando no scanner update pelo ip {} e porta {}", ip, porta);
				Server server = ScannerUpdate.conectar(ip, porta);
				LOGGER.info("Respondendo server com o status {}", ApplicationStatus.EXIT.getStatus());
				server.respoder(ApplicationStatus.EXIT);
				
			} catch (ScannerUpdateException e) {
				LOGGER.error("Erro ao conectar no servidor de atualizacao ", e);
			} finally {
				System.exit(ApplicationStatus.EXIT.getStatus());
			}
		}
	}
}