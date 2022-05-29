package br.com.webscanner.model.domain.scanner.impl.samsung;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.DriverNotInstalledException;
import br.com.webscanner.exception.ScannerConfigurationException;
import br.com.webscanner.exception.ScannerDoubleFeedException;
import br.com.webscanner.exception.ScannerException;
import br.com.webscanner.exception.ScannerImageNotFoundException;
import br.com.webscanner.exception.ScannerObstructedException;
import br.com.webscanner.libs.Kernel32;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.BITMAPINFOHEADER;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.Scanner.DriverScanner;
import br.com.webscanner.model.domain.ScannerPropertiesRange;
import br.com.webscanner.model.domain.image.ImageInfo;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.model.domain.scanner.BridgeManager;
import br.com.webscanner.model.domain.scanner.Scannable;
import br.com.webscanner.model.domain.scanner.impl.twain.Twain;
import br.com.webscanner.util.CentralSalvamentoImagem;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.RegistryUtil;

import com.sun.jna.Pointer;

public class SamsungScanner implements Scannable {
	
	private static final int SUCCESS = 0;
	private static final int FAILURE = 1;
	private static final int CANCEL  = 3;
	private static final int END_LIST = 7;
	
	private static final int TWDE_PAPERDOUBLEFEED = 12;
	private static final int TWDE_PAPERJAM        = 13;

	private static final short MSG_XFERREADY      = 0x0101; /* The data source has data ready           		  */
	private static final short MSG_CLOSEDSREQ     = 0x0102; /* Request for Application. to close DS               */
	private static final short MSG_CLOSEDSOK      = 0x0103; /* Tell the Application. to save the state.           */
	private static final short MSG_DEVICEEVENT    = 0X0104; /* Some event has taken place               Added 1.8 */
	
	private boolean duplex;
	private boolean splitDuplex;
	private boolean exportJpg;
	private Scanner scannerSelecionado = null;
	
	
	private static Logger logger = LogManager.getLogger(SamsungScanner.class.getName());
	private static Twain dll = BridgeManager.getTwain();
	private static Kernel32 kernel32 = BridgeManager.getKernel32();
	
	{
		ApplicationData.putParam("serialNumber", "0");
	}
	
	private void lexmarkShutdownHook(){
		logger.info("Alterando configuracao da lexmark ColorDepth para BW");
		
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "ColorDepth", "REG_DWORD", "1");
		RegistryUtil.setRegistry("HKCU\\Software\\GCCD\\Conf", "ColorDepth", "REG_DWORD", "1");	
	}
	
	private boolean isValidConfigurationLexmark(){
		logger.info("Validando configuracoes Lexmark");
		
		if (!RegistryUtil.CompareRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "HKCU\\Software\\GCCD\\Conf", "Resolution")){
			logger.error("Resolução alterada");
			return false;
		}
		
		if (!RegistryUtil.CompareRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "HKCU\\Software\\GCCD\\Conf", "PaperSize")){
			logger.error("Alterado o tamanho do papel");
			return false;
		}
		
		if (!RegistryUtil.CompareRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "HKCU\\Software\\GCCD\\Conf", "Orientation")){
			logger.error("Alterado a orientação da folha");
			return false;
		}
		
//		if (!RegistryUtil.CompareRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "HKCU\\Software\\GCCD\\Conf", "Duplex")){
//			logger.error("Alterou a configuração do Duplex");
//			return false;
//		}
		
		if (!RegistryUtil.CompareRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "HKCU\\Software\\GCCD\\Conf", "ColorDepth")){
			logger.error("Alterado o color depth");
			return false;
		}
		
		if (!RegistryUtil.CompareRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "HKCU\\Software\\GCCD\\Conf", "Brightness")){
			logger.error("Alterado o brilho");
			return false;
		}
		
		if (!RegistryUtil.CompareRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "HKCU\\Software\\GCCD\\Conf", "Contrast")){
			logger.error("Alterado o contraste");
			return false;
		}
		
		if (!RegistryUtil.CompareRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "HKCU\\Software\\GCCD\\Conf", "ContentType")){
			logger.error("Alterado o tipo de conteúdo");
			return false;
		}
		
		if (!RegistryUtil.getValue("Software\\Lexmark\\NetworkTwain\\Presets", "LastPreset").trim().isEmpty()){
			logger.error("Alterado o Preset");
			return false;
		}

		return true;
	}
	
	private void setConfigurationLexmark(){
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "Resolution", "REG_DWORD", "300");
		RegistryUtil.setRegistry("HKCU\\Software\\GCCD\\Conf", "Resolution", "REG_DWORD", "300");
		//Paper Size
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "PaperSize", "REG_DWORD", "4");
		RegistryUtil.setRegistry("HKCU\\Software\\GCCD\\Conf", "PaperSize", "REG_DWORD", "4");
		//Orientation
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "Orientation", "REG_DWORD", "0");
		RegistryUtil.setRegistry("HKCU\\Software\\GCCD\\Conf", "Orientation", "REG_DWORD", "0");
		//Duplex
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "Duplex", "REG_DWORD", "1");
		RegistryUtil.setRegistry("HKCU\\Software\\GCCD\\Conf", "Duplex", "REG_DWORD", "1");
		//Content Type
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "ContentType", "REG_DWORD", "0");
		RegistryUtil.setRegistry("HKCU\\Software\\GCCD\\Conf", "ContentType", "REG_DWORD", "0");
		//ColorDepth
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "ColorDepth", "REG_DWORD", "8");
		RegistryUtil.setRegistry("HKCU\\Software\\GCCD\\Conf", "ColorDepth", "REG_DWORD", "8");	
		//Brightness
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "Brightness", "REG_DWORD", "5");
		RegistryUtil.setRegistry("HKCU\\Software\\GCCD\\Conf", "Brightness", "REG_DWORD", "5");	
		//Contrast
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "Contrast", "REG_DWORD", "0");
		RegistryUtil.setRegistry("HKCU\\Software\\GCCD\\Conf", "Contrast", "REG_DWORD", "0");
		//ContentType
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "ContentType", "REG_DWORD", "0");
		RegistryUtil.setRegistry("HKCU\\Software\\GCCD\\Conf", "ContentType", "REG_DWORD", "0");
		//last preset
		RegistryUtil.setRegistry("HKCU\\Software\\Lexmark\\NetworkTwain\\Presets", "LastPreset", "REG_SZ", "\"\"");
	}
	
	@Override
	public boolean acquire() throws ScannerObstructedException, DriverNotInstalledException, ScannerDoubleFeedException, ScannerConfigurationException {
		int resultCode = dll.acquire();
		
		if (resultCode !=  FAILURE){
			switch (resultCode) {
				case MSG_XFERREADY:
					if (!isValidConfigurationLexmark()){
						throw new ScannerConfigurationException();
					}
					return true;
			
				case MSG_DEVICEEVENT:
					getDeviceEvent();
					
				case MSG_CLOSEDSREQ:
					return true;
				
				case MSG_CLOSEDSOK:
					return true;
				
			default:
				break;
			}
		}
		return false;
	}

	@Override
	public boolean openDSM() {
		if (dll.openDSM() == SUCCESS){
			return true;
		}
		return false;
	}

	@Override
	public boolean getScannerUserSelect() {
		int returnCode = dll.getScannerUserSelect();
		if (returnCode == SUCCESS || returnCode == CANCEL){
			return true;
		}
		return false;
	}

	@Override
	public boolean openScanner() {
		if (scannerSelecionado != null){
			setConfigurationLexmark();
		}

		if (dll.openScanner() == SUCCESS){
			return true;	
		}
		return false;
	}

	@Override
	public boolean setAutoScan(boolean param) {
		if(dll.setAutoScan(param) == SUCCESS){
			return true;
		}
		return false;
	}

	@Override
	public boolean setDuplex(boolean param) {
		this.duplex = param;
		return true;
	}

	@Override
	public boolean disableDefautSource() {
		if (scannerSelecionado != null){
			try {
				logger.info("Entrou no Disable Default Source");
				while(hasMoreImages()){
					freeImage();
				}
			} catch (ScannerObstructedException e) {
				e.printStackTrace();
			} catch (ScannerDoubleFeedException e) {
				e.printStackTrace();
			}
		}


		if (dll.disableDefaultSource() == SUCCESS){
			return true;
		}
		return false;
	}

	@Override
	public boolean closeDSM() {
		if (dll.closeDSM() == SUCCESS) {
			return true;
		}
		return false;
	}

	@Override
	public boolean setPixelTypeBW() {
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
	public boolean hasMoreImages() throws ScannerObstructedException, ScannerDoubleFeedException {
		if (dll.hasMoreImages() > 0){
			return true;
		}
		
		return false;
	}

	@Override
	public boolean endTransfer() {
		lexmarkShutdownHook();
		
		if (dll.endTransfer() == SUCCESS){
			return true;
		}
		return false;
	}

	
	private Image getImage() throws ScannerException {
		int point =  dll.getImage();
		Pointer p = kernel32.GlobalLock(point);
		
		if(point == 0){
			throw new ScannerImageNotFoundException("Ponteiro retornou 0 na recuperação da imagem");
		}
		
//		Pointer p = new Pointer(point);
		BITMAPINFOHEADER bih = new BITMAPINFOHEADER(p);
		Image img;
		if (bih.biBitCount ==1){		
			img  = ImageUtil.xferDIB1toImage(bih);
		}
		else if(bih.biBitCount ==8){
			img  = ImageUtil.xferDIB8toImage(bih);
		}
		else {
			img  = ImageUtil.xferDIB24toImage(bih);
		}
		kernel32.GlobalUnlock(point);
		kernel32.GlobalFree(point);
		return img;
	}
	
	@Override
	public ImageScanned getImageScanned() throws ScannerException {
		Image image = this.getImage();
		ImageScanned imageScanned = null;

		if(this.duplex){
			ImageInfo frontTIFF;
			ImageInfo frontJPG = null;
			try {
				frontTIFF = ImageUtil.saveTiff(image, "F");
				if(this.exportJpg){
					frontJPG = ImageUtil.saveJpeg(image, "F");
				}

				if (!this.splitDuplex){
					ImageInfo rearTIFF = null;
					if (dll.hasMoreImages()>0){
						image = this.getImage();
						rearTIFF =  ImageUtil.saveTiff(image, "V");
					}
					
					if(this.exportJpg){
						ImageInfo rearJPG = ImageUtil.saveJpeg(image, "V");
						imageScanned = new ImageScanned(new br.com.webscanner.model.domain.image.Image(frontTIFF, rearTIFF), new br.com.webscanner.model.domain.image.Image(frontJPG, rearJPG));
					} else {
						imageScanned = new ImageScanned(new br.com.webscanner.model.domain.image.Image(frontTIFF, rearTIFF), null);
					}
					
				}else{
					if(this.exportJpg){
						imageScanned = new ImageScanned(new br.com.webscanner.model.domain.image.Image(frontTIFF, null), new br.com.webscanner.model.domain.image.Image(frontJPG, null));
					} else {
						imageScanned = new ImageScanned(new br.com.webscanner.model.domain.image.Image(frontTIFF, null), null);
					}
				}
				
			} catch (FileNotFoundException e) {
				logger.error("Erro ao salvar imagem {}", e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				logger.error("Erro ao salvar imagem {}", e.getMessage());
			} catch (IOException e) {
				logger.error("Erro ao salvar imagem {}", e.getMessage());
				throw new ScannerException("Erro ao gerar o SHA1 da imagem");
			}
			
		}else{
			try {
				ImageInfo frontTIFF = ImageUtil.saveTiff(image, "F");
				ImageInfo frontJPG = null;
				if(this.exportJpg){
					frontJPG = ImageUtil.saveJpeg(image, "F");
					imageScanned = new ImageScanned(new br.com.webscanner.model.domain.image.Image(frontTIFF, null), new br.com.webscanner.model.domain.image.Image(frontJPG, null));
				} else {
					imageScanned = new ImageScanned(new br.com.webscanner.model.domain.image.Image(frontTIFF, null), null);
				}
			} catch (FileNotFoundException e) {
				logger.error("Erro ao salvar imagem {}", e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				logger.error("Erro ao salvar imagem {}", e.getMessage());
			} catch (IOException e) {
				logger.error("Erro ao salvar imagem {}", e.getMessage());
				throw new ScannerException("Erro ao gerar o SHA1 da imagem");
			}
		}
				
		return imageScanned;
	}

	@Override
	public boolean setPixelTypeGRAY() {
		return true;
	}

	
	@Override
	public boolean feederLoaded() {
		return dll.feederLoaded();
	}

	@Override
	public int getStatus() {
		return dll.getStatus();
	}

	@Override
	public boolean setIndicators(boolean param) {
		if (dll.setIndicators(param) == SUCCESS){
			return true;
		}
		return false;
	}

	@Override
	public boolean setMicrEnabled(boolean param) {
		return false;
	}

	@Override
	public boolean setDeviceEvent() {
		if ( dll.setDeviceEvent() == SUCCESS){
			return true;
		}
		return false;
	}
	
	@Override
	public int processEvent() {
		return dll.processEvent();
	}

	@Override
	public boolean translateMessage() {
		return dll.translateMessage();
	}

	@Override
	public boolean getMessage() throws ScannerObstructedException, ScannerDoubleFeedException {
		return dll.getMessage();
	}

	@Override
	public void getDeviceEvent() throws ScannerObstructedException, ScannerDoubleFeedException {
		int codeEvent = dll.getDeviceEvent();
		
		if (codeEvent == TWDE_PAPERJAM){
			throw new ScannerObstructedException();
		}
		else if(codeEvent == TWDE_PAPERDOUBLEFEED){
			throw new ScannerDoubleFeedException();
		}
	}

	@Override
	public String getCmc7() {
		return null;
	}

	@Override
	public void setSplitDuplex(boolean param) {
		this.splitDuplex = param;
	}
	
	
	public int getFirstScanner(){
		return dll.getFirstScanner();
	}
	
	public int getNextScanner(){
		return dll.getNextScanner();
	}
	
	private Scanner getScanner(){
		int pointerScanner = dll.getScannerName();
		Pointer p = new Pointer(pointerScanner);
		
		String name = p.getString(0);
		
//		if(name.equalsIgnoreCase("Lexmark Network TWAIN Scan")){
//			return new Scanner(DriverScanner.LEXMARK, name, null, null, null);
//		}
		
		return new Scanner(DriverScanner.TWAIN, name, null, null, null);
	}

	@Override
	public boolean setScanner(Scanner scanner) {
		int retorno = dll.getFirstScanner();
		if (retorno == 0){
			scannerSelecionado = getScanner();
			if (scanner.equals(scannerSelecionado)){
				return true;
			}
		}
	
		while(dll.getNextScanner() != END_LIST) {
			scannerSelecionado = getScanner();
			if (scanner.equals(scannerSelecionado)){
				return true;
			}
		}
		return false;
	}

	
	private void freeImage() {
		dll.getImage();
	}

	@Override
	public int setXFERMECH() {
		return dll.setXFERMECH();
	}

	@Override
	public int setAutomaticSenseMedium() {
		return dll.setAutomaticSenseMedium();
	}

	@Override
	public boolean getFeederEnabled() {
		return (dll.getFeederEnabled() ==1);
	}

	@Override
	public int setFeederEnabled(Boolean param) {
		return dll.setFeederEnabled(param);
	}

	@Override
	public ScannerPropertiesRange getContrast() {
		logger.info("GetContrast");
		ScannerPropertiesRange contrast = null;
		if (dll.getContrast() == SUCCESS){
			contrast = new ScannerPropertiesRange();
			contrast.setMinimumValue(dll.getContrastMin());
			contrast.setMaximumValue(dll.getContrastMax());
			contrast.setCurrentValue(dll.getContrastCurrent());
			logger.info("GetContrast - OK");
		}
		return contrast;
	}

	@Override
	public ScannerPropertiesRange getBrightness() {
		logger.info("GetBrightness");
		ScannerPropertiesRange brightness = null;
		if (dll.getBrightness() == SUCCESS){
			brightness = new ScannerPropertiesRange();
			brightness.setMinimumValue(dll.getBrightnessMin());
			brightness.setMaximumValue(dll.getBrightnessMax());
			brightness.setCurrentValue(dll.getBrightnessCurrent());
			logger.info("GetBrightness - OK");
		}
		return brightness;
	}

	@Override
	public boolean setContrast(int value) {
		return true;
	}

	@Override
	public boolean setBrightness(int value) {
		return true;
	}

	@Override
	public void setExportJpg(boolean param) {
		this.exportJpg = param;
	}

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

				if (dll.hasMoreImages() > 0) {
					verso = this.getImage();

				}

				central.adicionarImagemSalvar(image, verso);
			} else {

				central.adicionarImagemSalvar(image, null);

			}

		} else {

			central.adicionarImagemSalvar(image, null);

		}
		
	}
}
