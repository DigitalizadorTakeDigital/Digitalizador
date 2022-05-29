/**
 * 
 */
package br.com.webscanner.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.ScannerPropertiesRange;
import br.com.webscanner.model.domain.scanner.Scannable;
import br.com.webscanner.model.domain.scanner.ScannerFactory;
import br.com.webscanner.util.xml.XmlUtil;
import br.com.webscanner.view.MessagePanel.MessageLevel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author fernando.germano
 *
 */
public class ScannerChooserDialog extends JDialog {
	private static Logger  logger = LogManager.getLogger(ScannerChooserDialog.class.getName());
	private List<Scanner> scanners;
	private Scanner scannerSelected;
	private ProductScannerController controller;
	
	public ScannerChooserDialog(Component parent, List<Scanner> scanners, Scanner scannerSelected, ProductScannerController controller) {
		this.scanners = scanners;
		this.controller = controller;
		this.scannerSelected = scannerSelected;
		initComponents();
		setModal(true);
		setLocationRelativeTo(parent);
		setResizable(false);
		setAlwaysOnTop(true);
		setVisible(true);
	}
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Fernando Germano
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		scrollPanel = new JScrollPane();
		listScanner = new JList();
		buttonBar = new JPanel();
		btnOK = new JButton();
		btnCancel = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"default:grow",
					"fill:default:grow"));

				//======== scrollPanel ========
				{
					//---- listScanner ----
					listScanner.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					DefaultListModel model = new DefaultListModel();
					for (Scanner scanner : this.scanners) {
						model.addElement(scanner);
					}
					
					listScanner.setModel(model);
					if (scannerSelected != null){
						listScanner.setSelectedValue(scannerSelected, true);
					}
					
					scrollPanel.setViewportView(listScanner);
				}
				contentPanel.add(scrollPanel, CC.xy(1, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- btnOK ----
				btnOK.setText("Selecionar");
				buttonBar.add(btnOK, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
				btnOK.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						Object obj =listScanner.getSelectedValue(); 
						if (obj != null){
							if (obj instanceof Scanner){
								scannerSelected = (Scanner) obj;
								getScannerProperties(scannerSelected);
								try {
									XmlUtil.writeXstream(scannerSelected, new File(System.getProperty("java.io.tmpdir"), "scanner.properties"));
									controller.showMessage(new Message(Bundle.getString("scanner.configuration.sucess"),MessageLevel.INFO));
									setVisible(false);
									controller.updateScannerSelection(scannerSelected);
									controller.showLoading(false);
								} catch (Exception ex) {
									logger.error("Erro ao configurar o Scanner: " + ex.getMessage());
									controller.showMessage(new Message(Bundle.getString("scanner.configuration.error"),MessageLevel.ERROR));
								}
							}
						}
					}
				});
				buttonBar.add(btnOK, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				super.windowClosing(arg0);
				controller.showLoading(false);
			}
		});
		
		this.setSize(308, 237);
		this.setTitle("Selecione o scanner padrão");
		
	}
	
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JScrollPane scrollPanel;
	private JList listScanner;
	private JPanel buttonBar;
	private JButton btnOK;
	private JButton btnCancel;
	
	private static void getScannerProperties(Scanner scanner){
		logger.info("Buscando Propriedados de contraste e brilho no Scanner");
		Scannable scannable = null;
		try{
			scannable = ScannerFactory.getInstance(scanner);
			ScannerPropertiesRange contrast = null;
			ScannerPropertiesRange brightness = null;
			scannable.openDSM();
			scannable.setScanner(scanner);
			
			if(scannable.openScanner()){
				contrast =  scannable.getContrast();
				brightness = scannable.getBrightness();
				scanner.setBrightness(brightness);
				scanner.setContrast(contrast);
			}
		}catch (Exception e) {
			logger.error("Exception ao Buscar propriedades do Scanner {}", e.getMessage());
		}catch (Error e){
			logger.error("Erro ao Buscar propriedades do Scanner {}", e.getMessage());
		}finally{
			scannable.disableDefautSource();
			scannable.closeDSM();
		}
	}
}
