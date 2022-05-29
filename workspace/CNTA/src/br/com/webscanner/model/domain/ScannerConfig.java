package br.com.webscanner.model.domain;

public class ScannerConfig {

	private boolean grayScale;
	private boolean doubleSensor;
	private boolean blackPageConfig;
	
	public ScannerConfig() {
	}

	public ScannerConfig(boolean grayScale, boolean doubleSensor, boolean blackPageConfig) {
		this.grayScale = grayScale;
		this.doubleSensor = doubleSensor;
		this.blackPageConfig = blackPageConfig;
	}

	public boolean isGrayScale() {
		return grayScale;
	}

	public void setGrayScale(boolean grayScale) {
		this.grayScale = grayScale;
	}

	public boolean isDoubleSensor() {
		return doubleSensor;
	}

	public void setDoubleSensor(boolean doubleSensor) {
		this.doubleSensor = doubleSensor;
	}

	public boolean isBlackPageConfig() {
		return blackPageConfig;
	}

	public void setBlackPageConfig(boolean blackPageConfig) {
		this.blackPageConfig = blackPageConfig;
	}

}
