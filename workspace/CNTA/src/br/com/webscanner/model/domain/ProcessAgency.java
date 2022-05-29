/**
 * 
 */
package br.com.webscanner.model.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Classe respons�vel por armazenar informa��es referentes a local de captura dos documentos/lote.
 * @author Diego
 *
 */
@XStreamAlias("processAgency")
public class ProcessAgency {
	@XStreamAsAttribute
	private int bankCode;
	@XStreamAsAttribute
	private int agencyCaptureCode;
	@XStreamAsAttribute
	private int agencySenderCode;
	@XStreamAsAttribute
	private String userCapture;
	@XStreamAsAttribute
	private String machineName;
	
	public ProcessAgency(){
		this.userCapture = "";
		this.machineName = "";
	}
	
	public int getBankCode() {
		return bankCode;
	}
	public void setBankCode(int bankCode) {
		this.bankCode = bankCode;
	}
	public int getAgencyCaptureCode() {
		return agencyCaptureCode;
	}
	public void setAgencyCaptureCode(int agencyCaptureCode) {
		this.agencyCaptureCode = agencyCaptureCode;
	}
	public int getAgencySenderCode() {
		return agencySenderCode;
	}
	public void setAgencySenderCode(int agencySenderCode) {
		this.agencySenderCode = agencySenderCode;
	}
	public String getUserCapture() {
		return userCapture;
	}
	public void setUserCapture(String userCapture) {
		this.userCapture = userCapture;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}		
}