package br.com.webscanner.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import br.com.webscanner.controller.ConfigScannerController;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.ScannerConfig;
import br.com.webscanner.model.domain.typification.BlankPageRecognitionModel;
import br.com.webscanner.view.formattedfields.FormattedPercentField;

public class ConfigScannerDialog extends JDialog {
	private ConfigScannerController controller;

	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel buttonBar;
	private JButton configScannerButton;
	private JCheckBox scannerGrayCheckBox;
	private JCheckBox doubleSensorCheckBox;
	private JLabel blackPageConfigSizeLabel;
	private JLabel blackPageConfigCoverPercentLabel;
	private JFormattedTextField blackPageConfigSize;
	private FormattedPercentField blackPageConfigCoverPercent;
//	private CheckboxGroup checkboxGroup;
	private ScannerConfig scannerConfig;
	private BlankPageRecognitionModel blankPageRecognitionModel;

	public ConfigScannerDialog(ConfigScannerController controller) {
		this.controller = controller;
		initComponents();
		if (controller.getConfigScannerColor() != null) {
			if (controller.getConfigScannerColor().equals("2")) {
				scannerGrayCheckBox.setSelected(true);
				doubleSensorCheckBox.setSelected(true);
			} else if (controller.getConfigScannerColor().equals("3")) {
				scannerGrayCheckBox.setSelected(false);
				doubleSensorCheckBox.setSelected(false);
			} else if (controller.getConfigScannerColor().equals("4")) {
				scannerGrayCheckBox.setSelected(true);
				doubleSensorCheckBox.setSelected(false);
			} else {
				scannerGrayCheckBox.setSelected(false);
				doubleSensorCheckBox.setSelected(true);
			}
		}

	}

	private void configScannerButtonActionPerformed(ActionEvent e) {
		if (scannerGrayCheckBox.isSelected() && doubleSensorCheckBox.isSelected()) {
			controller.setConfigScannerColor("2");
		} else if (scannerGrayCheckBox.isSelected() && !doubleSensorCheckBox.isSelected()) {
			controller.setConfigScannerColor("4");
		} else if (!scannerGrayCheckBox.isSelected() && !doubleSensorCheckBox.isSelected()) {
			controller.setConfigScannerColor("3");
		} else {
			controller.setConfigScannerColor("1");
		}
		controller.setConfigBlankPage(Double.parseDouble(blackPageConfigSize.getValue().toString()),
				(double) blackPageConfigCoverPercent.getValue());
		this.dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Jonathan Camara
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		buttonBar = new JPanel();
//		checkboxGroup = new CheckboxGroup();
		scannerGrayCheckBox = new JCheckBox("Scala de cinza.");
		doubleSensorCheckBox = new JCheckBox("Sensor de folha dupla.");
		blankPageRecognitionModel = controller.getConfigBlankPage();

//		Estima
		blackPageConfigSizeLabel = new JLabel("Qual o tamanho da pagina em bytes para reconhecimento em branco?");
//		blackPageConfigSize = new JTextField(Double.toString(blankPageRecognitionModel.getSize()));
		blackPageConfigSize = new JFormattedTextField(new Integer((int) blankPageRecognitionModel.getSize()));
		blackPageConfigSize.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent evk) {
				char vchar = evk.getKeyChar();

				if(!(Character.isDigit(vchar)) || (vchar == KeyEvent.VK_BACK_SPACE) || (vchar == KeyEvent.VK_DELETE)) {
					evk.consume();
				}
				try {
					int inteiro = Integer.parseInt(blackPageConfigSize.getText());
				} catch (NumberFormatException e) {
				}
			}
		});

		blackPageConfigCoverPercentLabel = new JLabel(
				"Qual o percentual de cobertura da pagina para reconhecimento em branco?");

		blackPageConfigCoverPercent = new FormattedPercentField();
		blackPageConfigCoverPercent.setMaxValue(100.0);
		blackPageConfigCoverPercent.setMinValue(0.0);
		blackPageConfigCoverPercent.setValue(blankPageRecognitionModel.getThreshold());
		

		configScannerButton = new TranslucentButton("dialogOkButton.png", "dialogOkButtonOver.png", "Salvar");
		scannerConfig = controller.getScannerConfig();

		// ======== this ========
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Configura\u00e7\u00e3o do Scanner");
		setModal(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== dialogPane ========
		{
			dialogPane.setBackground(Color.white);
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

			dialogPane.setLayout(new BorderLayout());

			// ======== contentPanel ========
			{
				contentPanel.setBackground(Color.white);
				contentPanel.setLayout(new FormLayout("default:grow",
						"top:default, $lgap, default, $lgap,default, default, default, default"));

				// TODO: Verificar adicao do painel
//				contentPanel.add(filePanel, CC.xy(1, 1));
//				contentPanel.add(filePanel2, CC.xy(1, 3));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			// ======== buttonBar ========
			{
				buttonBar.setBackground(Color.white);
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] { 0, 80 };
				((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] { 1.0, 0.0 };

				// ---- configScannerButton ----
				configScannerButton.setEnabled(true);
				configScannerButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						configScannerButtonActionPerformed(e);
					}
				});
				buttonBar.add(configScannerButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);

			// ---- CheckBox ----
			{
				
				scannerGrayCheckBox.setSelected(false);
				doubleSensorCheckBox.setSelected(true);
				
				String product = ApplicationData.getProductId();
				
				if(product.equals("ADQU02")) {
					scannerGrayCheckBox.setSelected(false);
					doubleSensorCheckBox.setSelected(false);
				}
				
				
			}

			if (scannerConfig.isGrayScale()) {
				contentPanel.add(scannerGrayCheckBox, CC.xy(1, 1));
			}
			if (scannerConfig.isDoubleSensor()) {
				contentPanel.add(doubleSensorCheckBox, CC.xy(1, 3));
			}
			if (scannerConfig.isBlackPageConfig()) {
				contentPanel.add(blackPageConfigSizeLabel, CC.xy(1, 5));
				contentPanel.add(blackPageConfigSize, CC.xy(1, 6));
				contentPanel.add(blackPageConfigCoverPercentLabel, CC.xy(1, 7));
				contentPanel.add(blackPageConfigCoverPercent, CC.xy(1, 8));
			}

		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		setSize(565, 285);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

}
