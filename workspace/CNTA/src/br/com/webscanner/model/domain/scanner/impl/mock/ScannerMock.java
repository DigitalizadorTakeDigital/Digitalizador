package br.com.webscanner.model.domain.scanner.impl.mock;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
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
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.ApplicationData.Build;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.ScannerPropertiesRange;
import br.com.webscanner.model.domain.image.ImageInfo;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.model.domain.scanner.Scannable;
import br.com.webscanner.util.CentralSalvamentoImagem;
import br.com.webscanner.util.ImageUtil;

public class ScannerMock implements Scannable {

	private static final Logger LOGGER = LogManager.getLogger(ScannerMock.class
			.getName());

	private List<URL> images = null;
	private boolean splitDuplex;
	private boolean exportJpg;

	public ScannerMock() {
		images = new ArrayList<URL>();
	}

	@Override
	public boolean acquire() {
		if (ApplicationData.getBuild() == Build.DESENV) {
			LOGGER.info("Digitalizando mock DESENV");
			try {
				String product = ApplicationData.getProductId();
				String path = "br/com/webscanner/resources/mock/" + product;
				if (ScannerMock.class.getClassLoader().getResource(path) == null) {
					path = "br/com/webscanner/resources/mock/acta01";
				}

				int count = 1;

				while (ScannerMock.class.getClassLoader().getResource(
						path + "/image" + count + ".tif") != null) {
					images.add(ScannerMock.class.getClassLoader().getResource(
							path + "/image" + count + ".tif"));
					images.add(ScannerMock.class.getClassLoader().getResource(
							path + "/verso.tif"));
					count++;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		} else {
			File caminho = new File(ApplicationData.getParam("mock"));
			LOGGER.info("Digitalizando mock parametro {}",
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

//				if (dll.hasMoreImages() > 0) {
//					verso = this.getImage();
//
//				}

				central.adicionarImagemSalvar(image, verso);
			} else {

				central.adicionarImagemSalvar(image, null);

			}

		} else {

			central.adicionarImagemSalvar(image, null);

		}

	}
}