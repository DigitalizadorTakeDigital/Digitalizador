/**
 * 
 */
package br.com.webscanner.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.gcc.api.capi.library.CapiScanner;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.ApplicationData.Build;
import br.com.webscanner.model.domain.ProductModel;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.Scanner.DriverScanner;
import br.com.webscanner.model.domain.scanner.impl.twain.TwainScanner;
import br.com.webscanner.util.FileManagement;
import br.com.webscanner.util.xml.XmlUtil;
import br.com.webscanner.view.ProductScanner;
import br.com.webscanner.view.ScannerChooserDialog;

/**
 * @author fernando.germano
 *
 */
public class ScannerChooserController {
	private static Logger  logger = LogManager.getLogger(ScannerChooserController.class.getName());
	private ProductScanner productScanner;
	private ProductScannerController productScannerController;
	
	public ScannerChooserController(ProductScanner productScanner, ProductScannerController productScannerController) {
		this.productScanner = productScanner;
		this.productScannerController = productScannerController;
	}
	
	public List<Scanner> getListScanners() {
		List<Scanner> scanners = new ArrayList<Scanner>();
		
		ProductModel productModel = productScannerController.getProduct().getModel();
		if (productModel.scannerPermitido(DriverScanner.CAPI)) {
			try {
				CapiScanner.getInstance();
				scanners.add(new Scanner(DriverScanner.CAPI, "Scanner Retaguarda", null, null, null));
			} catch(Exception e) {
				logger.error(e.getMessage());
			} catch(Error e) {
				logger.fatal("Não foi possível criar a instância do CAPI. {}", e.getMessage());
			}
		}
				
		if (ApplicationData.getBuild() == Build.DESENV || ApplicationData.paramExists("mock")) {
			scanners.add(new Scanner(DriverScanner.MOCK, "Scanner Mock", null, null, null));
		}
		
		if (productModel.scannerPermitido(DriverScanner.RANGER)) {
			scanners.add(new Scanner(DriverScanner.RANGER, "Scanner Box do Caixa", null, null, null));
		}
		
		TwainScanner twainScanner = new TwainScanner();
		List<Scanner> twainScanners = twainScanner.getScanners();
		
		if (productModel.scannerPermitido(DriverScanner.TWAIN)) {
			for (Scanner scanner : twainScanners) {
				if (scanner.getDriver() == DriverScanner.TWAIN) {
					scanners.add(scanner);
				}
			}
		}
		
		if (productModel.scannerPermitido(DriverScanner.LEXMARK)) {
			for (Scanner scanner : twainScanners) {
				if (scanner.getDriver() == DriverScanner.LEXMARK) {
					scanners.add(scanner);
					break;
				}
			}
		}
		
		if (productModel.scannerPermitido(DriverScanner.HP)) {
			for (Scanner scanner : twainScanners) {
				if (scanner.getDriver() == DriverScanner.HP) {
					scanners.add(scanner);
					break;
				}
			}
		}
		
		if (productModel.scannerPermitido(DriverScanner.SAMSUNG)) {
			for (Scanner scanner : twainScanners) {
				if (scanner.getDriver() == DriverScanner.SAMSUNG) {
					scanners.add(scanner);
					break;
				}
			}
		}

		return scanners;
	}
	
	public Scanner getValidScannerConfigured(){
		File file = new File(System.getProperty("java.io.tmpdir"), "scanner.properties");
		
		if (file.exists()){
			Object object;
			try {
				object = XmlUtil.readXstream(file);
				Scanner scanner = null;
				
				if (object instanceof Scanner){
					scanner = (Scanner) object;
				}
				
				List<Scanner> scanners = getListScanners();
				if (scanners != null){
					if(scanners.contains(scanner)){
						if (scanner.getName().toLowerCase().contains("fi-6770dj")){
							peiFujitsu6770(scanner);
						}
						
//						getScannerProperties(scanner);
						return scanner;
					}
				}
			} catch (Exception e) {
				logger.error("Erro ao ler arquivo {} ", file.getAbsolutePath());
			}
		}
		return null;
	}
	
//	public void getScannerProperties(Scanner scanner){
//		TwainScanner twainScanner = new TwainScanner();
//		
//		ScannerPropertiesRange contrast = null;
//		ScannerPropertiesRange brightness = null;
//			
//		twainScanner.openDSM();
//		
//		twainScanner.setScanner(scanner);
//		
//		boolean isOpen = twainScanner.openScanner();
//				
//		if (isOpen){
//			contrast =  twainScanner.getContrast();
//			brightness = twainScanner.getBrightness();
//			scanner.setBrightness(brightness);
//			scanner.setContrast(contrast);
//		}
//	}
	
	public void showScannerChooser(){
		List<Scanner> scanners = getListScanners();
		logger.info("{} scanners encontrados", scanners.size());
		if (scanners.isEmpty()) {
			JOptionPane.showMessageDialog(this.productScanner, "Não há scanner configurado nesta estação", "Configurações do Scanner", JOptionPane.WARNING_MESSAGE);
			productScannerController.showLoading(false);
		} else {
			new ScannerChooserDialog(this.productScanner, scanners, getValidScannerConfigured(), this.productScannerController);
		}
	}
	
	private void peiFujitsu6770(Scanner scanner){
		logger.info("Iniciando a configuração do Arquivo Fujitsu");
		File folder = new File(System.getProperty("user.home"), "AppData\\Roaming\\Fujitsu\\Fjtwain");
		
		if (folder.exists()){
			String scannerName = scanner.getName().replace("FUJITSU", "").trim();
			String fileText = getString(scannerName, System.getProperty("user.home"));
			
			File[] files = folder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File arg0) {
					if (arg0.getName().toLowerCase().endsWith(".ini")){
						return true;
					}
					return false;
				}
			});
			
			for (File file : files) {

				try {
					FileManagement.write(fileText, file);
					logger.info("Arquivo de configuração do Fujitsu gravado com sucesso {} ", file.getName());
				} catch (IOException e) {
					logger.error("Erro ao gravar arquivo de configurações da Fujtisu: {}", e.getMessage());
				} 
			}
		}
		logger.info("Finalizando a configuração do Arquivo Fujitsu");
	}
	
	private String getString(Object ... params){
		ResourceBundle bundle;
		bundle = ResourceBundle.getBundle("fujitsu-6770");
		String string = bundle.getString("file.text");
		MessageFormat format = new MessageFormat(string);
		return format.format(params);
	}
	
}
