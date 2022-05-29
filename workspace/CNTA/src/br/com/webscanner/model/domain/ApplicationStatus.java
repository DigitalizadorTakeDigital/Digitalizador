package br.com.webscanner.model.domain;

public enum ApplicationStatus {
	SUCCESS(0), SCANNER_FAILED(1), EXIT(2), INEXISTENT_PRODUCT(3), PREINITIALIZATION_ERROR(4);
	
	private int status;

	private ApplicationStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
}