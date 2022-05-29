/**
 * 
 */
package br.com.webscanner.view.task;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.exception.ScannerConfigurationException;
import br.com.webscanner.exception.ScannerDoubleFeedException;
import br.com.webscanner.exception.ScannerException;
import br.com.webscanner.exception.ScannerImageNotFoundException;
import br.com.webscanner.exception.ScannerObstructedException;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.scanner.Scannable;
import br.com.webscanner.model.domain.scanner.ScannerFactory;
import br.com.webscanner.model.services.AutomaticTypingService;
import br.com.webscanner.view.MainDesenv;
import br.com.webscanner.view.MessagePanel.MessageLevel;

/**
 * Classe de tarefa responsável pela exibição do loading e digitalização das
 * imagens.
 * 
 * @author Jonathan Camara
 *
 */
public class ScannTask extends SwingWorker<Void, Void> {
	private ProductScannerController controller;
	MainDesenv mainDesenv;

	private static Logger logger = LogManager.getLogger(ScannTask.class
			.getName());
	private Set<Message> messages;

	public ScannTask(ProductScannerController controller) {
		this.controller = controller;
		this.messages = new LinkedHashSet<Message>();
		mainDesenv = new MainDesenv();
	}

	@Override
	protected Void doInBackground() throws Exception {
		logger.info("Iniciando digitalização");
		controller.showLoading(true);
		messages.clear();

		Scannable scanner = null;
		Scanner scannerSelected = this.controller.getScannerSelected();

		try {
			scanner = ScannerFactory.getInstance(scannerSelected);
			logger.info("Abrindo conexão do data source manager");
			if (!scanner.openDSM()) {
				logger.error("Falha ao abrir conexão do data source manager.");
				messages.add(new Message(Bundle
						.getString("scanner.connection.datasource.failed"),
						MessageLevel.ERROR));
			}

			logger.info("Conexão ao scanner {}", scannerSelected.getName());
			if (!scanner.setScanner(scannerSelected)) {
				logger.error("Falha ao recuperar scanner default");
				messages.add(new Message(Bundle
						.getString("scanner.get.default.failed"),
						MessageLevel.ERROR));
				return null;
			}

			logger.info("Abrindo conexão do scanner.");
			if (!scanner.openScanner()) {
				logger.error("Falha ao abrir conexão com scanner");
				messages.add(new Message(Bundle
						.getString("scanner.connection.open.failed"),
						MessageLevel.ERROR));
				return null;
			}

			logger.info("Set Feeder Enabled {}", scanner.setFeederEnabled(true));

			logger.info("Setando split duplex: {}", controller.getProduct()
					.getModel().isSplitDuplex());

			scanner.setSplitDuplex(controller.getProduct().getModel()
					.isSplitDuplex());

			scanner.setExportJpg(controller.getProduct().getModel()
					.isExportJpg());

			logger.info("Setando capability autoscan");
			if (!scanner.setAutoScan(true)) {
				logger.warn("Falha ao setar a capability auto scann");
			}

			logger.info("Setando capability duplex");
			if (!scanner.setDuplex(true)) {
				logger.warn("Falha ao setar a capability duplex");
			}

			logger.info("Setando o XDPI");
			if (!scanner.setXDPI(300)) {
				logger.warn("Não foi possível setar o XDPI");
			}

			logger.info("Setando o YDPI");
			if (!scanner.setYDPI(300)) {
				logger.warn("Não foi possível setar o YDPI");
			}

			if (controller.getProduct().getModel().isExportJpg()) {
				logger.info("Setando pixel type GRAY");
				if (!scanner.setPixelTypeGRAY()) {
					logger.warn("Não foi possível setar pixel Type GRAY");
				}
			} else {
				logger.info("Setando pixel type BW");
				if (!scanner.setPixelTypeBW()) {
					logger.warn("Não foi possível setar pixel Type BW");
				}
			}
			
			

			scanner.setAutomaticSenseMedium();

			boolean isMicrEnable = true;
			logger.info("Setando MICRENABLED ");
			if (!scanner.setMicrEnabled(true)) {
				isMicrEnable = false;
				logger.warn("Não foi possível setar MICRENABLED");
			}

			boolean hasDeviceEvents = true;
			if (!scanner.setDeviceEvent()) {
				hasDeviceEvents = false;
				logger.warn("Não foi possível habilitar os eventos do scanner.");
			}

			if (!scanner.feederLoaded()) {
				logger.info("Get Feeder Enabled {}", scanner.getFeederEnabled());
				// Gambiarra para o fujtsu;
				// logger.info("Set Feeder Enabled {}",
				// scanner.setFeederEnabled(false));
				logger.info("Get Feeder Enabled {}", scanner.getFeederEnabled());
			}

			if (scannerSelected.getContrast() != null) {
				logger.info("Setando a capability Contrast");
				if (!scanner.setContrast(scannerSelected.getContrast()
						.getCurrentValue())) {
					logger.info("Falha ao setar o Contraste");
				}
			}

			if (scannerSelected.getBrightness() != null) {
				logger.info("Setando a capability Brightness");
				if (!scanner.setBrightness(scannerSelected.getBrightness()
						.getCurrentValue())) {
					logger.info("Falha ao setar o Brilho");
				}
			}

			logger.info("Iniciando digitalização das imagens");

			if (scannerSelected.getName().contains("KODAK") == true) {
				controller.showLoading(true, "Aguarde...");

			}
			if (!scanner.acquire()) {
				logger.warn("Não foi possível realizar a digitalização das imagens.");
				messages.add(new Message(Bundle
						.getString("scanner.scann.failed"), MessageLevel.ERROR));
				return null;
			}

			logger.info("Recuperando as imagens digitalizadas");

			while (scanner.hasMoreImages()) {
				mainDesenv.contadorDePaginas++;
				if (mainDesenv.contadorDePaginas  < 500) {
				Document document = new Document(
						controller.getDocumentsCount(),
						controller.getCaptureSequence());

				document.setContent(scanner.getImageScanned());
				document.setDateCapture(new SimpleDateFormat(
						"dd/MM/yyyy HH:mm:ss.SSS").format(new Date()));

				String cmc7 = null;
				if (isMicrEnable) {
					cmc7 = scanner.getCmc7();
				}

				AutomaticTypingService.typing(controller.getRecognitions(),
						controller.getDocumentModels(), cmc7, document);
				
				 String produto =  controller.getProduct().getId();
				if (produto.equals("PREV02") || produto.equals("PREV03" )) {
					if (!document.getName().equals("Página em Branco")) {
						controller.addDocument(document);
						controller.setViewerLastDocument();
					}
					}else {
						if (!document.getName().equals("Página em Branco")) {
						controller.addDocument(document);
						controller.setViewerLastDocument();
						}
					}
				
			}else if(mainDesenv.contadorDePaginas >= 500) {				
			 JOptionPane.showMessageDialog(null, "Atenção seu lote atingiu o processamento de 500 páginas"
					 						+"\n" 
					 						+"O mesmo deverá ser finalizado para o início de um novo lote!");
			 mainDesenv.contadorDePaginas = 0;

			
			}
		} 
		

			if (hasDeviceEvents) {
				scanner.getDeviceEvent();
			}

			logger.info("Finalizando transferencia de imagens");
			if (!scanner.endTransfer()) {
				logger.error("Falha ao finalizar a transferência das imagens.");
			}
		} catch (ScannerObstructedException e) {
			logger.warn("Scanner Obstruído. Message: {}", e.getMessage());
			messages.add(new Message(Bundle.getString("scanner.papperJam"),
					MessageLevel.WARN));
		} catch (ScannerDoubleFeedException e) {
			logger.warn("Papel espesso ou dupla alimentação. Message: {}",
					e.getMessage());
			messages.add(new Message(Bundle.getString("scanner.doubleFeeded"),
					MessageLevel.WARN));
		} catch (ScannerImageNotFoundException e) {
			logger.warn(e.getMessage());
		} catch (ScannerException e) {
			logger.warn("Erro retornado pelo scanner. Message: {}",
					e.getMessage());
			messages.add(new Message(Bundle.getString("scanner.scann.failed"),
					MessageLevel.WARN));
		} catch (ScannerConfigurationException e) {
			logger.warn("Usuário alterou na tela de configurações do scanner.");
			messages.add(new Message(Bundle
					.getString("scanner.configuration.changed"),
					MessageLevel.ERROR));
		} catch (Exception e) {
			logger.error("Exception: {}", e);
		} catch (Error e) {
			logger.error("Error: {}", e);
		} finally {
			
			logger.info("Finalizando transferencia de imagens END TRANSFER");
			if (!scanner.endTransfer()) {
				logger.error("Falha ao finalizar a transferência das imagens.");
			}

			logger.info("Desabilidando default source");
			if (!scanner.disableDefautSource()) {
				logger.error("Falha ao desabilitar o default source");
			}
			logger.info("Fechando o scanner");
			if (!scanner.closeDSM()) {
				logger.error("Erro ao fechar a conexão com o scanner");
				messages.add(new Message(Bundle
						.getString("scanner.connection.close.failed"),
						MessageLevel.ERROR));
			}
		}

		return null;
	}

	@Override
	protected void done() {
		super.done();
		logger.info("Finalizando digitalização");

		controller.setEnableActions(true);
		controller.showLoading(false);

		for (Message message : getMessages()) {
			controller.showMessage(message);
		}
	}

	public Set<Message> getMessages() {
		return messages;
	}
}