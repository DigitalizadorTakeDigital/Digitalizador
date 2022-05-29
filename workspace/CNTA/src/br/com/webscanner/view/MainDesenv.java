package br.com.webscanner.view;

import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * Classe de inicialização da aplicação para build Desktop
 * 
 * @author Jonathan Camara
 * 
 */
public class MainDesenv extends JFrame implements MainViewer {
	private static Logger logger = LogManager.getLogger(MainDesenv.class
			.getName());

	private static final long serialVersionUID = 1L;
	private BackgroundPanel backgroundPanel;
	private ProductMenuController controller;
	public static int contadorDePaginas;

	public MainDesenv() {
		initComponents();
	}

	public static String encrypt(String key, String initVector, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(
					initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"),
					"AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());

			return new BigInteger(Base64.encode(encrypted).getBytes())
					.toString();
		} catch (Exception ex) {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
	
		ApplicationData.setBuild(Build.DESENV);

		WebScannerConfig.init();
		inicializeLog4j();

		logger.info("Inicializando aplicativo versao DESENV");
		try {
			MainDesenv mainDesenv = new MainDesenv();
			
			mainDesenv.parseParam(transp01());


			mainDesenv.controller = new ProductMenuController(mainDesenv);

			Splash splash = new Splash(mainDesenv);
			PreProcessTask task = new PreProcessTask();
			task.execute();

			try {
				Product product = task.get();
				if (product != null) {
					mainDesenv.controller.showProductScanner(product);
				} else {
					mainDesenv.controller.showMenu();
				}
				mainDesenv.setVisible(true);
				mainDesenv.toFront();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				Throwable cause = e.getCause();
				logger.error("Erro na preinicializacao. {}", e.getMessage());
				if (cause instanceof InexistentProductException) {
					JOptionPane.showMessageDialog(mainDesenv,
							"Produto não foi encontrado",
							"Produto não foi encontrado",
							JOptionPane.ERROR_MESSAGE);
					System.exit(ApplicationStatus.INEXISTENT_PRODUCT
							.getStatus());
				} else if (cause instanceof PreInitializationException
						|| cause instanceof MissingParametersException
						|| cause instanceof InvalidParametersException) {
					JOptionPane.showMessageDialog(mainDesenv,
							cause.getMessage(),
							"Erro na inicialiazação do produto",
							JOptionPane.ERROR_MESSAGE);
					System.exit(ApplicationStatus.PREINITIALIZATION_ERROR
							.getStatus());
				}
			} finally {
				splash.setVisible(false);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("Parâmetros para inicialização do aplicativo não recebidos");
		}
	}
	
	private static String transp01() { 
		return "?product=TRANSP01&token=&ambiente=DESE";
	}

	private static String transp02() { 
		return "?product=TRANSP02&token=&ambiente=DESE";
	}



	private static void inicializeLog4j() {
		try {
			URI uri = new URI(WebScannerConfig.getProperty("urlLog4j").replace(
					"\\", "/"));
			Configurator.initialize("log4j", MainDesenv.class.getClassLoader(),
					uri);
			logger = LogManager.getLogger(MainDesenv.class.getName());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método recebe uma url e recupera os parâmetros passados na queryString.
	 * 
	 * @param url
	 *            - Url da aplicação.
	 * @throws IOException
	 */
	private void parseParam(String url) {

		logger.info("Recuperando parametros da url: {}", url);
		if (url != null) {
			if (url.indexOf("?") != -1) {
				url = url.substring(url.indexOf("?") + 1);
			} else {
				return;
			}

			StringTokenizer paramGroup = new StringTokenizer(url, "&");

			while (paramGroup.hasMoreTokens()) {
				StringTokenizer tokenizer = new StringTokenizer(
						paramGroup.nextToken(), "=");

				String key = tokenizer.nextToken();
				String value = tokenizer.hasMoreTokens() ? tokenizer
						.nextToken() : "";

				logger.info("Parameter : key = {}; value = {}", key, value);

				ApplicationData.putParam(key, value);
			}

			ApplicationData
					.setProductId(ApplicationData.paramExists("product") ? ApplicationData
							.getParam("product") : null);
			ProcessAgency processAgency = ApplicationData.getProcessAgency();
			processAgency
					.setAgencyCaptureCode(ApplicationData
							.paramExists("agencyCapture") ? Integer
							.parseInt(ApplicationData
									.removeParam("agencyCapture")) : 0);
			processAgency.setAgencySenderCode(ApplicationData
					.paramExists("agencySender") ? Integer
					.parseInt(ApplicationData.removeParam("agencySender")) : 0);
			processAgency
					.setBankCode(ApplicationData.paramExists("bank") ? Integer
							.parseInt(ApplicationData.removeParam("bank")) : 0);
			processAgency.setUserCapture(System.getProperty("user.name"));
			processAgency.setMachineName(StringEncryptor.removerAcentos(System.getenv("computername")));
		}
	}

	private void initComponents() {
		try {
			this.backgroundPanel = new BackgroundPanel(ImageUtil.getImage(
					"background.jpg").getImage());
		} catch (Exception e) {
			logger.error("Erro ao recuperar a imagem background.jpg. Erro: {}",
					e.getMessage());
			e.printStackTrace();
		}

		this.backgroundPanel.setLayout(new FormLayout(
				"$lcgap, default:grow, $lcgap",
				"$lgap, fill:default:grow, $lgap"));
		add(backgroundPanel);

		setIconImage(ImageUtil.getImage("logoKRL.png").getImage());
		setTitle("BGCC Desenv");
		setName("BGCCPRODUCAO");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setUndecorated(true);
		Insets screenInsets = getToolkit().getScreenInsets(
				getGraphicsConfiguration());
		Rectangle screenSize = getGraphicsConfiguration().getBounds();
		Rectangle maxBounds = new Rectangle(screenInsets.left + screenSize.x,
				screenInsets.top + screenSize.y, screenSize.x
						+ screenSize.width - screenInsets.right
						- screenInsets.left, screenSize.y + screenSize.height
						- screenInsets.bottom - screenInsets.top);
//		System.out.println("Estima - " + screenInsets.bottom);
		// setExtendedState(JFrame.MAXIMIZED_BOTH);
		resize(maxBounds.getSize());
//		setResizable(true);
	}

	@Override
	public void showComponent(JComponent component) {
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