package br.com.webscanner.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import br.com.webscanner.model.domain.Extension;
import br.com.webscanner.util.FileManagement;

@SuppressWarnings("serial")
public class FilePanel extends JPanel {
	private static JFileChooser chooser;
	private JTextField filePathTextField;
	private JButton fileChooserButton;
	private String label;
	private Extension extension;
	
	static{
		chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}
	
	public FilePanel(String label, Extension extension) {
		this.label = label;
		this.extension = extension;
		construct();
	}

	private void construct() {
		filePathTextField = new JTextField();
		fileChooserButton = new TranslucentButton("dialogSearchButton.png","dialogSearchButtonOver.png", "Procurar documento");
		
		this.setBackground(Color.white);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		//---- filePathTextField ----
		filePathTextField.setName(label);
		filePathTextField.setBackground(Color.white);
		filePathTextField.setBorder(new TitledBorder(label));
		filePathTextField.setEditable(false);
		this.add(filePathTextField);

		//---- fileChooserButton ----
		fileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooser.removeChoosableFileFilter(chooser.getFileFilter());
				chooser.setFileFilter(new ExtensionFileFilter(extension));
				
				if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					filePathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
					chooser.setSelectedFile(new File(""));
				}
			}
		});
		this.add(fileChooserButton);
	}
}

class ExtensionFileFilter extends FileFilter {
	private Extension extension;

	public ExtensionFileFilter(Extension extension) {
		this.extension = extension;
	}

	@Override
	public String getDescription() {
		return extension.getDescription();
	}

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			String ext = FileManagement.getFileExtension(file).toLowerCase();
			if (extension.getExts().contains(ext)) {
				return true;
			}
		}
		return false;
	}
}
