/**
 * 
 */
package br.com.webscanner.model.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Classe respons�vel por armazenar as informa��es referentes ao scanner.
 * @author Diego
 *
 */
@XStreamAlias("scannerProperties")
public class ScannerProperties {
	@XStreamAsAttribute
	private String serialNumber;
	@XStreamAsAttribute
	private int codeScanner;
	@XStreamAsAttribute
	private int numberDoubleFeed;
	@XStreamAsAttribute
	private int numberCongestion;
	@XStreamAsAttribute
	private int numberIntervention;
	
	public ScannerProperties(){
		this.serialNumber = "000000000";
		this.codeScanner = 0;
		this.numberCongestion = 0;
		this.numberDoubleFeed = 0;
		this.numberIntervention = 0;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public int getCodeScanner() {
		return codeScanner;
	}
	public void setCodeScanner(int codeScanner) {
		this.codeScanner = codeScanner;
	}

	public int getNumberDoubleFeed() {
		return numberDoubleFeed;
	}

	public void setNumberDoubleFeed(int numberDoubleFeed) {
		this.numberDoubleFeed = numberDoubleFeed;
	}

	public int getNumberCongestion() {
		return numberCongestion;
	}

	public void setNumberCongestion(int numberCongestion) {
		this.numberCongestion = numberCongestion;
	}

	public int getNumberIntervention() {
		return numberIntervention;
	}

	public void setNumberIntervention(int numberIntervention) {
		this.numberIntervention = numberIntervention;
	}
}
