package br.com.webscanner.model.services;

import br.com.bradesco.fwop.cliente.api.v1.FwopSessaoEstrutural;

public class FwopSessaoEstruturalImpl implements FwopSessaoEstrutural {
	  private String codigoCanal;
	    private String codigoDependenciaOperante;
	    private String codigoEmpresaOperante;
	    private String codigoFormatoUsuario;
	    private String codigoIdentificadorUsuario;
	    private String codigoIdioma;
	    private String nomeUsuario;
	    private String senhaUsuario;
	                    
	    public FwopSessaoEstruturalImpl(String codigoCanal,
	            String codigoDependenciaOperante,
	            String codigoEmpresaOperante,
	            String codigoFormatoUsuario,
	            String codigoIdentificadorUsuario,
	            String codigoIdioma,
	            String nomeUsuario,
	            String senhaUsuario) {
	        
	        this.codigoCanal = codigoCanal;
	        this.codigoDependenciaOperante = codigoDependenciaOperante;
	        this.codigoEmpresaOperante = codigoEmpresaOperante;
	        this.codigoFormatoUsuario = codigoFormatoUsuario;
	        this.codigoIdentificadorUsuario = codigoIdentificadorUsuario;
	        this.codigoIdioma = codigoIdioma;
	        this.nomeUsuario = nomeUsuario;
	        this.senhaUsuario = senhaUsuario;
	    }

	    public String getCodigoCanal() {
	        return codigoCanal;
	    }

	    public void setCodigoCanal(String codigoCanal) {
	        this.codigoCanal = codigoCanal;
	    }

	    public String getCodigoDependenciaOperante() {
	        return codigoDependenciaOperante;
	    }

	    public void setCodigoDependenciaOperante(String codigoDependenciaOperante) {
	        this.codigoDependenciaOperante = codigoDependenciaOperante;
	    }

	    public String getCodigoEmpresaOperante() {
	        return codigoEmpresaOperante;
	    }

	    public void setCodigoEmpresaOperante(String codigoEmpresaOperante) {
	        this.codigoEmpresaOperante = codigoEmpresaOperante;
	    }

	    public String getCodigoFormatoUsuario() {
	        return codigoFormatoUsuario;
	    }

	    public void setCodigoFormatoUsuario(String codigoFormatoUsuario) {
	        this.codigoFormatoUsuario = codigoFormatoUsuario;
	    }

	    public String getCodigoIdentificadorUsuario() {
	        return codigoIdentificadorUsuario;
	    }

	    public void setCodigoIdentificadorUsuario(String codigoIdentificadorUsuario) {
	        this.codigoIdentificadorUsuario = codigoIdentificadorUsuario;
	    }

	    public String getCodigoIdioma() {
	        return codigoIdioma;
	    }

	    public void setCodigoIdioma(String codigoIdioma) {
	        this.codigoIdioma = codigoIdioma;
	    }

	    public String getNomeUsuario() {
	        return nomeUsuario;
	    }

	    public void setNomeUsuario(String nomeUsuario) {
	        this.nomeUsuario = nomeUsuario;
	    }

	    public String getSenhaUsuario() {
	        return senhaUsuario;
	    }

	    public void setSenhaUsuario(String senhaUsuario) {
	        this.senhaUsuario = senhaUsuario;
	    }

	    public boolean getIndicadorPrimeiraTransacao() {
	        return true;
	    }

	    public String getTicketSeguranca() {
	        return "0";
	    }

	    public void setTicketSeguranca(String string) {
	    }

	    public String getDataExpiracaoTicketSeguranca() {
	        return "";
	    }

	    public void setDataExpiracaoTicketSeguranca(String string) {
	    }

	    public String getDataHoraLocal() {
	        return "2012-02-06T16:20:34.435Z";
	    }

	    public void setDataHoraLocal(String string) {
	    }

	    public String getCodigoIdentificadorSessao() {
	        return "I500334";
	    }

	    public void setCodigoIdentificadorSessao(String string) {
	    }

	    public String getCodigoIdentificadorChaveCriptografia() {
	        return "0";
	    }

	    public void setCodigoIdentificadorChaveCriptografia(String string) {
	    }

	    public String getIndicadorLogout() {
	        return "N";
	    }

	    public void setIndicadorLogout(String string) {
	    }

	    public String getNumeroOperacao() {
	        return "1";
	    }

	    public void setNumeroOperacao(String string) {
	    }

	    public String getCodigoIdentificadorUsuarioAtribuido() {
	        return (this.getCodigoIdentificadorUsuario());
	    }

	    public void setCodigoIdentificadorUsuarioAtribuido(String string) {
	    }

	    public String getCodigoIdentificadorPeriferico() {
	        return "d4253";
	    }

	    public void setCodigoIdentificadorPeriferico(String string) {
	    }

	    public String getIndicadorAutorizacaoAdicional() {
	        return "0";
	    }

	    public void setIndicadorAutorizacaoAdicional(String string) {
	    }
}
