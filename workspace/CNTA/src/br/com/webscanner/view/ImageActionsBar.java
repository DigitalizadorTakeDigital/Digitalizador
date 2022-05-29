package br.com.webscanner.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.media.jai.operator.TransposeDescriptor;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.controller.ImageViewerController;
import br.com.webscanner.controller.ImageViewerController.Type;
import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.model.domain.Content;
import br.com.webscanner.model.domain.image.ImageImported;
import br.com.webscanner.model.domain.image.ImageScanned;

/**
 * Componente que contém os botões de ação referente as imagens.
 * @author Jonathan Camara
 */
@SuppressWarnings("serial")
public class ImageActionsBar extends JPanel {
	
	private static Logger logger = LogManager.getLogger(ImageActionsBar.class.getName());
	
	private ImageViewerController controller;
	private JButton tiff;
	private JButton jpg;
	private JButton switchImage;
	private JButton zoomIn;
	private JButton zoomOut;
	private JButton rotateLeft;
	private JButton rotateRight;
	private JButton realSize;
	private JButton windowSize;
	private JButton openDocument;

	private Type type;
	
	public ImageActionsBar(ProductScannerController productScannerController, ImagePanel imagePanel, JPanel container) {
		this.controller = new ImageViewerController(imagePanel, this, productScannerController);
		construct();
	}
	
	private void reset() {
		tiff.setVisible(false);
		tiff.setEnabled(false);
		jpg.setVisible(false);
		jpg.setEnabled(false);
		switchImage.setVisible(false);
		switchImage.setEnabled(false);
		zoomIn.setVisible(false);
		zoomIn.setEnabled(false);
		zoomOut.setVisible(false);
		zoomOut.setEnabled(false);
		rotateLeft.setVisible(false);
		rotateLeft.setEnabled(false);
		rotateRight.setVisible(false);
		rotateRight.setEnabled(false);
		realSize.setVisible(false);
		realSize.setEnabled(false);
		windowSize.setVisible(false);
		windowSize.setEnabled(false);
		openDocument.setVisible(false);
		openDocument.setEnabled(false);
	}
	
	public void showActionBar(Content content, Type type) {
		reset();
		this.type = type;
		
		switch (type) {
			case TIF:
				enableTifActions((ImageScanned) content);
				break;
			case JPG:
				enableJpgActions((ImageScanned) content);
				break;
			case IMAGE_IMPORTED:				
				enableImageImportedActions((ImageImported) content);
				break;
			case DOCUMENT_IMPORTED:				
				enableDocumentImportedActions();
		}
	}
	
	
	/**
	 * Habilita as ações possíveis para um documento importado
	 */
	private void enableDocumentImportedActions() {
		openDocument.setEnabled(true);
		openDocument.setVisible(true);
	}

	/**
	 * Habilita as ações possíveis para uma imagem importada
	 * @param imported
	 */
	private void enableImageImportedActions(ImageImported imported) {
		boolean hasRear = imported.getImage().getRear() != null;
		boolean imageSmallerThanPanel = controller.isImageSmallerThanPanel();
		boolean hasScaleDefined = controller.hasScaleDefined();
		
		switchImage.setEnabled(hasRear);
		switchImage.setVisible(true);

		zoomIn.setEnabled(true);
		zoomIn.setVisible(true);
		
		zoomOut.setEnabled(imageSmallerThanPanel || hasScaleDefined);
		zoomOut.setVisible(true);
		
		realSize.setEnabled(!imageSmallerThanPanel);
		realSize.setVisible(!imageSmallerThanPanel);
	}

	/**
	 * Habilita as ações possíveis para uma imagem digitalizada com default JPG
	 * @param content
	 */
	private void enableJpgActions(ImageScanned content) {
		boolean hasRear = content.getJpg().getRear() != null;
		boolean imageSmallerThanPanel = controller.isImageSmallerThanPanel();
		boolean hasScaleDefined = controller.hasScaleDefined();
		
		jpg.setVisible(false);
		
		tiff.setEnabled(true);
		tiff.setVisible(true);
		
		switchImage.setEnabled(hasRear);
		switchImage.setVisible(true);

		zoomIn.setEnabled(true);
		zoomIn.setVisible(true);
		
		zoomOut.setEnabled(imageSmallerThanPanel || hasScaleDefined);
		zoomOut.setVisible(true);
		
		rotateLeft.setEnabled(true);
		rotateLeft.setVisible(true);
		
		rotateRight.setEnabled(true);
		rotateRight.setVisible(true);
		
		realSize.setEnabled(!imageSmallerThanPanel);
		realSize.setVisible(!imageSmallerThanPanel);
	}

	/**
	 * Habilita as ações possíveis para uma imagem digitalizada com default TIF
	 * @param imageScanned
	 */
	private void enableTifActions(ImageScanned imageScanned) {
		boolean hasJpg = imageScanned.getJpg() != null;
		boolean hasRear = imageScanned.getTiff().getRear() != null;
		boolean imageSmallerThanPanel = controller.isImageSmallerThanPanel();
		boolean hasScaleDefined = controller.hasScaleDefined();
		
		jpg.setEnabled(hasJpg);
		jpg.setVisible(hasJpg);
		
		switchImage.setEnabled(hasRear);
		switchImage.setVisible(true);

		zoomIn.setEnabled(true);
		zoomIn.setVisible(true);
		
		zoomOut.setEnabled(imageSmallerThanPanel || hasScaleDefined);
		zoomOut.setVisible(true);
		
		rotateLeft.setEnabled(true);
		rotateLeft.setVisible(true);
		
		rotateRight.setEnabled(true);
		rotateRight.setVisible(true);
		
		realSize.setEnabled(!imageSmallerThanPanel);
		realSize.setVisible(!imageSmallerThanPanel);
	}

	public ImageViewerController getImageViewerController() {
		return controller;
	}

	public void setEnabledActions(boolean b) {
		this.setVisible(b);
		this.getParent().repaint();
	}
	
	private void construct(){
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		boolean smallIcons = screenSize.getWidth() > 1000 ? false : true;
		
		if(smallIcons){
			tiff = new TranslucentButton("small/tiffViewerButton.png", "small/tiffViewerButtonOver.png", "Visualizar TIFF");
			jpg = new TranslucentButton("small/jpgViewerButton.png", "small/jpgViewerButtonOver.png", "Visualizar JPG");
			switchImage = new TranslucentButton("small/switchImageButton.png", "small/switchImageButtonOver.png", "Exibir frente/verso");
			zoomIn = new TranslucentButton("small/zoomInButton.png", "small/zoomInButtonOver.png", "Mais Zoom");
			zoomOut = new TranslucentButton("small/zoomOutButton.png", "small/zoomOutButtonOver.png", "Menos Zoom");
			rotateLeft = new TranslucentButton("small/rotateLeftButton.png", "small/rotateLeftButtonOver.png", "Girar à esquerda");
			rotateRight = new TranslucentButton("small/rotateRightButton.png", "small/rotateRightButtonOver.png", "Girar à direita");
			realSize = new TranslucentButton("small/realSizeButton.png", "small/realSizeButtonOver.png", "Tamanho Real");
			windowSize = new TranslucentButton("small/windowSizeButton.png", "small/windowSizeButtonOver.png", "Ajustar a Janela");
			openDocument = new TranslucentButton("small/openDocumentButton.png", "small/openDocumentButtonOver.png", "Abrir documento");
		} else{
			tiff = new TranslucentButton("tiffViewerButton.png", "tiffViewerButtonOver.png", "Visualizar TIFF");
			jpg = new TranslucentButton("jpgViewerButton.png", "jpgViewerButtonOver.png", "Visualizar JPG");
			switchImage = new TranslucentButton("switchImageButton.png", "switchImageButtonOver.png", "Exibir frente/verso");
			zoomIn = new TranslucentButton("zoomInButton.png", "zoomInButtonOver.png", "Mais Zoom");
			zoomOut = new TranslucentButton("zoomOutButton.png", "zoomOutButtonOver.png", "Menos Zoom");
			rotateLeft = new TranslucentButton("rotateLeftButton.png", "rotateLeftButtonOver.png", "Girar à esquerda");
			rotateRight = new TranslucentButton("rotateRightButton.png", "rotateRightButtonOver.png", "Girar à direita");
			realSize = new TranslucentButton("realSizeButton.png", "realSizeButtonOver.png", "Tamanho Real");
			windowSize = new TranslucentButton("windowSizeButton.png", "windowSizeButtonOver.png", "Ajustar a Janela");
			openDocument = new TranslucentButton("openDocumentButton.png", "openDocumentButtonOver.png", "Abrir documento");
		}
		
		setBackground(new Color(255, 255, 255, 0));
		setOpaque(false);
		setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
		
		/**
		 * Botão tif
		 */
		tiff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					controller.showImage(Type.TIF);
				} catch (IOException e) {
					logger.error("Erro ao exibir a imagem TIF. {}", e);
					ImageActionsBar.this.setEnabled(false);
				}
			}
		});
		
		/**
		 * Botão jpg
		 */
		jpg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					controller.showImage(Type.JPG);
				} catch (IOException e) {
					logger.error("Erro ao exibir a imagem JPG. {}", e);
					ImageActionsBar.this.setVisible(false);
				}
			}
		});
		
		/**
		 * Botão switch
		 */
		switchImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.switchImage();
			}
		});
		switchImage.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK), "switchImage");
		switchImage.getActionMap().put("switchImage", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				switchImage.doClick();
			}
		});
		
		/**
		 * Botão Zoom In
		 */
		zoomIn.setActionCommand("zoomIn");
		zoomIn.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, KeyEvent.CTRL_MASK), "zoomIn");
		zoomIn.getActionMap().put("zoomIn", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				zoomIn.doClick();
			}
		});
		zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean zoomEnable = controller.zoomIn();
				
				zoomIn.setEnabled(zoomEnable);
				zoomOut.setEnabled(true);
				
				if(controller.isImageSmallerThanPanel()){
					realSize.setEnabled(true);
					realSize.setVisible(true);
				} else{
					realSize.setVisible(false);
					windowSize.setEnabled(true);
					windowSize.setVisible(true);
				}
			}
		});
		
		/**
		 * Botão Zoom Out
		 */
		zoomOut.setActionCommand("zoomOut");
		zoomOut.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, KeyEvent.CTRL_MASK), "zoomOut");
		zoomOut.getActionMap().put("zoomOut", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				zoomOut.doClick();
			}
		});
		zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean zoomEnable = controller.zoomOut();
				zoomOut.setEnabled(zoomEnable);
				zoomIn.setEnabled(true);
				realSize.setVisible(true);
				if(controller.isImageSmallerThanPanel()){
					realSize.setEnabled(zoomEnable);
					windowSize.setVisible(false);
				} else{
					realSize.setVisible(!zoomEnable);
					windowSize.setVisible(zoomEnable);
				}
			}
		});
		
		/**
		 * Botão rotate left
		 */
		rotateLeft.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.rotateImage(TransposeDescriptor.ROTATE_270);
				try {
					controller.showImage(type);
				} catch (IOException e) {
					logger.error("Erro ao exibir a imagem. {}", e);
					ImageActionsBar.this.setVisible(false);
				}
			}
		});
		rotateLeft.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.CTRL_MASK), "rotateLeft");
		rotateLeft.getActionMap().put("rotateLeft", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				rotateLeft.doClick();
			}
		});
		rotateLeft.setActionCommand("rotateLeft");
		
		/**
		 * Botão rotate right
		 */
		rotateRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.rotateImage(TransposeDescriptor.ROTATE_90);
				try {
					controller.showImage(type);
				} catch (IOException e) {
					logger.error("Erro ao exibir a imagem. {}", e);
					ImageActionsBar.this.setVisible(false);
				}
			}
		});
		rotateRight.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.CTRL_MASK), "rotateRight");
		rotateRight.getActionMap().put("rotateRight", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				rotateRight.doClick();
			}
		});
		rotateRight.setActionCommand("rotateRight");
		
		/**
		 * Botão real size
		 */
		realSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.setImageRealSize();
				if(controller.isImageSmallerThanPanel()){
					realSize.setEnabled(false);
					windowSize.setVisible(false);
					windowSize.setEnabled(false);
					zoomOut.setEnabled(false);
					zoomIn.setEnabled(true);
				} else{
					realSize.setVisible(false);
					windowSize.setVisible(true);
					windowSize.setEnabled(true);
					zoomOut.setEnabled(true);
					zoomIn.setEnabled(true);
				}
			}
		});
		realSize.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK), "realSize");
		realSize.getActionMap().put("realSize", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				realSize.doClick();
			}
		});
		
		/**
		 * Botão window size
		 */
		windowSize.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setImageWindowsSize();
				windowSize.setVisible(false);
				realSize.setVisible(true);
				zoomIn.setEnabled(true);
				if(controller.isImageSmallerThanPanel() || !controller.hasScaleDefined()){
					zoomOut.setEnabled(false);
				} else {
					zoomOut.setEnabled(true);
				}
			}
		});
		windowSize.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.ALT_MASK), "windowSize");
		windowSize.getActionMap().put("windowSize", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				windowSize.doClick();
			}
		});
		
		/**
		 * Botão open document
		 */
		openDocument.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.openDocument();
			}
		});
		
		reset();
		
		add(realSize);
		add(windowSize);
		add(tiff);
		add(jpg);
		add(switchImage);
		add(zoomIn);
		add(zoomOut);
		add(rotateLeft);
		add(rotateRight);
		add(openDocument);
	}
}


