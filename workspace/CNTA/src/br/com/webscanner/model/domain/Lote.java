package br.com.webscanner.model.domain;

import java.io.File;

public final class Lote {

	private String nome;
	private File lot;
	private File pak;
	private File jpk;

	public Lote(String nome, File lot, File pak) {
		this.nome = nome;
		this.lot = lot;
		this.pak = pak;
	}
	
	public Lote(String nome, File lot, File pak, File jpk) {
		this.nome = nome;
		this.lot = lot;
		this.pak = pak;
		this.jpk = jpk;
	}

	public String getNome() {
		return nome;
	}
	public File getLot() {
		return lot;
	}
	public File getPak() {
		return pak;
	}
	public File getJpk() {
		return jpk;
	}
}
