package br.com.webscanner.view.task;

import static br.com.webscanner.infra.WebScannerConfig.getProperty;
import static br.com.webscanner.util.FileManagement.createDirectory;
import static br.com.webscanner.util.FileManagement.downloadSystemFile;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.gcc.api.capi.library.CapiReturnValue;
import br.com.gcc.api.capi.library.CapiScanner;
import br.com.gcc.api.scanner.ReturnValue;
import br.com.webscanner.exception.InexistentProductException;
import br.com.webscanner.exception.PreInitializationException;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.domain.MenuProduct;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.model.domain.Scanner.DriverScanner;
import br.com.webscanner.model.domain.preinitialization.PreInitializable;
import br.com.webscanner.model.domain.scanner.BridgeManager;
import br.com.webscanner.util.xml.XmlUtil;

public class PreProcessTask extends SwingWorker<Product, Void>{

	private static final Logger LOGGER = LogManager.getLogger(PreProcessTask.class.getName());
	
//	private static final String PROFESSIONAL = "1";
//	private static final String ADAPTIVE = "2";
	private static final String MICRO_ELITE = "3";
	
	@Override
	protected Product doInBackground() throws Exception, InexistentProductException, PreInitializationException {
		LOGGER.info("Iniciando pre-requisitos operacionais");
		
		LOGGER.info("Versão do java em execucao {}", System.getProperty("java.version"));
		
		LOGGER.info("Realizando download dos arquivos");
		downloadSystemFiles();
		
		LOGGER.info("Carregando DLLs em memoria");
		loadDlls();
		
		LOGGER.info("Apagando imagens da pasta temporária.");
		//cleanTemp();
		
		String productId = ApplicationData.getProductId();
		if(productId != null) {
			List<MenuProduct> menuProducts = XmlUtil.getMenuProducts();
			Collections.sort(menuProducts);
			int index = Collections.binarySearch(menuProducts, new MenuProduct(productId));
			
			if(index < 0){
				LOGGER.error("Produto não foi encontrado. {}", productId);
				throw new InexistentProductException();
			} else {
				LOGGER.info("Realizando leitura do XML do produto: {}", productId);
				Product product = XmlUtil.getProduct(menuProducts.get(index));

				if (product != null) {
					PreInitializable preInitializable = product.getModel().getPreinitializable();
					if(preInitializable != null){
						LOGGER.info("Realizando pré-inicialização.");
						preInitializable.initialize(product);
						try {
							List<DocumentModel> documents = preInitializable.filtroDocumentos(product.getModel().getDocumentModels());
							if(!documents.isEmpty()) {
								product.getModel().setDocumentModels(documents);
							}
						} catch (Exception e) {
							LOGGER.info("O produto não possui filtro de documentos.");
						}
						
					} else {
						LOGGER.info("O produto não possui pré-inicialização.");
					}
					
					LOGGER.info("Selecionando scanner de cheque.");
					
					try {
						CapiScanner scanner = CapiScanner.getInstance();
						scanner.loadConfig(new URL(getProperty("urlcapiconfig")));
						
						LOGGER.info("Abrindo conexao com scanner");
						ReturnValue connectionReturnValue = scanner.openConnection();
						if (connectionReturnValue == CapiReturnValue.SUCCESS) {
							
							String linha = scanner.getTransportInfo("ProductLine");
							
							if (linha.trim().equals(MICRO_ELITE)) {
								product.getModel().getDrivers().remove(DriverScanner.CAPI);
							} else {
								product.getModel().getDrivers().remove(DriverScanner.RANGER);
							}
						} else {
							LOGGER.error("Falha ao abrir conexao com o scanner CAPI {}", connectionReturnValue);
							product.getModel().getDrivers().remove(DriverScanner.CAPI);
							product.getModel().getDrivers().remove(DriverScanner.RANGER);
						}
					} catch (Exception e) {
						product.getModel().getDrivers().remove(DriverScanner.CAPI);
						product.getModel().getDrivers().remove(DriverScanner.RANGER);
						LOGGER.error(e);
					}
					
					return product;
				}
			}
		}
		throw new InexistentProductException();
	}
	
	private void loadDlls() throws PreInitializationException {
		if (!BridgeManager.load()) {
			throw new PreInitializationException("Erro ao carregar as DLL de digitalização. Verificar o LOG.");
		}
	}

	private void downloadSystemFiles(){
		downloadSystemFile(WebScannerConfig.getProperty("urlTwain"));
		downloadSystemFile(WebScannerConfig.getProperty("urlCom4J"));
		downloadSystemFile(WebScannerConfig.getProperty("urlImageIni"));
		downloadSystemFile(WebScannerConfig.getProperty("urlReaderIni"));
		downloadSystemFile(WebScannerConfig.getProperty("urlSnippetIni"));
		downloadSystemFile(WebScannerConfig.getProperty("urlPocketIni"));
		downloadSystemFile(WebScannerConfig.getProperty("urlIqfIni"));
		downloadSystemFile(WebScannerConfig.getProperty("urlCom4Jx86"));
		
		downloadSystemFile(WebScannerConfig.getProperty("urlLiblept168"));
		downloadSystemFile(WebScannerConfig.getProperty("urlTesseract"));
		createDirectory(WebScannerConfig.getProperty("dirTessTraineddata"));
		downloadSystemFile(WebScannerConfig.getProperty("urlTessTraineddata"), System.getProperty("java.io.tmpdir") + WebScannerConfig.getProperty("dirTessTraineddata") + File.separator);
	}
	
	private void cleanTemp() {
		File tempFolder = new File(System.getProperty("user.home"), WebScannerConfig.getProperty("pathImage"));
		File tempFolderExternal = new File(System.getProperty("user.home"), WebScannerConfig.getProperty("pathImage")+File.separator+"externo");
		
		if (tempFolder != null && tempFolder.exists()) {
			for (File image : tempFolder.listFiles()) {
				if (image.isFile()) {
					if (!image.delete()) {
						LOGGER.warn("Não foi possível deletar a imagem {}", image.getName());
					}
				}
			}
		} else if (!tempFolder.exists()){
			tempFolder.mkdirs();
		}
		
		if (tempFolderExternal != null && tempFolderExternal.exists()) {
			for (File image : tempFolderExternal.listFiles()) {
				if (image.isFile()) {
					if (!image.delete()) {
						LOGGER.warn("Não foi possível deletar a imagem {}", image.getName());
					}
				}
			}
		} else if (!tempFolderExternal.exists()){
			tempFolderExternal.mkdirs();
		}
	}
}
