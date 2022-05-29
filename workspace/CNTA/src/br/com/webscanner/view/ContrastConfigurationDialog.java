package br.com.webscanner.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import br.com.webscanner.controller.ContrastConfigurationController;
import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.model.domain.Scanner;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;


public class ContrastConfigurationDialog extends JDialog {
	private ProductScannerController controller;
	private ContrastConfigurationController contrastController;
	private static final int TICK_DIVIDER = 20;
	
	public ContrastConfigurationDialog(ProductScannerController controller, ContrastConfigurationController contrastController) {
		super();
		this.controller = controller;
		this.contrastController = contrastController;
		initComponents();
		
	}
	
	private void EnableDisableComponents(){
		Scanner scanner = this.controller.getScannerSelected();
		if (scanner.getContrast() != null){
			int value = scanner.getContrast().getCurrentValue();
			int minValue = scanner.getContrast().getMinimumValue();
			int maxValue = scanner.getContrast().getMaximumValue();
			
			int total = Math.abs(maxValue - minValue);
			
			sliderContraste.setMinorTickSpacing(total/TICK_DIVIDER);
			sliderContraste.setMaximum(maxValue);
			sliderContraste.setMinimum(minValue);
			
			sliderContraste.setValue(value);
		}else{
			sliderContraste.setEnabled(false);
		}
		
		if (scanner.getBrightness() != null){
			int value = scanner.getBrightness().getCurrentValue();
			int minValue = scanner.getBrightness().getMinimumValue();
			int maxValue = scanner.getBrightness().getMaximumValue();
			int total = Math.abs(maxValue - minValue);
			
			sliderBrilho.setMinorTickSpacing(total/TICK_DIVIDER);
			sliderBrilho.setMaximum(maxValue);
			sliderBrilho.setMinimum(minValue);
			
			sliderBrilho.setValue(value);
		}else{
			sliderBrilho.setEnabled(false);
		}
	}

	private void initComponents() {
		painelBrilho = new JPanel();
		sliderBrilho = new JSlider();
		painelContraste = new JPanel();
		sliderContraste = new JSlider();
		painelSalvar = new JPanel();
		botaoSalvar = new JButton();

		//======== this ========
		setMinimumSize(new Dimension(450, 240));
		setTitle("Configura\u00e7\u00f5es da imagem");
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"default, $lcgap, default:grow, $lcgap, default",
			"2*($lgap), 2*(default, $lgap), default:grow"));

		//======== painelBrilho ========
		{
			painelBrilho.setBorder(new TitledBorder("Brilho:"));

			painelBrilho.setLayout(new BoxLayout(painelBrilho, BoxLayout.X_AXIS));

			//---- sliderBrilho ----
			sliderBrilho.setSnapToTicks(true);
			sliderBrilho.setPaintLabels(true);
			sliderBrilho.setPaintTicks(true);
			sliderBrilho.setMinorTickSpacing(10);
			painelBrilho.add(sliderBrilho);
		}
		contentPane.add(painelBrilho, CC.xy(3, 3));

		//======== painelContraste ========
		{
			painelContraste.setBorder(new TitledBorder("Contraste:"));
			painelContraste.setLayout(new BoxLayout(painelContraste, BoxLayout.X_AXIS));

			//---- sliderContraste ----
			sliderContraste.setSnapToTicks(true);
			sliderContraste.setPaintTicks(true);
			sliderContraste.setPaintLabels(true);
			sliderContraste.setMinorTickSpacing(10);
			painelContraste.add(sliderContraste);
		}
		contentPane.add(painelContraste, CC.xy(3, 5));

		//======== painelSalvar ========
		{
			painelSalvar.setLayout(new BoxLayout(painelSalvar, BoxLayout.Y_AXIS));

			//---- botaoSalvar ----
			botaoSalvar.setText("Salvar");
			botaoSalvar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					contrastController.saveContrastBrightness(sliderContraste.getValue(), sliderBrilho.getValue());
					setVisible(false);
					controller.showLoading(false);
				}
			});
			painelSalvar.add(botaoSalvar);
		}
		contentPane.add(painelSalvar, CC.xy(3, 7, CC.RIGHT, CC.CENTER));
		pack();
		setLocationRelativeTo(getOwner());
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				super.windowClosing(arg0);
				controller.showLoading(false);
			}
		});
		
		setModal(true);
		setLocationRelativeTo(null);
		setResizable(false);
		EnableDisableComponents();
		setVisible(true);
	}
		
	private JPanel painelBrilho;
	private JSlider sliderBrilho;
	private JPanel painelContraste;
	private JSlider sliderContraste;
	private JPanel painelSalvar;
	private JButton botaoSalvar;
}

