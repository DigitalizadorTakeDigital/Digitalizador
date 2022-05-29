package br.com.webscanner.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import br.com.webscanner.controller.DocumentImportController;
import br.com.webscanner.infra.i18n.Bundle;
import br.com.webscanner.model.domain.Extension;
import br.com.webscanner.view.model.ExtensionComboBoxModel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;



/**
 * @author Jonathan Camara
 */
@SuppressWarnings("serial")
public class DocumentImportDialog extends JDialog {
	private DocumentImportController controller;
	private List<Extension> extensions;
	private JTextField frontTextField;
	private JTextField rearTextField;
	private JTextField fileTextField;
	
	public DocumentImportDialog(DocumentImportController controller, List<Extension> extensions) {
		this.controller = controller;
		this.extensions = extensions;
		initComponents();
	}

	private void importButtonActionPerformed(ActionEvent e) {
		String front = null;
		String rear = null;
		String file = null;
		
		for(Component component : contentPanel.getComponents()){
			JTextField textField = (JTextField) component.getComponentAt(1,1);
			if(textField.getName().equalsIgnoreCase("Frente")){
				front = textField.getText().isEmpty() ? null : textField.getText();
				this.frontTextField = textField;
			} else if(textField.getName().equalsIgnoreCase("Verso")){
				rear = textField.getText().isEmpty() ? null : textField.getText();
				this.rearTextField = textField;
			} else if(textField.getName().equalsIgnoreCase("Arquivo")){
				file = textField.getText().isEmpty() ? null : textField.getText();
				this.fileTextField = textField;
			}
		}
		
		Extension extension = (Extension) documentTypeCombobox.getSelectedItem();
		
		if(extension.getType().equalsIgnoreCase("image")){
			if(front == null){
				JOptionPane.showMessageDialog(null, Bundle.getString("import.file.required", "Frente"), "Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(extension.isDuplex()){
				if(extension.isBothRequired()){
					if(rear == null){
						JOptionPane.showMessageDialog(null, Bundle.getString("import.file.required", "Verso"), "Erro", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
			
			if(front.equals(rear)){
				JOptionPane.showMessageDialog(null, Bundle.getString("import.image.duplicated"), "Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(!controller.createImportedDocument(front, rear, extension)){
				JOptionPane.showMessageDialog(null, controller.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
				return;
			} else{
				this.dispose();
			}
		}else{
			if(file == null){
				JOptionPane.showMessageDialog(null, Bundle.getString("import.file.required", "Arquivo"), "Erro", JOptionPane.ERROR_MESSAGE);
				return;
			} else{
				if(!controller.createImportedDocument(file, extension)){
					JOptionPane.showMessageDialog(null, controller.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
					return;
				} else{
					this.dispose();
				}
			}
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Jonathan Camara
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		buttonBar = new JPanel();
		importButton = new TranslucentButton("dialogImportButton.png","dialogImportButtonOver.png", "Importar documento");
		documentTypeCombobox = new JComboBox();

		//======== this ========
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Importa\u00e7\u00e3o de Documentos");
		setModal(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBackground(Color.white);
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setBackground(Color.white);
				contentPanel.setLayout(new FormLayout(
					"default:grow",
					"top:default, $lgap, default"));

				//TODO: Verificar adicao do painel
//				contentPanel.add(filePanel, CC.xy(1, 1));
//				contentPanel.add(filePanel2, CC.xy(1, 3));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBackground(Color.white);
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

				//---- importButton ----
				importButton.setEnabled(false);
				importButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						importButtonActionPerformed(e);
					}
				});
				buttonBar.add(importButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);

			//---- documentTypeCombobox ----
			documentTypeCombobox.setBackground(Color.white);
			documentTypeCombobox.setBorder(new TitledBorder("Tipo de Documento"));
			documentTypeCombobox.setModel(createExtensionModel());
			documentTypeCombobox.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					changePanels(arg0);
				}
			});
			if(extensions.size()==1) {
				documentTypeCombobox.setSelectedIndex(1);
				importButton.setEnabled(true);
				createImportFilePanel(extensions.get(0));
			}
			dialogPane.add(documentTypeCombobox, BorderLayout.NORTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		setSize(565, 285);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	
	protected void changePanels(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED){
			JComboBox comboBox = (JComboBox) e.getSource();
			if(comboBox.getSelectedItem() instanceof Extension){
				Extension extension = (Extension)comboBox.getSelectedItem();
				importButton.setEnabled(true);
				createImportFilePanel(extension);
			} else{
				importButton.setEnabled(false);
				this.contentPanel.removeAll();
				contentPanel.revalidate();
				repaint();
			}
		}
	}

	private DefaultComboBoxModel createExtensionModel(){
		return new ExtensionComboBoxModel(extensions);
	}
	
	private void createImportFilePanel(Extension extension){
		if(extension.getType().equalsIgnoreCase("image")){
			this.contentPanel.removeAll();
			contentPanel.add(new FilePanel("Frente", extension), CC.xy(1, 1));
			
			if(extension.isDuplex()){
				contentPanel.add(new FilePanel("Verso", extension), CC.xy(1, 3));
			}
		} else if(extension.getType().equalsIgnoreCase("document")){
			this.contentPanel.removeAll();
			contentPanel.add(new FilePanel("Arquivo", extension), CC.xy(1, 1));
		}
		
		contentPanel.revalidate();
		repaint();
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Jonathan Camara
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel buttonBar;
	private JButton importButton;
	private JComboBox documentTypeCombobox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
