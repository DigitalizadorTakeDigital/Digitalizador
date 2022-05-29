package br.com.webscanner.view;

import java.awt.Insets;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
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

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


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
import br.com.webscanner.util.FileManagement;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.StringEncryptor;
import br.com.webscanner.util.WebscannerUtil;
import br.com.webscanner.view.task.PreProcessTask;

/**
 * Classe de inicialização da aplicação para build Desktop
 * 
 * @author Jonathan Camara
 *
 */
public class MainClientCaptura extends JFrame implements MainViewer {
	
	private static Logger logger = LogManager.getLogger(MainClientCaptura.class
			.getName());
	
	private static final long serialVersionUID = 1L;
	
	private BackgroundPanel backgroundPanel;
	
	private ProductMenuController controller;

	private static final String TEMP_DIR = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "webscanner"
			+ System.getProperty("file.separator") + "dadosUrlProdutos";

	// static{
	// try {
	// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// } catch (InstantiationException e) {
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// e.printStackTrace();
	// } catch (UnsupportedLookAndFeelException e) {
	// e.printStackTrace();
	// }
	// }

	public MainClientCaptura() {
		initComponents();

		// Runtime.getRuntime().addShutdownHook(new Thread(){
		// @Override
		// public void run() {
		// logger.info("Reiniciando o serviço do capthic");
		// try {
		// ServiceUtil.startService(serviceName);
		// } catch (IOException e) {
		// logger.error(e.getMessage());
		// } catch (InterruptedException e) {
		// logger.error(e.getMessage());
		// }
		// }
		// });
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

	public static void main(String[] args) {
		ApplicationData.setBuild(Build.EMBARCADO);

		WebScannerConfig.init();
		inicializeLog4j();
		
		String caminhoArquivo = args[0];
		
//		String caminhoArquivo = "C:\\Users\\M197266\\Desktop\\ADQU01_v20_1547810872247.bgcc";

		String nomeArquivo = WebscannerUtil.getFileName(caminhoArquivo);
		
		String caminhoInstalado = "";
		try {
			caminhoInstalado = Paths.get(MainClientCaptura.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString();
		} catch (URISyntaxException e1) {
			logger.error("Erro ao inicializar o caminho do JAR.");
		}
						
		logger.info("Inicializando aplicativo versao DESENV.");
		try {

			MainClientCaptura mainDesenv = new MainClientCaptura();
			//			
			String produto = null;
			File arquivoGCCD = new File(caminhoArquivo);

			try {
				produto = FileManagement.readFile(caminhoArquivo);
			} catch (IOException e) {
				e.printStackTrace();
			}
//			mainDesenv.parseParam(vida01());
			mainDesenv.parseParam(produto);
			
			try {
				
		//		AtualizacaoEmbarcado.atualizarSistema(nomeArquivo, caminhoInstalado);
				
			} catch (Exception e) {
				
				return;
			}
			
			try {
			//	FileManagement.delete(arquivoGCCD);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
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
//				logger.error("Erro na preinicializacao. {}", e.getMessage());
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



	private static String adqu01() {
		return "?product=ADQU01&token=";
	}
	
	private static String adqu02() {
		return "?product=ADQU02&token=";
	}
	
	private static String adqu03() {
		return "?product=ADQU03&token=";
	}

	private static String corr01() {
		return "?product=CORR01&serv=2&cpfCnpj=32740533845&email=teste%40teste.com&tel=1140020022&cel=11987654321&rgn=2&suc=624&seg=3&token=";
	}

	private static String reem01() {
		return "?product=REEM01&cpfCnpj=12345678901&tipo=NF141001&placa=EKJ2002&sinistro=122212312&token=";
	}

	private static String prev01() {
		// return "?product=PREV01"
		// + "&usuarioCaptura=m172284"
		// + "&codigoProduto=5"
		// + "&numeroProposta=19201061"
		// + "&dataAssinatura=26062018"
		// + "&codigoCorretor=321654"
		// + "&codigoSucursal=9151"
		// + "&codigoAgencia=5678"
		// + "&codigoProposta=12"
		// + "&cpf=29729895899"
		// + "&cnpj=33055146000193"
		// + "&token=";
		return "?product=PREV01" + "&codigoProduto=5"
				+ "&numeroProposta=19201061" + "&cpf=29729895899";
	}

	private static String vida01() {
		// return "?product=VIDA01"
		// + "&usuarioCaptura=m172284"
		// + "&tipoDocumento=PROPOSTA"
		// + "&numeroProposta=19201061"
		// + "&cpf=29729895899"
		// + "&cnpj=33055146000193"
		// + "&codigoCEI=9876"
		// + "&codigoSucursal=9151"
		// + "&codigoCorretor=321654"
		// + "&dataAssinatura=10032018"
		// + "&numeroTermo=1321321321"
		// + "&codigoProdutoVida=1"
		// + "&codigoProdutoComercial=54"
		// + "&origemProposta=12"
		// + "&prefixoProposta=77"
		// + "&canalVenda=1";
		// String url =
		// "?product=VIDA01&usuarioCaptura=m172284&codigoProdutoVida=45&numeroProposta=12051512&tipoDocumento=PROPOSTA&cpf=39967161841&dataAssinatura=28052018&codigoSucursal=841&codigoCorretor=432101";
		// return url;
		return "?product=VIDA01" + "&usuarioCaptura=m172284"
				+ "&numeroProposta=19201061" + "&codigoProdutoVida=19201061"
				+ "&cpf=29729895899";
		// + "&codigoProdutoComercial=54"
		// + "&origemProposta=12"
		// + "&prefixoProposta=77"
		// + "&canalVenda=1";
	}
	
	private static String prev02() {
		// return "?product=PREV01"
		// + "&usuarioCaptura=m172284"
		// + "&codigoProduto=5"
		// + "&numeroProposta=19201061"
		// + "&dataAssinatura=26062018"
		// + "&codigoCorretor=321654"
		// + "&codigoSucursal=9151"
		// + "&codigoAgencia=5678"
		// + "&codigoProposta=12"
		// + "&cpf=29729895899"
		// + "&cnpj=33055146000193"
		// + "&token=";
		return "?product=PREV02" + "&codigoProduto=5"
				+ "&numeroProposta=19201061" + "&cpf=29729895899";
	}
	
	private static String prev03() {
		// return "?product=PREV01"
		// + "&usuarioCaptura=m172284"
		// + "&codigoProduto=5"
		// + "&numeroProposta=19201061"
		// + "&dataAssinatura=26062018"
		// + "&codigoCorretor=321654"
		// + "&codigoSucursal=9151"
		// + "&codigoAgencia=5678"
		// + "&codigoProposta=12"
		// + "&cpf=29729895899"
		// + "&cnpj=33055146000193"
		// + "&token=";
		return "?product=PREV03" + "&codigoProduto=5"
				+ "&numeroProposta=19201061" + "&cpf=29729895899";
	}

	private static String adqu05() {
		return "?product=ADQU05&token=&assinatura=true&numeroApolice=123456&cdTipoDocumento=28&ambiente=DESE";
	}
	private static String adqu06() {
		return "?product=ADQU06&token=&assinatura=false&numeroApolice=123456&cnpj=400555668&cdTipoDocumento=28&nome=felipe&cpf=4000455823&ambiente=DESE";
	}
	
	private static String adqu04() {
		return "?product=ADQU04&token=&ambiente=DESE";
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
			Configurator.initialize("log4j",
					MainClientCaptura.class.getClassLoader(), uri);
			logger = LogManager.getLogger(MainClientCaptura.class.getName());
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
		//Replace para retirar quebra de linha lida do arquivo em 10-08-2018.
		url = url.replace("\r", "").replace("\n", "");
		if (url != null) {
			if (url.indexOf("?") != -1) {
				url = url.substring(url.indexOf("?") + 1);
			} else {
				return;
			}

			StringTokenizer paramGroup = new StringTokenizer(url, "&");

			while (paramGroup.hasMoreTokens()) {
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
		setTitle("BGCC-ClientCaptura");
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
//		setExtendedState(JFrame.MAXIMIZED_BOTH);
		resize(maxBounds.getSize());
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