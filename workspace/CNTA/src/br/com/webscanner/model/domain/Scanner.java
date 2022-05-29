/**
 * 
 */
package br.com.webscanner.model.domain;

/**
 * @author fernando.germano
 *
 */

public class Scanner {
	public enum DriverScanner {
		TWAIN, CAPI, RANGER, MOCK, LEXMARK, HP, IMPRESSORA_EXTERNA, SAMSUNG
	}

	private DriverScanner driver;
	private String name;
	private ScannerPropertiesRange contrast;
	private ScannerPropertiesRange brightness;
	private String scannerColor;

	public Scanner(DriverScanner driver, String name, ScannerPropertiesRange contrast,
			ScannerPropertiesRange brightness, String scannerColor) {
		this.driver = driver;
		this.name = name;
		this.contrast = contrast;
		this.brightness = brightness;
		this.scannerColor = scannerColor;
	}

	public void setContrast(ScannerPropertiesRange contrast) {
		this.contrast = contrast;
	}

	public void setBrightness(ScannerPropertiesRange brightness) {
		this.brightness = brightness;
	}

	public ScannerPropertiesRange getContrast() {
		return contrast;
	}

	public ScannerPropertiesRange getBrightness() {
		return brightness;
	}

	public DriverScanner getDriver() {
		return driver;
	}

	public String getName() {
		return name;
	}

	public String getScannerColor() {
		return scannerColor;
	}

	public void setScannerColor(String scannerColor) {
		this.scannerColor = scannerColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((driver == null) ? 0 : driver.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Scanner other = (Scanner) obj;
		if (driver != other.driver)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
