package br.com.webscanner.view;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import br.com.webscanner.controller.ProductMenuController;
import br.com.webscanner.exception.InexistentProductException;
import br.com.webscanner.exception.InvalidParametersException;
import br.com.webscanner.exception.MissingParametersException;
import br.com.webscanner.exception.PreInitializationException;
import br.com.webscanner.exception.ScannerUpdateException;
import br.com.webscanner.infra.ScannerUpdate;
import br.com.webscanner.infra.ScannerUpdate.Server;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.ApplicationData.Build;
import br.com.webscanner.model.domain.ApplicationStatus;
import br.com.webscanner.model.domain.ProcessAgency;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.StringEncryptor;
import br.com.webscanner.view.task.PreProcessTask;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Classe de inicialização da aplicação para build Desktop
 * @author Jonathan Camara
 *
 */
public class MainDesktop extends JFrame implements MainViewer{
	private static Logger logger;
	private static final long serialVersionUID = 1L;
	private BackgroundPanel backgroundPanel;
	private ProductMenuController controller;

//	static{

//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
//	}
	
	public MainDesktop() {
		initComponents();
		
//		Runtime.getRuntime().addShutdownHook(new Thread(){
//			@Override
//			public void run() {
//				logger.info("Reiniciando o serviço do capthic");
//				try {
//					CommandExecution.execute(false, "cmd.exe /C taskkill /im smartsourceManager.exe /f /t");
//					CommandExecution.execute(false, "cmd.exe /C taskkill /im smartsourceStart.exe /f /t");
//					CommandExecution.execute(false, "cmd.exe /C taskkill /im smartsourceStartup.exe /f /t");
//					CommandExecution.execute(false, "cmd.exe /C taskkill /im smartStats.exe /f /t");
//					CommandExecution.execute(false, "cmd.exe /C taskkill /im SSExceptionHandler.exe /f /t");
//					CommandExecution.execute(false, "cmd.exe /C taskkill /im TrcDump.exe /f /t");
//
//					ServiceUtil.startService(false, serviceName);
//				} catch (IOException e) {
//					logger.error(e.getMessage());
//				} catch (InterruptedException e) {
//					logger.error(e.getMessage());
//				}
//			}
//		});
	}

	public static void main(String[] args) {
		ApplicationData.setBuild(Build.DESKTOP);
		WebScannerConfig.init();
		inicializeLog4j();
		
		logger.info("Inicializando aplicativo versao DESKTOP");
		logger.info("Versão: {}", WebScannerConfig.getImplementationVersion());

		MainDesktop mainDesktop = new MainDesktop();
		try {
			String ip = WebScannerConfig.getProperty("ip").trim();
			Integer porta = Integer.valueOf(WebScannerConfig.getProperty("porta").trim());
			
			logger.info("Conectando no scanner update pelo ip {} e porta {}", ip, porta);
			Server server = ScannerUpdate.conectar(ip, porta);
			logger.info("Conectado ao servidor de atualizacao com sucesso");
			
			mainDesktop.parseParam(args[0]);
			mainDesktop.controller = new ProductMenuController(mainDesktop);
			
			Splash splash = new Splash(mainDesktop);
			
			PreProcessTask task = new PreProcessTask();
			task.execute();

			try {
				Product product =  task.get();
				if(product != null) {
					mainDesktop.controller.showProductScanner(product);
				} else {
					mainDesktop.controller.showMenu();
				}
				mainDesktop.setVisible(true);
				mainDesktop.toFront();
			} catch (InterruptedException e) {
				logger.error("erro ", e);
			} catch (ExecutionException e) {
				logger.error("Erro na preinicializacao. {}", e);
				Throwable cause = e.getCause();
				if(cause instanceof InexistentProductException){
					JOptionPane.showMessageDialog(mainDesktop, "Produto não foi encontrado", "Produto não foi encontrado", JOptionPane.ERROR_MESSAGE);
					server.respoder(ApplicationStatus.INEXISTENT_PRODUCT);
					System.exit(ApplicationStatus.INEXISTENT_PRODUCT.getStatus());
				} else if(cause instanceof PreInitializationException || cause instanceof MissingParametersException || cause instanceof InvalidParametersException){
					JOptionPane.showMessageDialog(mainDesktop, cause.getMessage(), "Erro na inicialiazação do produto", JOptionPane.ERROR_MESSAGE);
					server.respoder(ApplicationStatus.PREINITIALIZATION_ERROR);
					System.exit(ApplicationStatus.PREINITIALIZATION_ERROR.getStatus());
				}
			} finally {
				splash.setVisible(false);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("Parâmetros para inicialização do aplicativo não recebidos");
		} catch (ScannerUpdateException e) {
			logger.error("Erro ao conectar no servidor de atualizacao", e);
			JOptionPane.showMessageDialog(mainDesktop, "Erro ao conectar no servidor de atualização", "Erro na inicialização", JOptionPane.ERROR_MESSAGE);
			System.exit(ApplicationStatus.PREINITIALIZATION_ERROR.getStatus());
		}
	}
	
	private static void inicializeLog4j() {
		try {
			URI uri = new URI(WebScannerConfig.getProperty("urlLog4j").replace("\\", "/"));
			Configurator.initialize("log4j", MainDesktop.class.getClassLoader(), uri);
			logger = LogManager.getLogger(MainDesktop.class.getName());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Método recebe uma url e recupera os parâmetros passados na queryString.
	 * @param url - Url da aplicação.
	 */
	private void parseParam(String url){
		logger.info("Recuperando parametros da url: {}", url);

		if(url != null){
			if (url.indexOf("?") != -1) {
				url = url.substring(url.indexOf("?") + 1);
			}else{
				return;
			}
		
			StringTokenizer paramGroup = new StringTokenizer(url, "&");
			
			while(paramGroup.hasMoreTokens()){
				StringTokenizer tokenizer = new StringTokenizer(paramGroup.nextToken(), "=");
				
				String key = tokenizer.nextToken();
				String value = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
				
				logger.info("Parameter : key = {}; value = {}", key, value);
				
				ApplicationData.putParam(key, value);
			}
			
			ApplicationData.setProductId(ApplicationData.paramExists("product") ? ApplicationData.getParam("product") : null);
			ProcessAgency processAgency = ApplicationData.getProcessAgency();
			processAgency.setAgencyCaptureCode(ApplicationData.paramExists("agencyCapture") ? Integer.parseInt(ApplicationData.removeParam("agencyCapture")) : 0);
			processAgency.setAgencySenderCode(ApplicationData.paramExists("agencySender") ? Integer.parseInt(ApplicationData.removeParam("agencySender")) : 0);
			processAgency.setBankCode(ApplicationData.paramExists("bank") ? Integer.parseInt(ApplicationData.removeParam("bank")) : 0);
			processAgency.setUserCapture(System.getProperty("user.name"));
			processAgency.setMachineName(StringEncryptor.removerAcentos(System.getenv("computername")));
		}
	}
	
	private void initComponents(){
		try {
			this.backgroundPanel = new BackgroundPanel(ImageUtil.getImage("background.jpg").getImage());
		} catch (Exception e) {
			logger.error("Erro ao recuperar a imagem background.jpg. Erro: {}", e.getMessage());
			e.printStackTrace();
		}

		this.backgroundPanel.setLayout(new FormLayout("$lcgap, default:grow, $lcgap", "$lgap, fill:default:grow, $lgap"));
		add(backgroundPanel);
		
		setTitle("BGCC");
		setName("BGCCPRODUCAO");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setUndecorated(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
//		setAlwaysOnTop(true);
	}

	@Override
	public void showComponent(JComponent component){
		this.backgroundPanel.removeAll();
		this.backgroundPanel.add(component, CC.xy(2, 2));
		this.paintAll(getGraphics());
	}
	
	@Override
	public void toFront() {
		super.toFront();
		
		setAlwaysOnTop(true);
		requestFocus();
		setAlwaysOnTop(false);
	}
}