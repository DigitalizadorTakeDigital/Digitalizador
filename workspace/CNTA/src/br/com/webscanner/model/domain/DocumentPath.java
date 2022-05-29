package br.com.webscanner.model.domain;

public class DocumentPath {
	
	private String url;
	private int id;
	private String DocumentTitle;
	private int GccNrAgencia;
	private int GccNrConta;
	private int GccCdProcesso;
	private String GccDtContratacao;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDocumentTitle() {
		return DocumentTitle;
	}
	public void setDocumentTitle(String documentTitle) {
		DocumentTitle = documentTitle;
	}
	public int getGccNrAgencia() {
		return GccNrAgencia;
	}
	public void setGccNrAgencia(int gccNrAgencia) {
		GccNrAgencia = gccNrAgencia;
	}
	public int getGccNrConta() {
		return GccNrConta;
	}
	public void setGccNrConta(int gccNrConta) {
		GccNrConta = gccNrConta;
	}
	public int getGccCdProcesso() {
		return GccCdProcesso;
	}
	public void setGccCdProcesso(int gccCdProcesso) {
		GccCdProcesso = gccCdProcesso;
	}
	public String getGccDtContratacao() {
		return GccDtContratacao;
	}
	public void setGccDtContratacao(String gccDtContratacao) {
		GccDtContratacao = gccDtContratacao;
	}
}