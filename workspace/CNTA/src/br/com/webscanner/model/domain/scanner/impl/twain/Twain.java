package br.com.webscanner.model.domain.scanner.impl.twain;
import com.sun.jna.Library;


public interface Twain extends Library  {
	
	int openDSM ();
	
	int getScannerUserSelect (); 
	
	int openScanner () ;
	
	int acquire ();
	
	int getScannerDefault ();
	
	int setAutoScan(Boolean PARAM);
	
	int setDuplex(int PARAM);
	
	 int setIndicators(Boolean PARAM);
	
	int disableDefaultSource () ;
	
	int closeDSM () ;
	
	int setPixelTypeBW();
	
	int setPixelTypeGRAY();
	
	int setXDPI(int dpi);
	
	int setYDPI(int dpi);
	
	int hasMoreImages ();
	
	int endTransfer();
	
	int getImage();
	
	boolean feederLoaded();
	
	boolean isDuplex();
	
	int getStatus();
	
	int GetExtendedImageInfo();
	
	int setMicrEnabled(Boolean param);
	
	int GetMessageK ();
	
	int ProcessTWMessage();
	
	int translate();
	
	int setDeviceEvent();
	
	int getDeviceEvent();
	
	int processEvent();
	
	boolean translateMessage();
	
	boolean getMessage();
	
	int getEvent();
	
	int getFirstScanner();
	
	int getNextScanner();
	
	int getScannerName();
	
	int GetPageNumber();
	
	int setPixelFlavor();
	
	int setXFERMECH();
	
	int setAutomaticSenseMedium();
	
	int getFeederEnabled();
	
	int setFeederEnabled(Boolean param);
	
	int getContrast();
	
	int getContrastMin();
	
	int getContrastMax();
	
	int getContrastCurrent();
	
	int getBrightness();
	
	int getBrightnessMin();
	
	int getBrightnessMax();
	
	int getBrightnessCurrent();
	
	int setContrast(int value);
	
	int setBrightness(int value);
}
