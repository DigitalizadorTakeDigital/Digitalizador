package br.com.webscanner.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.view.ProductScanner;

public class ClearLotListener implements ActionListener {
	private static Logger logger = LogManager.getLogger(ClearLotListener.class.getName());
	private ProductScannerController controller;
	private ProductScanner productScanner;

	{
		UIManager.put("OptionPane.yesButtonText", "Sim");   
		UIManager.put("OptionPane.noButtonText", "Não"); 
		UIManager.put("OptionPane.cancelButtonText", "Cancelar");
	}
	
	public ClearLotListener(ProductScanner productScanner, ProductScannerController controller) {
		this.productScanner = productScanner;
		this.controller = controller;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		logger.info("Iniciando limpeza de lote");
		try {
			controller.showLoading(true);
			
			int option = JOptionPane.showConfirmDialog(productScanner, Bundle.getString("dialog.cancel.lot"), "Cancelar lote.", JOptionPane.YES_NO_OPTION);
			if(option == JOptionPane.YES_OPTION){
				controller.clearLot();
			}
		} catch (Exception e) {
			logger.error("Erro ao realizar a limpeza do lote. {}", e.getMessage());
		} finally{
			controller.showLoading(false);
		}
	}
}