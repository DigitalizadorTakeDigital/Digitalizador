package br.com.webscanner.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.util.ImageUtil;

public class MainUpdate extends JFrame implements ComponentListener{
	
	private static final long serialVersionUID = 1L;
	private static String caminhoArquivo;
	private static String nomeArquivo;
	JLabel fileSizeLabel;
	JLabel headLabel;
	JProgressBar progressBar;
	private BackgroundPanel backgroundPanel;
	private Integer fileSize;
	private static File arquivoBGCC;
	private static URL url;
	
	private static Logger logger = LogManager.getLogger(MainClientCaptura.class
			.getName());
	
	public MainUpdate() {
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error("Erro ao executar o BGCC-Update. Mensagem: "
					+ e.getMessage());
		}
		
		try {
			backgroundPanel = new BackgroundPanel(ImageUtil.getImage("splashKRL.png").getImage());
		} catch (Exception e) {
			logger.error("Não foi possivel carregar a imagem de fundo. {}", e.getMessage());
		}
		
		addComponentListener(this);
		
		fileSizeLabel = new JLabel("00.00 MB",JLabel.CENTER);
		headLabel = new JLabel("BGCC");
		headLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		headLabel.setForeground(Color.white);
		
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("Tahoma", Font.BOLD, 18));
		progressBar.setString("Atualizando");
		
		Container container = getContentPane();
		FormLayout layout = new FormLayout("10dlu,pref,right:160dlu,pref,10dlu",
				"top:33dlu,pref,20dlu,pref,25dlu,pref,27dlu");
		JPanel panel = new JPanel(layout);
		backgroundPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		panel.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		panel.add(headLabel,cc.xyw(1,1,5));
		panel.add(progressBar,cc.xyw(2,3,3));
		panel.add(fileSizeLabel,cc.xywh(3,4,1,1));
		backgroundPanel.add(panel, cc.xy(1,1));
		container.add(backgroundPanel);
		
		setSize(400, 200);
		setUndecorated(true);
		setLocationRelativeTo(null);
		pack();
	}
	
	public static URL getUrl() throws MalformedURLException {
		String caminhoDonwload = "";
		
		String ambiente = ApplicationData.getParam("ambiente");
		
		if(ambiente.equalsIgnoreCase("DESE"))
			
			caminhoDonwload = "wsphttp.dsv.bradseg.com.br/BGCC-ClientCaptura";
		
		else if(ambiente.equalsIgnoreCase("hml"))
		
			caminhoDonwload = "wsphttp.hml.bradseg.com.br/BGCC-ClientCaptura";
		
		else 
			
			caminhoDonwload = "wwws.bradescoseguros.com.br/BGCC-ClientCaptura";
		
		
//		String path = WebScannerConfig.getSourcePath();
		
//		caminhoDonwload = caminhoDonwload.replace(path, "");
		
		URL url = new URL("http://" + caminhoDonwload
				+ "/instalador/BGCC-Atualizacao.zip");
		return url;
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		caminhoArquivo = args[0];
		nomeArquivo = args[1];
		arquivoBGCC = new File(caminhoArquivo+"\\"+nomeArquivo);
		String produto = null;
		try {
			produto = readFile(arquivoBGCC.getAbsolutePath());
			parseParam(produto);
			url = getUrl();
		} catch (IOException e) {
			logger.error("Erro ao fechar o stream de leitura. {}", e.getMessage());
		}

		MainUpdate atualizador = new MainUpdate();
		atualizador.show();
	}
	
	public static String readFile(String filePath) throws IOException {
		logger.info("Iniciando leitura do arquivo {}", filePath);
		BufferedReader bufferedReader = null;
		FileInputStream fileInputStream = null;
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			fileInputStream = new FileInputStream(filePath);
			bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
			String line;
			while((line = bufferedReader.readLine()) != null){
				stringBuilder.append(line);
				stringBuilder.append(System.getProperty("line.separator"));
			}
			logger.info("Termino da leitura de arquivo.");
		} finally{
			if (bufferedReader !=null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar o stream de leitura. {}", e.getMessage());
				}
			}
			if(fileInputStream != null){
				try{
					fileInputStream.close();
				} catch(IOException e){
					logger.error("Erro ao fechar o stream de leitura. {}", e.getMessage());
				}
			}
		}
		return stringBuilder.toString();
	}
	
	private static void parseParam(String url) {

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

				ApplicationData.putParam(key, value);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	final SwingWorker w = new SwingWorker() {

		@Override
		protected Object doInBackground() throws Exception {
			if (getFile(url)) {
				descompactar();
			}
			try {
				Runtime.getRuntime().exec(caminhoArquivo+"\\"+"BGCC-ClientCaptura.exe "+arquivoBGCC.getAbsolutePath());
				System.exit(0);
			} catch (IOException e) {
				logger.error("Erro ao fechar o stream de leitura. {}", e.getMessage());
			}
			System.exit(0);
			return null;
		}
	};
	
	private boolean descompactar() {
		progressBar.setValue(0);
		
		File zipFile = new File(caminhoArquivo+"\\BGCC-Atualizacao.zip");
		

		File folder = new File(caminhoArquivo);
		progressBar.setString("Instalando.");
		progressBar.setMaximum(100);
		try {
			FileInputStream fileInputStream = new FileInputStream(zipFile);
			ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
			ZipEntry zipEntry = null;
			try {
				while((zipEntry = zipInputStream.getNextEntry()) != null) {
					File file = new File(folder.getCanonicalPath(),zipEntry.getName());
//					File file = new File(zipEntry.getName());
					progressBar.setValue(0);
					fileSizeLabel.setText(zipEntry.getName());
					progressBar.setMaximum((int)zipEntry.getSize());
					if (zipEntry.isDirectory()) {
						file.mkdirs();
						continue;
					}
					file.getParentFile().mkdirs();
					OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
					try {
						try {
							final byte[] buf = new byte[1024];
							int bytesRead;
							long nread = 0L;
							
							while(-1 != (bytesRead = zipInputStream.read(buf))) {
								outputStream.write(buf, 0, bytesRead);
								nread += bytesRead;
								progressBar.setValue((int)nread);
								
							}
						} finally {
							outputStream.close();
						}
					} catch (IOException e) {
						file.delete();
						logger.error("Erro ao descompactar o arquivo. {}", e.getMessage());
						throw null;
					}
				}
			
			} finally {
				zipInputStream.close();
			}
		} catch (FileNotFoundException e) {
			logger.error("Erro arquivo zip não encontrado. {}", e.getMessage());
			return false;
		} catch (IOException e) {
			logger.error("Erro no arquivo zip. {}", e.getMessage());
			return false;
		}
		zipFile.delete();
		return true;
	}

	private Double megaByte(int bytes) {
		Double retorno = (double) bytes;
		retorno = (retorno / 1024 / 1024);
		return retorno;
	}
	
	private boolean getFile(URL url) {
		
		try (
				BufferedInputStream in = new BufferedInputStream(url.openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(caminhoArquivo+"\\BGCC-Atualizacao.zip")
			) {
				progressBar.setString("Baixando.");
				progressBar.setValue(0);
				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				Integer downloaFile = 0;
				while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					fileOutputStream.write(dataBuffer, 0, bytesRead);
					downloaFile = downloaFile + bytesRead;
					progressBar.setValue(downloaFile);
					}
				fileOutputStream.close();
				in.close();
				return true;
				
			} catch (IOException e) {
				logger.error("Erro ao fazer o download. {}", e.getMessage());
				return false;
			}
	}
	
	private static int getFileSize(URL url) {
	    URLConnection conn = null;
	    try {
	        conn = url.openConnection();
	        if(conn instanceof HttpURLConnection) {
	            ((HttpURLConnection)conn).setRequestMethod("HEAD");
	        }
	        conn.getInputStream();
	        return conn.getContentLength();
	    } catch (IOException e) {
	    	logger.error("Erro arquivo não encontrado para pegar o tamanho. {}", e.getMessage());
	        throw new RuntimeException(e);
	    } finally {
	        if(conn instanceof HttpURLConnection) {
	            ((HttpURLConnection)conn).disconnect();
	        }
	    }
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
		this.fileSize = getFileSize(url);
		progressBar.setMaximum(fileSize);
		DecimalFormat df2 = new DecimalFormat(".##");
		fileSizeLabel.setText(df2.format(megaByte(this.fileSize))+" MB");
		w.execute();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
