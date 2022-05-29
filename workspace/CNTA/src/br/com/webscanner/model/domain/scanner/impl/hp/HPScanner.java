package br.com.webscanner.model.domain.scanner.impl.hp;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.DriverNotInstalledException;
import br.com.webscanner.exception.ScannerConfigurationException;
import br.com.webscanner.exception.ScannerDoubleFeedException;
import br.com.webscanner.exception.ScannerException;
import br.com.webscanner.exception.ScannerImageNotFoundException;
import br.com.webscanner.exception.ScannerObstructedException;
import br.com.webscanner.infra.WebScannerConfig;
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

import com.sun.jna.Pointer;

public class HPScanner implements Scannable {
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
	private boolean micrEnabled;
	private Scanner scannerSelecionado = null;
	private String  cmc7;
	private List<Integer> imageList = new ArrayList<Integer>();
	private ScanSettingsTemplate originalTemplate;
	private ScanSettingsTemplate newTemplate;
	private File templateFile = new File(System.getProperty("user.home"), WebScannerConfig.getProperty("hpTemplateFile"));
	
	private static Logger logger = LogManager.getLogger(HPScanner.class.getName());
	private static Twain dll = BridgeManager.getTwain();
	private static Kernel32 kernel32 = BridgeManager.getKernel32();
	
	{
		ApplicationData.putParam("serialNumber", "0");
	}
	
	@Override
	public boolean acquire() throws ScannerObstructedException, DriverNotInstalledException, ScannerDoubleFeedException, ScannerConfigurationException {
		imageList.clear();
		int resultCode = dll.acquire();
		
		try {
			if (resultCode !=  FAILURE){
				switch (resultCode) {
				case MSG_XFERREADY:
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
		} finally {
			int imageCount = dll.hasMoreImages();
			while (imageCount-- > 0) {
				int point =  dll.getImage();
				imageList .add(point);
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
		updateDisplay();
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
		dll.setDuplex((param) ? 1 : 0);
		this.duplex = dll.isDuplex();
		logger.info("DUPLEX:" + dll.isDuplex());
		return (this.duplex);
	}

	@Override
	public boolean disableDefautSource() {
		if (dll.disableDefaultSource() == SUCCESS){
			return true;
		}
		return false;
	}

	@Override
	public boolean closeDSM() {
		hpShutdownHook();
		if (dll.closeDSM() == SUCCESS)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean setPixelTypeBW() {
//		int retorno = dll.setPixelFlavor();
//		logger.info("Setando a capability PixelFlavor, retorno {}", retorno);
		
		if (dll.setPixelTypeBW() ==  SUCCESS){
			return true;
		}
		
		return false;
	}

	@Override
	public boolean setYDPI(int dpi) {
		newTemplate.setResolution(new Tag(String.valueOf(dpi)));
		try {
			marshal(newTemplate, templateFile);
		} catch (JAXBException e) {
			logger.warn("Erro ao gravar a resolução no template", e);
		}
		if (dll.setYDPI(dpi) == SUCCESS){
			return true;
		}
		return false;
	}

	@Override
	public boolean setXDPI(int dpi) {
		newTemplate.setResolution(new Tag(String.valueOf(dpi)));
		try {
			marshal(newTemplate, templateFile);
		} catch (JAXBException e) {
			logger.warn("Erro ao gravar a resolução no template", e);
		}
		if(dll.setXDPI(dpi) == SUCCESS){
			return true;
		}
		return false;
	}

	@Override
	public boolean hasMoreImages() throws ScannerObstructedException, ScannerDoubleFeedException {
		if (imageList.size() > 0){
			return true;
		}
		
		return false;
	}

	@Override
	public boolean endTransfer() {
		if (dll.endTransfer() == SUCCESS){
			return true;
		}
		return false;
	}

	
	private Image getImage() throws ScannerException {
		int point =  imageList.remove(0);
		Pointer p = kernel32.GlobalLock(point);
		
		if(point == 0){
			throw new ScannerImageNotFoundException("Ponteiro retornou 0 na recuperação da imagem");
		}
		
//		Pointer p = new Pointer(point);
		BITMAPINFOHEADER bih = new BITMAPINFOHEADER(p);
		Image img;
		if (bih.biBitCount == 1){		
			img  = ImageUtil.xferDIB1toImage(bih);
		}
		else if(bih.biBitCount == 8){
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

		cmc7 = null;
		if(this.micrEnabled){
			cmc7 = this.getExtendedImageInfo();
		}
		
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
		if (dll.setPixelTypeGRAY() == SUCCESS){
			return true;
		}
		return false;
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

	private String getExtendedImageInfo() {
		int point =   dll.GetExtendedImageInfo();;
		Pointer p = new Pointer(point);
		
		String retorno = null;
		if(p != null){
			retorno = p.getString(0).replaceAll("[^0-9\\s]", "").trim();
		}
					
		return retorno;
	}

	@Override
	public boolean setMicrEnabled(boolean param) {
		if (dll.setMicrEnabled(param) == SUCCESS){
			micrEnabled = true;
			return true;
		}
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
		return this.cmc7;
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

	public List<Scanner> getScanners(){
		dll.openDSM();
		List<Scanner> scanners = new ArrayList<Scanner>(); 
		int retorno = dll.getFirstScanner();
		if (retorno == 0){
			scanners.add(getScanner());
		}

		while(dll.getNextScanner() != END_LIST) {
			scanners.add(getScanner());
		}
		
		dll.closeDSM();
		
		return scanners;
	}
	
	private Scanner getScanner(){
		int pointerScanner = dll.getScannerName();
		Pointer p = new Pointer(pointerScanner);
		
		String name = p.getString(0);
		
		if(name.equalsIgnoreCase("Lexmark Network TWAIN Scan")){
			return new Scanner(DriverScanner.LEXMARK, name, null, null, null);
		} else if (name.equalsIgnoreCase("EC_TWAIN_SOLUTION")) {
			return new Scanner(DriverScanner.HP, name, null, null, null);
		}
		
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
		return (dll.setContrast(value) == SUCCESS);
	}

	@Override
	public boolean setBrightness(int value) {
		return (dll.setBrightness(value) == SUCCESS);
	}

	@Override
	public void setExportJpg(boolean param) {
		this.exportJpg = param;
	}
	
	private void updateDisplay() {
		String usuarioCaptura = ApplicationData.getParam("usuarioCaptura");
		
		try {
			originalTemplate = unmarshal(templateFile);
			newTemplate = (ScanSettingsTemplate) originalTemplate.clone();
			
			newTemplate.getLabelA().setValue(usuarioCaptura);
			newTemplate.getLabelB().setValue(usuarioCaptura);
			
			marshal(newTemplate, templateFile);
		} catch (JAXBException e) {
			e.printStackTrace();
			logger.error("Erro ao realizar o parse do xml {}. {}", templateFile, e.getMessage());
		} catch (CloneNotSupportedException e) {
			logger.error("Erro ao clonar o objeto template");
		}
	}
	
	private void hpShutdownHook() {
		try {
			marshal(originalTemplate, templateFile);
		} catch (JAXBException e) {
			logger.error("Erro ao realizar o hook do xml da HP {}. {}", templateFile, e.getMessage());
		}
	}

	private void marshal(ScanSettingsTemplate template, File templateFile) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ScanSettingsTemplate.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(template, templateFile);
	}

	private ScanSettingsTemplate unmarshal(File templateFile) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ScanSettingsTemplate.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (ScanSettingsTemplate) unmarshaller.unmarshal(templateFile);
	}

	@Override
	public void getImageScannedAssicrono(CentralSalvamentoImagem central)
			throws ScannerException {
		// TODO Auto-generated method stub
		
	}
}	