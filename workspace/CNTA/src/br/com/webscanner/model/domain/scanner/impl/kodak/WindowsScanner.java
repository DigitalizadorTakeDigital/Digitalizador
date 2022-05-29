package br.com.webscanner.model.domain.scanner.impl.kodak;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.ScannerDoubleFeedException;
import br.com.webscanner.exception.ScannerException;
import br.com.webscanner.exception.ScannerObstructedException;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.ScannerPropertiesRange;
import br.com.webscanner.model.domain.image.ImageInfo;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.model.domain.scanner.Scannable;
import br.com.webscanner.util.CentralSalvamentoImagem;
import br.com.webscanner.util.CommandExecution;
import br.com.webscanner.util.FileManagement;
import br.com.webscanner.util.ImageUtil;

public class WindowsScanner implements Scannable {

	private static final Logger LOGGER = LogManager
			.getLogger(WindowsScanner.class.getName());

	private List<URL> images = new ArrayList<URL>();
	private boolean splitDuplex;
	private boolean exportJpg;

	private Scanner scan;
	


	public WindowsScanner(Scanner scanner) {

		this.scan = scanner;
	}

	private String caminhoImagens = System.getProperty("user.home")
			+ System.getProperty("file.separator")
			+ WebScannerConfig.getProperty("pathImage") + "\\externo";

	public static Process processoEscaneamento;

	// new File(System.getProperty("user.home") +
	// System.getProperty("file.separator") +
	// WebScannerConfig.getProperty("pathImage"), imageName.toString())

	public WindowsScanner() {
		images = new ArrayList<URL>();
	}

	private boolean iniciarLeituraExterna() {

		String separador = System.getProperty("file.separator");

		String caminhoExe = FileManagement.JAR_PATH.concat("\\classes")
				.concat(separador).concat("leituraExterna").concat(separador)
				.concat("FileZilla_3.40.0_win64-setup.exe");

		String pastaTiffUnico = "'"
				+ caminhoImagens.concat(separador).concat("tiffUnico") + "'";

//		// APAGA A PASTA
		new File(caminhoImagens).delete();

		// CRIA A PASTA
		new File(caminhoImagens).mkdir();

		String nomeDriveEscaner = "'" + scan.getName().split(" ")[3] + "'";

		String parametros = pastaTiffUnico + " " + nomeDriveEscaner;

		String comando = caminhoExe.concat(" ").concat(parametros);

		String retorno = CommandExecution.executarCmdRetorno(comando);

		if (!retorno.toUpperCase().contains("OK")) {

			LOGGER.info("Scaneamento Externo com Sucesso!");

			return true;

		}

		if (retorno.toUpperCase().contains("ERRO")) {

			LOGGER.error("Scaneamento Externo com Falha! Mensagem: "
					.concat(retorno));

			return false;
		} else {

			LOGGER.error("Retorno não reconhecido, Retorno: ".concat(retorno));

			return false;
		}

	}

	public void iniciarLeituraExternaAssicrona() throws IOException,
			InterruptedException {

		String separador = System.getProperty("file.separator");

		//USAR NO ECLIPSE
//		String caminhoExe = FileManagement.JAR_PATH.concat(separador).concat("\\classes\\")
//				.concat("leituraExterna").concat(separador)
//				.concat("ScannerDemo.exe");
		
//		PARA GERAR .JAR
		String caminhoExe = FileManagement.JAR_PATH.concat(separador).concat("leituraExterna").concat(separador)
				.concat("ScannerDemo.exe");

		String pastaSalvaImagens = '"'
				+ caminhoImagens+ '"';

		// APAGA A PASTA
//		FileManagement.deleteFolder(new File(caminhoImagens),true);
		

		// CRIA A PASTA
		new File(caminhoImagens).mkdir();

		String nomeDriveEscaner = '"' + scan.getName().split(" ")[2] + '"';
		LOGGER.info("Scanner selecionado e: " + nomeDriveEscaner + " | " + scan.getName());
		
		
//		1 - PB + Sensor
//		2 - Cinza + Sensor
//		3 - PB
//		4 - Cinza
		
		String product = ApplicationData.getProductId();
		
		String scannerColor = "1";
		
		if(product.equals("ADQU02")) {
		   scannerColor = "3";
		}
				
		if(scan.getScannerColor() != null) {
			if(!scan.getScannerColor().isEmpty()) {
				scannerColor = scan.getScannerColor();
			}
		}
		
		String parametros = pastaSalvaImagens + " " + nomeDriveEscaner + " " + scannerColor;

		String comando = "\""+caminhoExe.concat("\" ").concat(parametros);

		// String retorno = CommandExecution.executarCmdRetorno(comando);

		this.processoEscaneamento = CommandExecution.executeAssicrono(comando);

	}

	@Override
	public boolean acquire() {

		try {
			iniciarLeituraExternaAssicrona();
		} catch (IOException | InterruptedException e1) {

			LOGGER.error("Erro ao iniciar leitura externa" + e1);
		}

		boolean voltar = true;

		if (voltar == true)
			return true;
		

		// boolean isSucessoScaneamento = iniciarLeituraExterna();

		// if (isSucessoScaneamento == false)
		// return false;

		File caminho = new File(caminhoImagens);

		LOGGER.info("Digitalizando impressora externa parametro {}",
				caminho.getAbsolutePath());

		if (caminho.exists()) {
			File[] imagens = caminho.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".tif");
				}
			});
			LOGGER.info("Imagens TIF encontrados = {}", imagens.length);
			for (File imagem : imagens) {
				try {
					images.add(imagem.toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

		}

		return true;
	}

	// @Override
	public java.awt.Image getImage() {
		try {
			return ImageUtil.getFileImage(images.remove(0));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean openDSM() {
		return true;
	}

	@Override
	public boolean getScannerUserSelect() {
		return true;
	}

	@Override
	public boolean openScanner() {
		ApplicationData.putParam("serialNumber", "0");
		return true;
	}

	@Override
	public boolean setAutoScan(boolean param) {
		return true;
	}

	@Override
	public boolean setDuplex(boolean param) {
		return true;
	}

	@Override
	public boolean disableDefautSource() {
		return true;
	}

	@Override
	public boolean setPixelTypeBW() {
		return true;
	}

	@Override
	public boolean hasMoreImages() {
		return !images.isEmpty();
	}

	// @Override
	public boolean isDuplex() {
		return true;
	}

	@Override
	public boolean setPixelTypeGRAY() {
		return true;
	}

	@Override
	public boolean closeDSM() {
		return true;
	}

	@Override
	public boolean setYDPI(int dpi) {
		return true;
	}

	@Override
	public boolean setXDPI(int dpi) {
		return true;
	}

	@Override
	public boolean endTransfer() {

		try {
			processoEscaneamento.waitFor();
		} catch (InterruptedException e1) {


			
		}
		
//		InputStream errorStream = processoEscaneamento.getErrorStream();
//		InputStream infoStream = processoEscaneamento.getInputStream();


		return true;
	}

	@Override
	public boolean feederLoaded() {
		return true;
	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public boolean setIndicators(boolean param) {
		return true;
	}

	// @Override
	public String getExtendedImageInfo() {
		return "";
	}

	@Override
	public boolean setMicrEnabled(boolean param) {
		return true;
	}

	@Override
	public boolean setDeviceEvent() {
		return false;
	}

	@Override
	public int processEvent() {
		return 257;
	}

	@Override
	public boolean translateMessage() {
		return false;
	}

	@Override
	public boolean getMessage() throws ScannerObstructedException,
			ScannerDoubleFeedException {
		return false;
	}

	@Override
	public void getDeviceEvent() throws ScannerObstructedException,
			ScannerDoubleFeedException {

	}

	@Override
	public ImageScanned getImageScanned() throws ScannerException {
		Image image = this.getImage();
		ImageScanned imageScanned = null;

		try {
			if (this.splitDuplex) {
				ImageInfo frontTIFF = ImageUtil.saveTiff(image, "F");
				if (!this.exportJpg) {
					imageScanned = new ImageScanned(
							new br.com.webscanner.model.domain.image.Image(
									frontTIFF, null), null);
				} else {
					ImageInfo frontJPG = ImageUtil.saveJpeg(image, "F");
					imageScanned = new ImageScanned(
							new br.com.webscanner.model.domain.image.Image(
									frontTIFF, null),
							new br.com.webscanner.model.domain.image.Image(
									frontJPG, null));
				}
			} else {
				ImageInfo frontTIFF = ImageUtil.saveTiff(image, "F");
				ImageInfo rearTIFF = null;
				if (!this.exportJpg) {
					image = this.getImage();
					rearTIFF = ImageUtil.saveTiff(image, "V");
					imageScanned = new ImageScanned(
							new br.com.webscanner.model.domain.image.Image(
									frontTIFF, rearTIFF), null);
				} else {
					ImageInfo frontJPG = ImageUtil.saveJpeg(image, "F");
					image = this.getImage();
					rearTIFF = ImageUtil.saveTiff(image, "V");
					ImageInfo rearJPG = ImageUtil.saveJpeg(image, "V");
					imageScanned = new ImageScanned(
							new br.com.webscanner.model.domain.image.Image(
									frontTIFF, rearTIFF),
							new br.com.webscanner.model.domain.image.Image(
									frontJPG, rearJPG));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ScannerException("Erro ao gerar o SHA1 da imagem");
		}
		return imageScanned;
	}

	@Override
	public String getCmc7() {
		return null;
	}

	@Override
	public void setSplitDuplex(boolean param) {
		this.splitDuplex = param;
	}

	@Override
	public boolean setScanner(Scanner scanner) {
		return true;
	}

	@Override
	public int setXFERMECH() {
		return 0;
	}

	@Override
	public int setAutomaticSenseMedium() {
		return 0;
	}

	@Override
	public boolean getFeederEnabled() {
		return true;
	}

	@Override
	public int setFeederEnabled(Boolean param) {
		return 0;
	}

	@Override
	public ScannerPropertiesRange getContrast() {
		return null;
	}

	@Override
	public ScannerPropertiesRange getBrightness() {
		return null;
	}

	@Override
	public boolean setContrast(int value) {
		return false;
	}

	@Override
	public boolean setBrightness(int value) {
		return false;
	}

	@Override
	public void setExportJpg(boolean param) {
		this.exportJpg = param;
	}

	private boolean duplex = true;

	@Override
	public void getImageScannedAssicrono(CentralSalvamentoImagem central)
			throws ScannerException {
		central.setDuplex(this.duplex);
		central.setExportJpg(this.exportJpg);

		Image image = this.getImage();
		ImageScanned imageScanned = null;

		if (this.duplex) {
			Image verso = null;

			if (!this.splitDuplex) {

				// if (dll.hasMoreImages() > 0) {
				// verso = this.getImage();
				//
				// }

				central.adicionarImagemSalvar(image, verso);
			} else {

				central.adicionarImagemSalvar(image, null);

			}

		} else {

			central.adicionarImagemSalvar(image, null);

		}

	}


}
