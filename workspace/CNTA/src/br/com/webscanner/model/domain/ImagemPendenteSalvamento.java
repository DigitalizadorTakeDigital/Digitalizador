package br.com.webscanner.model.domain;

import java.awt.Image;

import br.com.webscanner.model.domain.image.ImageInfo;

public class ImagemPendenteSalvamento {

	private Image imagem;

	private Image verso;

	private String posicao;

	private String formatoArquivoFinal;

	private ImageInfo imagemInfo;

	public Image getImagem() {
		return imagem;
	}

	public void setImagem(Image imagem) {
		this.imagem = imagem;
	}

	public String getPosicao() {
		return posicao;
	}

	public void setPosicao(String posicao) {
		this.posicao = posicao;
	}

	public String getFormatoArquivoFinal() {
		return formatoArquivoFinal;
	}

	public void setFormatoArquivoFinal(String formatoArquivoFinal) {
		this.formatoArquivoFinal = formatoArquivoFinal;
	}

	public ImageInfo getImagemInfo() {
		return imagemInfo;
	}

	public void setImagemInfo(ImageInfo imagemInfo) {
		this.imagemInfo = imagemInfo;
	}

	public Image getVerso() {
		return verso;
	}

	public void setVerso(Image verso) {
		this.verso = verso;
	}

}
