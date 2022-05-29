/**
 * 
 */
package br.com.webscanner.view.task;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.exception.ExportException;
import br.com.webscanner.exception.ScannerUpdateException;
import br.com.webscanner.infra.ScannerUpdate;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.infra.ScannerUpdate.Server;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.ApplicationStatus;
import br.com.webscanner.model.domain.Message;

/**
 * @author Diego
 *
 */
public class CloseLotTask extends SwingWorker<Void, Void> {
	private ProductScannerController controller;
	private static Logger logger = LogManager.getLogger(CloseLotTask.class.getName());

	public CloseLotTask(ProductScannerController controller) {
		this.controller = controller;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		logger.info("Iniciando fechamento de lote/exportação.");
		
		try {
			controller.setEnableActions(false);
			controller.prepareClosingView();
			controller.showLoading(true, Bundle.getString("export.start", controller.getProduct().getName()));
			if(this.controller.isProductValid()){
				try {
					this.controller.export();
					this.controller.clearLot();
					
					if(controller.getProduct().getModel().isExitAfterProcessed()) {
						logger.info("Finalizando a aplicação com sucesso!");
						
						try {
							String ip = WebScannerConfig.getProperty("ip").trim();
							Integer porta = Integer.valueOf(WebScannerConfig.getProperty("porta").trim());
							
							logger.info("Conectando no scanner update pelo ip {} e porta {}", ip, porta);
							Server server = ScannerUpdate.conectar(ip, porta);
							logger.info("Respondendo server com o status {}", ApplicationStatus.SUCCESS.getStatus());
							server.respoder(ApplicationStatus.SUCCESS);
						} catch (ScannerUpdateException e) {
							logger.error("Erro ao conectar no servidor de atualizacao ", e);
						} finally {
							System.exit(ApplicationStatus.SUCCESS.getStatus());
						}
					}
				} catch (ExportException e) {
					logger.error("Erros na exportação. Lote não foi fechado.");
				} finally{
					for(Message message : controller.getMessages()){
						this.controller.showMessage(message);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Stack {}", e);
		}
		return null;
	}

	@Override
	protected void done() {
		super.done();
		controller.setEnableActions(true);
		controller.showLoading(false);
//		controller.clearImagePanel();
	}
}
