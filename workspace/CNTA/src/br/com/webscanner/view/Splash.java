package br.com.webscanner.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.text.ParseException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.WebscannerUtil;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

public class Splash extends JDialog{

	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger(Splash.class.getName());			
	
	private BackgroundPanel backgroundPanel;
	private JPanel headerPanel;
	private JLabel splashHeaderLabel;
	private JPanel centerPanel;
	private JLabel splashCenterLabel;
	private JPanel footerPanel;
	private JLabel splashFooterLabel;
	
	public Splash(Window owner) {
		super(owner);
		initComponents();
	}
	
	public Splash(Frame owner){
		super(owner);
		initComponents();
	}
	
	private void initComponents() {
		headerPanel = new JPanel();
		splashHeaderLabel = new JLabel();
		centerPanel = new JPanel();
		splashCenterLabel = new JLabel();
		footerPanel = new JPanel();
		splashFooterLabel = new JLabel();
		try {
			backgroundPanel = new BackgroundPanel(ImageUtil.getImage("splashKRL.png").getImage());
		} catch (Exception e) {
			logger.error("Não foi possivel carregar a imagem do Splash. {}", e.getMessage());
		}

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"default:grow",
				"top:33dlu, $lgap, 38dlu, $lgap, 24dlu:grow"));

		//======== headerPanel ========
		{
			headerPanel.setOpaque(false);
			headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 35, 5));

			//---- splashHeaderLabel ----
			splashHeaderLabel.setText("DIGITALIZADOR");
			splashHeaderLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
			splashHeaderLabel.setForeground(Color.white);
			headerPanel.add(splashHeaderLabel);
		}
		contentPane.add(headerPanel, CC.xy(1, 1));

		//======== centerPanel ========
		{
			centerPanel.setOpaque(false);
			centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			//---- splashCenterLabel ----
			splashCenterLabel.setText("Iniciando Aplica\u00e7\u00e3o de Captura de imagens");
			splashCenterLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
			centerPanel.add(splashCenterLabel);
		}
		contentPane.add(centerPanel, CC.xy(1, 3));

		//======== footerPanel ========
		{
			footerPanel.setOpaque(false);
			footerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 25, 10));

			//---- splashFooterLabel ----
			try {
//				splashFooterLabel.setText(WebscannerUtil.maskIt("A.A.AAA", WebScannerConfig.getImplementationVersion()) + " v");
//				Leandro Estima
				splashFooterLabel.setText(WebscannerUtil.maskIt("V "+"AAA", WebScannerConfig.getProperty("versao"))+" - "+ ApplicationData.getParam("ambiente"));
			} catch (ParseException e) {}
			footerPanel.add(splashFooterLabel);
		}
		contentPane.add(footerPanel, CC.xy(1, 5));

		//======== backgroundPanel ========
		{
			backgroundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		}
		contentPane.add(backgroundPanel, CC.xywh(1, 1, 1, 5));
		setLocationRelativeTo(getOwner());
		
		setSize(400, 200);
		setUndecorated(true);
		setModal(true);
		setLocationRelativeTo(null);
		setModalityType(ModalityType.MODELESS);
		setVisible(true);
	}
}
