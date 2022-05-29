package br.com.webscanner.util;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.model.domain.ImagemPendenteSalvamento;
import br.com.webscanner.model.domain.image.ImageInfo;
import br.com.webscanner.model.domain.image.ImageScanned;

public class CentralSalvamentoImagem extends Thread {

	private boolean duplex = false;

	private List<ImagemPendenteSalvamento> imagensPendentes = new ArrayList<ImagemPendenteSalvamento>();

	private List<ImageScanned> imagensGravadas = new ArrayList<ImageScanned>();

	private boolean isParar = false;

	private boolean exportJpg;

	private static Logger logger = LogManager
			.getLogger(CentralSalvamentoImagem.class.getName());

	@Override
	public void run() {
		logger.info("Iniciando execução da central de salvamento");
		while (true) {

			for (int i = 0; i < imagensPendentes.size(); i++) {


				logger.info("Salvando imagem numero: " + i);
				
				try {

					salvar(imagensPendentes.get(i));

				} catch (Exception e) {

					logger.error(e);
				}

				imagensPendentes.remove(i);

			}

			if (this.isParar == true){
				logger.info("Parando Execução");
				return;
			}

		}
	}

	private void salvar(ImagemPendenteSalvamento imagemPendenteSalvamento)
			throws NoSuchAlgorithmException, IOException {

		try {

			if (this.duplex == true) {

				duplex(imagemPendenteSalvamento);

			} else {

				ImageInfo frenteTiff = null;

				ImageInfo frenteJpeg = null;

				ImageScanned imagemScaneada = null;

				frenteTiff = ImageUtil.saveTiff(
						imagemPendenteSalvamento.getImagem(), "F");

				if (this.exportJpg) {

					frenteJpeg = ImageUtil.saveJpeg(
							imagemPendenteSalvamento.getImagem(), "F");

					imagemScaneada = new ImageScanned(
							new br.com.webscanner.model.domain.image.Image(
									frenteTiff, null),
							new br.com.webscanner.model.domain.image.Image(
									frenteJpeg, null));

				} else {

					imagemScaneada = new ImageScanned(
							new br.com.webscanner.model.domain.image.Image(
									frenteTiff, null), null);

				}

				imagensGravadas.add(imagemScaneada);

			}

		} catch (FileNotFoundException e) {

			logger.error(e);

		}

	}

	public void duplex(ImagemPendenteSalvamento imagemPendenteSalvamento)
			throws NoSuchAlgorithmException, IOException {

		ImageInfo frenteTiff = null;
		ImageInfo versoTiff = null;

		ImageInfo frenteJpeg = null;
		ImageInfo versoJpeg = null;

		ImageScanned imagemScaneada = null;

		frenteTiff = ImageUtil.saveTiff(imagemPendenteSalvamento.getImagem(),
				"F");
		if(imagemPendenteSalvamento.getVerso() != null)
			versoTiff = ImageUtil
				.saveTiff(imagemPendenteSalvamento.getVerso(), "V");

		if (this.exportJpg) {

			frenteJpeg = ImageUtil.saveJpeg(
					imagemPendenteSalvamento.getImagem(), "F");
			
			if(imagemPendenteSalvamento.getVerso() != null)
			versoJpeg = ImageUtil.saveJpeg(imagemPendenteSalvamento.getVerso(),
					"V");

			imagemScaneada = new ImageScanned(
					new br.com.webscanner.model.domain.image.Image(frenteTiff,
							versoTiff),
					new br.com.webscanner.model.domain.image.Image(frenteJpeg,
							versoJpeg));

		} else {

			imagemScaneada = new ImageScanned(
					new br.com.webscanner.model.domain.image.Image(frenteTiff,
							versoTiff), null);

		}

		imagensGravadas.add(imagemScaneada);
	}

	public void adicionarImagemSalvar(Image imagem, Image verso) {

		ImagemPendenteSalvamento imagemPend = new ImagemPendenteSalvamento();

		imagemPend.setImagem(imagem);
		imagemPend.setVerso(verso);

		imagensPendentes.add(imagemPend);

	}

	@Override
	public void interrupt() {

		this.isParar = true;
		super.interrupt();
	}

	public boolean isDuplex() {
		return duplex;
	}

	public void setDuplex(boolean duplex) {
		this.duplex = duplex;
	}

	public List<ImageScanned> getImagensGravadas() {
		return imagensGravadas;
	}

	public void setImagensGravadas(List<ImageScanned> imagensGravadas) {
		this.imagensGravadas = imagensGravadas;
	}

	public boolean isExportJpg() {
		return exportJpg;
	}

	public void setExportJpg(boolean exportJpg) {
		this.exportJpg = exportJpg;
	}

	public List<ImagemPendenteSalvamento> getImagensPendentes() {
		return imagensPendentes;
	}

	public void setImagensPendentes(List<ImagemPendenteSalvamento> imagensPendentes) {
		this.imagensPendentes = imagensPendentes;
	}
	
	

}
