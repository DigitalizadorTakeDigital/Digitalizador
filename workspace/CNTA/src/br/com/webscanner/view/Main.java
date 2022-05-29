/**
 * 
 */
package br.com.webscanner.view;

import java.applet.AppletContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import br.com.webscanner.controller.ProductMenuController;
import br.com.webscanner.exception.InexistentProductException;
import br.com.webscanner.exception.InvalidParametersException;
import br.com.webscanner.exception.MissingParametersException;
import br.com.webscanner.exception.PreInitializationException;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.ApplicationData.Build;
import br.com.webscanner.model.domain.ApplicationStatus;
import br.com.webscanner.model.domain.ProcessAgency;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.util.CommandExecution;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.ServiceUtil;
import br.com.webscanner.util.StringEncryptor;
import br.com.webscanner.util.WebscannerUtil;
import br.com.webscanner.view.task.PreProcessTask;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Classe view principal que inicia a aplicacao.
 * @author Jonathan Camara
 *
 */
public class Main extends JApplet implements MainViewer{
	private static Logger logger;
	private static final long serialVersionUID = 1L;
	private BackgroundPanel backgroundPanel;
	private ProductMenuController controller;
	private String serviceName = "ATHIC_ScannerServiceHost";
	private static AppletContext context;
	private String url = null;
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

	@Override
	public void init() {
		super.init();
		ApplicationData.setBuild(Build.WEB);
		
		context = getAppletContext();
				
		
//		URL documentBase = getDocumentBase();
//		int port = documentBase.getPort() != -1 ? documentBase.getPort() : documentBase.getDefaultPort();  
//		WebScannerConfig.setPath("http://" + documentBase.getHost() + ":" + port + "/" + documentBase.getPath());
		
		String documentBase = "";
		if(getDocumentBase() != null) {
		    String host = getDocumentBase().getHost();
		    String port = "";
		    if (getDocumentBase().getPort() != -1) {
		     port = ":" + String.valueOf(getDocumentBase().getPort());
		    }
		    String path = getDocumentBase().getPath();
		    int lastSlashIndexOf = path.lastIndexOf('/');
		    if (!path.equals("") && lastSlashIndexOf > -1)
		     path = path.substring(0, path.lastIndexOf('/') + 1);
		    
		    documentBase = "http://" + host + port + path;
		    
		   }	
		
		WebScannerConfig.setPath(documentBase);
		
		
		WebScannerConfig.init();
		inicializeLog4j();
		
		logger.info("Iniciando applet versao WEB");
		logger.info("Versão: {}", WebScannerConfig.getImplementationVersion());
		parseParam(getDocumentBase().toString());

		this.controller = new ProductMenuController(this);
		construct();
		
		Splash splash = new Splash(SwingUtilities.windowForComponent(this));
		
		PreProcessTask task = new PreProcessTask();
		task.execute();

		try {
			Product product =  task.get();
			if(product != null) {
				this.controller.showProductScanner(product);
			} else {
				this.controller.showMenu();
			}
			setVisible(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			logger.error("Erro na preinicializacao. {}", e.getMessage());
			Throwable cause = e.getCause();
			if(cause instanceof InexistentProductException){
				JOptionPane.showMessageDialog(this, "Produto não foi encontrado", "Produto não foi encontrado", JOptionPane.ERROR_MESSAGE);
				callJSReturn("returnCall", String.valueOf(ApplicationStatus.INEXISTENT_PRODUCT.getStatus()), null);
				System.exit(ApplicationStatus.INEXISTENT_PRODUCT.getStatus());
			} else if(cause instanceof PreInitializationException || cause instanceof MissingParametersException || cause instanceof InvalidParametersException){
				JOptionPane.showMessageDialog(this, cause.getMessage(), "Erro na inicialiazação do produto", JOptionPane.ERROR_MESSAGE);
				callJSReturn("returnCall", String.valueOf(ApplicationStatus.PREINITIALIZATION_ERROR.getStatus()), null);
				System.exit(ApplicationStatus.PREINITIALIZATION_ERROR.getStatus());
			}
		} finally {
			splash.setVisible(false);
		}
	}

	@Override
	public void start() {
		super.start();
		this.repaint();
	}
	
	private void inicializeLog4j() {
		try {
			URI uri = new URI(WebScannerConfig.getProperty("urlLog4j").replace("\\", "/"));
			Configurator.initialize("config", Main.class.getClassLoader(), uri);
			logger = LogManager.getLogger(Main.class.getName());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Método recebe uma url e recupera os parâmetros passados na queryString.
	 * @param url - Url da aplicação.
	 */
	private void parseParam(String urlParam){
		this.url = urlParam;
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

	/**
	 * Método responsável pela definição do layout do applet.
	 */
	private void construct(){
		try {
			this.backgroundPanel = new BackgroundPanel(ImageUtil.getImage("background.jpg").getImage());
		} catch (Exception e) {
			logger.error("Erro ao recuperar a imagem background.jpg. Erro: {}", e.getMessage());
			e.printStackTrace();
		}

		this.backgroundPanel.setLayout(new FormLayout("$lcgap, default:grow, $lcgap", "$lgap, fill:default:grow, $lgap"));
		this.add(backgroundPanel);
	}

	
	@Override
	public void destroy() {
		super.destroy();
		logger.info("Reiniciando o serviço do capthic");
		try {
			CommandExecution.execute(false, "cmd.exe /C taskkill /im smartsourceManager.exe /f /t");
			CommandExecution.execute(false, "cmd.exe /C taskkill /im smartsourceStart.exe /f /t");
			CommandExecution.execute(false, "cmd.exe /C taskkill /im smartsourceStartup.exe /f /t");
			CommandExecution.execute(false, "cmd.exe /C taskkill /im smartStats.exe /f /t");
			CommandExecution.execute(false, "cmd.exe /C taskkill /im SSExceptionHandler.exe /f /t");
			CommandExecution.execute(false, "cmd.exe /C taskkill /im TrcDump.exe /f /t");

		} catch (IOException e) {
			logger.error("Exception {}", e);
		} catch (InterruptedException e) {
			logger.error("Exception {}", e);
		}
	}

	@Override
	public void showComponent(JComponent component){
		this.backgroundPanel.removeAll();
		this.backgroundPanel.add(component, CC.xy(2, 2));
		this.paintAll(getGraphics());
	}
	
	public static void callJSReturn(final String function, final String ... args) {
		logger.info("Chamando função javascript: {}", function);
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
				@Override
				public Object run() throws Exception {
					try {
						String call = parse(function, args);
						logger.info("Chamada: " + call);
						context.showDocument(new URL(call));
					} catch (MalformedURLException e) {
						logger.error("Erro ao chamar a funcao do javascript. {}", e.getMessage());
					} catch (Exception e) {
						logger.error("Erro ao chamar o javascript", e);
					}					
					return null;
				}
				
				private String parse(String funtion, String ... args) {
					if (WebscannerUtil.isNull(args) || args.length == 0) {
						return "javascript:" + function + "()";
					} else {
						StringBuilder builder = new StringBuilder("javascript:");
						builder.append(function).append("(");
						for (String arg : args) {
							builder.append(arg).append(",");
						}
						builder.setLength(builder.length() - 1);
						builder.append(")");
						return builder.toString();
					}
				}
			});
		} catch (PrivilegedActionException e) {
			logger.error("Erro {}", e);
		} catch (Exception e) {
			logger.error("Erro {}", e);
		}
	}
}
