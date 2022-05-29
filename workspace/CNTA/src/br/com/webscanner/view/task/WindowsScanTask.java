package br.com.webscanner.view.task;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.exception.ScannerConfigurationException;
import br.com.webscanner.exception.ScannerDoubleFeedException;
import br.com.webscanner.exception.ScannerObstructedException;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.scanner.Scannable;
import br.com.webscanner.model.domain.scanner.ScannerFactory;
import br.com.webscanner.util.TipificacaoAssicrona;
import br.com.webscanner.view.MessagePanel.MessageLevel;

public class WindowsScanTask extends SwingWorker<Void, Void> {

	private ProductScannerController controller;

	private static Logger logger = LogManager.getLogger(ScannTask.class
			.getName());
	private Set<Message> messages;

	public WindowsScanTask(ProductScannerController controller) {
		this.controller = controller;
		this.messages = new LinkedHashSet<Message>();
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

			this.carregarComunicacao(scannerSelected, scanner);

			logger.info("Iniciando digitalização das imagens");

			controller.showLoading(true, "Aguarde...");

			// INICIA EXECUÇÃO DO C# PARA ESCANEAR AS IMAGEMS
			if (!scanner.acquire()) {
				logger.warn("Não foi possível realizar a digitalização das imagens.");
				messages.add(new Message(Bundle
						.getString("scanner.scann.failed"), MessageLevel.ERROR));
				return null;
			}

			logger.info("Recuperando as imagens digitalizadas");

			// INICIA A EXECUÇÃO DA TIPIFICAÇÃO ASSICRONA

			String caminhoImagens = System.getProperty("user.home")
					+ System.getProperty("file.separator")
					+ WebScannerConfig.getProperty("pathImage")
					+ System.getProperty("file.separator") + "externo";

			TipificacaoAssicrona assicrono = new TipificacaoAssicrona(
					caminhoImagens, controller.getProduct().getModel()
							.isExportJpg(), controller.getProduct().getModel()
							.isSplitDuplex(), true, controller);

			assicrono.execute();

			while (true) {

				assicrono.setTerminouDigitalizacao(scanner.endTransfer());

				if (assicrono.isDone() == true)
					break;
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

	private void carregarComunicacao(Scanner scannerSelected, Scannable scanner) {

		logger.info("Conexão ao scanner {}", scannerSelected.getName());

		logger.info("Setando split duplex: {}", controller.getProduct()
				.getModel().isSplitDuplex());

		scanner.setSplitDuplex(controller.getProduct().getModel()
				.isSplitDuplex());

		scanner.setExportJpg(controller.getProduct().getModel().isExportJpg());

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
