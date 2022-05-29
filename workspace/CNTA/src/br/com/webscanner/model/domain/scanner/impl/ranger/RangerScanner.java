package br.com.webscanner.model.domain.scanner.impl.ranger;

import br.com.webscanner.exception.DriverNotInstalledException;
import br.com.webscanner.exception.ScannerConfigurationException;
import br.com.webscanner.exception.ScannerDoubleFeedException;
import br.com.webscanner.exception.ScannerException;
import br.com.webscanner.exception.ScannerObstructedException;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.ScannerPropertiesRange;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.model.domain.scanner.Scannable;
import br.com.webscanner.util.CentralSalvamentoImagem;

public class RangerScanner implements Scannable {

	@Override
	public boolean acquire() throws ScannerObstructedException, DriverNotInstalledException, ScannerDoubleFeedException, ScannerConfigurationException {
		return false;
	}

	@Override
	public boolean openDSM() {
		return false;
	}

	@Override
	public boolean getScannerUserSelect() {
		return false;
	}

	@Override
	public boolean setScanner(Scanner scanner) {
		return false;
	}

	@Override
	public boolean openScanner() {
		return false;
	}

	@Override
	public boolean setAutoScan(boolean param) {
		return false;
	}

	@Override
	public boolean setDuplex(boolean param) {
		return false;
	}

	@Override
	public void setSplitDuplex(boolean param) {}

	@Override
	public void setExportJpg(boolean param) {}

	@Override
	public boolean disableDefautSource() {
		return false;
	}

	@Override
	public boolean closeDSM() {
		return false;
	}

	@Override
	public boolean setPixelTypeBW() {
		return false;
	}

	@Override
	public boolean setPixelTypeGRAY() {
		return false;
	}

	@Override
	public boolean setYDPI(int dpi) {
		return false;
	}

	@Override
	public boolean setXDPI(int dpi) {
		return false;
	}

	@Override
	public boolean hasMoreImages() throws ScannerObstructedException, ScannerDoubleFeedException {
		return false;
	}

	@Override
	public boolean endTransfer() {
		return false;
	}

	@Override
	public ImageScanned getImageScanned() throws ScannerException {
		return null;
	}

	@Override
	public boolean feederLoaded() {
		return false;
	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public boolean setIndicators(boolean param) {
		return false;
	}

	@Override
	public String getCmc7() {
		return null;
	}

	@Override
	public boolean setMicrEnabled(boolean param) {
		return false;
	}

	@Override
	public boolean setDeviceEvent() {
		return false;
	}

	@Override
	public int processEvent() {
		return 0;
	}

	@Override
	public boolean translateMessage() {
		return false;
	}

	@Override
	public boolean getMessage() throws ScannerObstructedException, ScannerDoubleFeedException {
		return false;
	}

	@Override
	public void getDeviceEvent() throws ScannerObstructedException, ScannerDoubleFeedException, ScannerException {
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
		return false;
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
	public void getImageScannedAssicrono(CentralSalvamentoImagem central)
			throws ScannerException {
		// TODO Auto-generated method stub
		
	}
}
