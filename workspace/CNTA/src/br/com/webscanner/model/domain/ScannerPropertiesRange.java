/**
 * 
 */
package br.com.webscanner.model.domain;

/**
 * @author fernando.germano
 *
 */
public class ScannerPropertiesRange {
	private int minimumValue;
	private int maximumValue;
	private int currentValue;
	private int defaultValue;
	
	public int getMinimumValue() {
		return minimumValue;
	}
	public void setMinimumValue(int minimumValue) {
		this.minimumValue = minimumValue;
	}
	public int getMaximumValue() {
		return maximumValue;
	}
	public void setMaximumValue(int maximumValue) {
		this.maximumValue = maximumValue;
	}
	public int getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}
	public int getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(int defaultValue) {
		this.defaultValue = defaultValue;
	}
}