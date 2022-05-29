package br.com.webscanner.util;

import java.awt.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.exception.FileAlreadyExistsException;
import br.com.webscanner.exception.ScannerException;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.image.ImageInfo;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.model.domain.scanner.impl.kodak.WindowsScanner;
import br.com.webscanner.model.services.AutomaticTypingService;
import br.com.webscanner.view.MainDesenv;

public class TipificacaoAssicrona extends SwingWorker<Void, Void> {

	MainDesenv mainDesenv;
	
	private String caminhoPasta = null;

	private static final Logger LOGGER = LogManager
			.getLogger(WindowsScanner.class.getName());

	private List<URL> images = new ArrayList<URL>();

	private boolean isTerminouDigitalizacao = false;

	private boolean exportJpg;

	private boolean splitDuplex;

	private List<ImageScanned> listaImagemScaneada = new ArrayList<ImageScanned>();

	private ImageScanned imagemEscaneada = null;

	private boolean duplex;

	private List<File> arquivosPendentesExclusao = new ArrayList<File>();

	private ProductScannerController controller;

	public TipificacaoAssicrona(String caminhoPasta, boolean exportJpg,
			boolean splitDuplex, boolean duplex,
			ProductScannerController controller) {
       mainDesenv = new MainDesenv();
		this.caminhoPasta = caminhoPasta;

		this.exportJpg = exportJpg;

		this.splitDuplex = splitDuplex;

		this.duplex = duplex;

		this.controller = controller;

	}

	@Override
	protected Void doInBackground() throws Exception {

		try {
			iniciar();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		super.done();
		return null;
	}

	public void iniciar() throws ScannerException {

		LOGGER.info("Iniciando Tipificação Assicrona");
		
		while (true) {

			//excluirArquivosPendentes();

			// CARREGA AS IMAGENS PENDENTES DE TIPIFICACAO
			verificaArquivosPendentes();

			if (isTerminouDigitalizacao == true && images.isEmpty() == true
					&& arquivosPendentesExclusao.isEmpty()) {

				break;
			}

			boolean isTermino = verificarTerminoDigitalizacao();

			processar(isTermino);

		}
		
		LOGGER.info("Fim da Tipificação Assicrona");

	}

	private void excluirArquivosPendentes() {

		for (int i = 0; i < arquivosPendentesExclusao.size(); i++) {

			File arquivo = arquivosPendentesExclusao.get(i);

			boolean isExcluiu = FileManagement.delete(arquivo);

			if (isExcluiu == true)
				arquivosPendentesExclusao.remove(i);

		}

	}

	private void processar(boolean isAteUltimo) throws ScannerException {

		while (true) {

			if (images.size() == 1 && isTerminouDigitalizacao == false) {

				return;
			}
			if (images.isEmpty())
				return;
//
//			System.out.println("Processando Arquivo: "
//					+ images.get(0).getFile());

			if (arquivoPendenteExclusao(images.get(0).getFile()) != null) {

				LOGGER.info("Arquivo Pendente de Exclusão Encontrado, Arquivo: " + images.get(0).getFile() );
				
				boolean isExcluido = FileManagement.delete(new File(images.get(
						0).getFile()));

				if (isExcluido){
				
					LOGGER.info("Arquivo Pendente de Exclusão Removido com Sucesso! Arquivo: " +images.get(0).getFile() );
					removerPendenteExclusao(images.get(0).getFile());
				
				}
				images.remove(0);

			} else {
				processarArquivo(0);
			}

		}

	}

	private void removerPendenteExclusao(String arquivoRemover) {

		for (int i = 0; i < arquivosPendentesExclusao.size(); i++) {

			File arquivo = arquivosPendentesExclusao.get(i);

			if (new File(arquivoRemover).getAbsolutePath().equalsIgnoreCase(arquivo.getAbsolutePath())) {
				arquivosPendentesExclusao.remove(i);
			}
		}

	}

	private File arquivoPendenteExclusao(String file) {

		for (File arquivo : arquivosPendentesExclusao) {

			if (arquivo.getAbsolutePath().equalsIgnoreCase(
					new File(images.get(0).getFile()).getAbsolutePath())) {
				return arquivo;
			}
		}
		return null;
	}

	private void processarArquivo(int i) throws ScannerException {

		LOGGER.info("Iniciando Tipificação do arquivo");
		imagemEscaneada = tipificar(i);
		
		LOGGER.info("Tipificação Executada");

		
		LOGGER.info("Inciando Visualização do Arquivo");
		// VISUALIZA NA TELA
		carregarVisualizador();
		LOGGER.info("Visualização Encerrada");

		// EXCLUSAO
		/*
		boolean isDeletado = FileManagement.delete(new File(images.get(i)
				.getFile()));

		if (isDeletado == false)
			arquivosPendentesExclusao.add(new File(images.get(i).getFile()));

		 */
		// REMOVE DA LISTA DE PENDENTES
		removerPendentes(i);
		LOGGER.info("Arquivo Removido de Pendentes");

	}

	private void carregarVisualizador() {

		/*
		 * while (scanner.hasMoreImages()) { Document document = new Document(
		 * controller.getDocumentsCount(), controller.getCaptureSequence());
		 * 
		 * document.setContent(scanner.getImageScanned());
		 * document.setDateCapture(new SimpleDateFormat(
		 * "dd/MM/yyyy HH:mm:ss.SSS").format(new Date()));
		 * 
		 * String cmc7 = null; if (isMicrEnable) { cmc7 = scanner.getCmc7(); }
		 * 
		 * AutomaticTypingService.typing(controller.getRecognitions(),
		 * controller.getDocumentModels(), cmc7, document);
		 * 
		 * controller.addDocument(document); controller.setViewerLastDocument();
		 * }
		 */
		MainDesenv.contadorDePaginas ++;
        String produto =  controller.getProduct().getId();
        System.out.println(MainDesenv.contadorDePaginas);
         if (mainDesenv.contadorDePaginas <500 && produto.equals("ADQU02")) {     
        	 TipificarDocumentos(); 
         }else if(mainDesenv.contadorDePaginas >= 500 && produto.equals("ADQU02")){     
        	 CongelaScanner();
    		 JOptionPane.showMessageDialog(null, "Atenção seu lote atingiu o processamento de 500 páginas."
                                           +"\n" 
    				                       +"O mesmo deverá ser finalizado para o início de um novo lote!");
    		 mainDesenv.contadorDePaginas = 0;        	       	  
        	TipificarDocumentos();
         }else {
        	 TipificarDocumentos(); 
         }


	}

	private void removerPendentes(int i) {

		images.remove(i);

	}

	private ImageScanned tipificar(int i) throws ScannerException {
		return getImageScanned(i);
	}

	private Image getImage(int posicao) {

		try {
			return ImageUtil.getFileImage(images.get(posicao));
		} catch (IllegalArgumentException e) {
			LOGGER.error("Erro ao Buscar Imagem: " + e);
			e.printStackTrace();
//			System.out.println("Erro ao recuperar o arquivo : "
//					+ images.get(posicao));
		} catch (IOException e) {
			LOGGER.error("Erro ao Buscar Imagem: " + e);
			e.printStackTrace();
//			System.out.println("Erro ao recuperar o arquivo : "
//					+ images.get(posicao));
		}
		return null;
	}

	public ImageScanned getImageScanned(int i) throws ScannerException {
		//Image image = getImage(i);
		// URL image = images.get(i);
		ImageScanned imageScanned = null;

		try {
			if (this.duplex) {
				if (this.splitDuplex) {
					// ImageInfo frontTIFF = ImageUtil.saveTiff(image, "F");
					ImageInfo frontTIFF = ImageUtil.renomear(new File(images
							.get(i).getFile()), "F");

					imageScanned = new ImageScanned(
							new br.com.webscanner.model.domain.image.Image(
									frontTIFF, null), null);

				} else {
					// ImageInfo frontTIFF = ImageUtil.saveTiff(image, "F");
					ImageInfo frontTIFF = ImageUtil.renomear(new File(images
							.get(i).getFile()), "F");
					ImageInfo rearTIFF = null;

					//image = getImage(i);
					// rearTIFF = ImageUtil.saveTiff(image, "V");
					rearTIFF = ImageUtil.renomear(new File(images.get(i)
							.getFile()), "V");
					imageScanned = new ImageScanned(
							new br.com.webscanner.model.domain.image.Image(
									frontTIFF, rearTIFF), null);

				}
			} else {

				// ImageInfo frontTIFF = ImageUtil.saveTiff(image, "F");
				ImageInfo frontTIFF = ImageUtil.renomear(new File(images.get(i)
						.getFile()), "F");
				ImageInfo frontJPG = null;

				imageScanned = new ImageScanned(
						new br.com.webscanner.model.domain.image.Image(
								frontTIFF, null), null);

			}
			// } catch (FileNotFoundException e) {
			// e.printStackTrace();
			// } catch (NoSuchAlgorithmException e) {
			// e.printStackTrace();
		} catch (IOException | SecurityException | FileAlreadyExistsException e) {
			LOGGER.error("Erro ao Tipificar: " + e);
			e.printStackTrace();
			throw new ScannerException("Erro ao gerar o SHA1 da imagem");
		}
		return imageScanned;
	}

	private boolean verificarTerminoDigitalizacao() {

		return this.isTerminouDigitalizacao;
	}



	public void verificaArquivosPendentes() {

		File caminho = new File(caminhoPasta);

		if (caminho.exists()) {
			File[] imagensPasta = caminho.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					// return name.endsWith("_PENDENTE_TIPIFICACAO.tiff");

					return name.endsWith(".tif");

				}

			});
			LOGGER.info("Imagens TIF encontrados aguardando tipificacao = {}",
					imagensPasta.length);
			this.images.clear();
			for (File imagem : imagensPasta) {
				try {
					images.add(imagem.toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
					LOGGER.error("Erro na URL do Arquivo: " + e);
				}
			}

			/*
			 * Collections.sort(listaPessoas, new Comparator<Pessoa>() {
			 * 
			 * @Override public int compare(Pessoa p1, Pessoa p2) { return
			 * p1.getNome().compareTo(p2.getNome()); }
			 * 
			 * });
			 */

		
			Collections.sort(this.images, new Comparator<URL>() {

				@Override
				public int compare(URL url1, URL url2) {

					String nomeArquivo1 = new File(url1.getFile()).getName().replace("img", "");

					String nomeArquivo2 = new File(url2.getFile()).getName().replace("img", "");;

					long valor1 = Long.valueOf(nomeArquivo1.substring(0,
							nomeArquivo1.indexOf(".")));

					long valor2 = Long.valueOf(nomeArquivo2.substring(0,
							nomeArquivo2.indexOf(".")));

					if (valor1 == valor2) {

						return 0;
					} else if (valor1 > valor2) {

						return 1;
					} else {
						return -1;
					}
				}
			});

			return;

		}

	}

	public boolean isTerminouDigitalizacao() {
		return isTerminouDigitalizacao;
	}

	public void setTerminouDigitalizacao(boolean isTerminouDigitalizacao) {
		this.isTerminouDigitalizacao = isTerminouDigitalizacao;
	}
	
	public void CongelaScanner() {    
		try {
			Runtime.getRuntime().exec("TASKKILL.EXE /F /IM SCANNERDEMO.EXE /T");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void TipificarDocumentos() {
		
		Document document = new Document(controller.getDocumentsCount(),
				controller.getCaptureSequence());

		document.setContent(imagemEscaneada);

		document.setDateCapture(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS")
				.format(new Date()));

		boolean isMicrEnable = true;
		String cmc7 = null;

		AutomaticTypingService.typing(controller.getRecognitions(),
				controller.getDocumentModels(), cmc7, document);

		 String produto =  controller.getProduct().getId();
			
					controller.addDocument(document);
					controller.setViewerLastDocument();
			
	}
	





}
