/**
 * 
 */
package br.com.webscanner.model.domain.scanner;

import br.com.webscanner.exception.DriverNotInstalledException;
import br.com.webscanner.exception.ScannerConfigurationException;
import br.com.webscanner.exception.ScannerDoubleFeedException;
import br.com.webscanner.exception.ScannerException;
import br.com.webscanner.exception.ScannerObstructedException;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.ScannerPropertiesRange;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.util.CentralSalvamentoImagem;

/**
 * Interface que define o comportamento de um scanner.
 * @author Jonathan Camara
 *
 */
public interface Scannable {
	
	boolean acquire() throws ScannerObstructedException, DriverNotInstalledException,  ScannerDoubleFeedException, ScannerConfigurationException;
	
	boolean openDSM();
	
	boolean getScannerUserSelect();
	
	boolean setScanner(Scanner scanner);
	
	boolean openScanner();
	
	boolean setAutoScan(boolean param);
	
	boolean setDuplex(boolean param);
	
	void setSplitDuplex(boolean param);
	
	void setExportJpg(boolean param);
	
	boolean disableDefautSource();
	
	boolean closeDSM();
	
	boolean setPixelTypeBW();
	
	boolean setPixelTypeGRAY();
	
	boolean setYDPI(int dpi);
	
	boolean setXDPI(int dpi);
	
	boolean hasMoreImages() throws ScannerObstructedException, ScannerDoubleFeedException;
	
	boolean endTransfer();
	
	ImageScanned getImageScanned() throws ScannerException;
	
	boolean feederLoaded();
	
	int getStatus();
	
	boolean setIndicators(boolean param);
	
	String getCmc7();
	
	boolean setMicrEnabled(boolean param);
	
	boolean setDeviceEvent();
	
	int processEvent();
	
	boolean translateMessage();
	
	boolean getMessage() throws ScannerObstructedException, ScannerDoubleFeedException;
	
	void getDeviceEvent() throws ScannerObstructedException, ScannerDoubleFeedException, ScannerException;
	
	int setXFERMECH();
	
	int setAutomaticSenseMedium();
	
	boolean getFeederEnabled();
	
	int setFeederEnabled(Boolean param);
	
	ScannerPropertiesRange getContrast();
	
	ScannerPropertiesRange getBrightness();
	
	boolean setContrast(int value);
	
	boolean setBrightness(int value);
	
	void getImageScannedAssicrono(CentralSalvamentoImagem central) throws ScannerException;
}