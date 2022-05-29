package br.com.webscanner.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import br.com.webscanner.controller.ConfigScannerController;
import br.com.webscanner.controller.DocumentImportController;
import br.com.webscanner.controller.MetadataController;
import br.com.webscanner.controller.MoveToController;
import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.controller.ScannerChooserController;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.ApplicationData.Build;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Message;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.WebscannerUtil;
import br.com.webscanner.view.adapter.ScrollPaneAdapter;
import br.com.webscanner.view.adapter.TableActionAdapter;
import br.com.webscanner.view.handler.TableRowTransferHandler;
import br.com.webscanner.view.listener.ClearLotListener;
import br.com.webscanner.view.listener.CloseLotListener;
import br.com.webscanner.view.listener.ComboBoxDocumentSelectionListener;
import br.com.webscanner.view.listener.ConfigScannerListener;
import br.com.webscanner.view.listener.ConfigurationListener;
import br.com.webscanner.view.listener.ContrastConfigurationListener;
import br.com.webscanner.view.listener.DocumentImportListener;
import br.com.webscanner.view.listener.ExitListener;
import br.com.webscanner.view.listener.ScannListener;
import br.com.webscanner.view.listener.TableDocumentSelectionListener;
import br.com.webscanner.view.model.DocumentComboBoxModel;
import br.com.webscanner.view.model.DocumentTableModel;
import br.com.webscanner.view.renderer.ComboBoxRenderer;
import br.com.webscanner.view.renderer.TableRenderer;

/**
 * @author Jonathan Camara
 */
@SuppressWarnings("serial")
public class ProductScanner extends JPanel{
	private static Logger logger = LogManager.getLogger(ProductScanner.class.getName());
	private static final long serialVersionUID = 1L;
	private JPanel leftPanel;
	private JLabel logoLabel;
	private JPanel imagesListPanel;
	private JPanel rightPanel;
	private JPanel menuContainerPanel;
	private JPanel buttonsPanel;
	private JPanel menuPanel;
	private JPanel imageContainerPanel;
	private JPanel documentsPanel;
	private JScrollPane scrollImagePanel;	
	private ImagePanel imagePanel;
	private JComboBox documentsComboBox;
	private JPanel metadataPanel;
	private Product product;
	private JScrollPane tableScrollPane;
	private JTable documentTable;
	private JButton scannButton;
	private JButton closeLotButton;
	private JButton documentImportButton;
	private JButton configScannerButton;
	private JButton clearLotButton;
	private JButton brighnessContrastButton;
	private ImageActionsBar imageActionsBar;
	private LoadingPanel loadingPanel;
	private ProductScannerController productController;
	private ScannerChooserController scannerChooserController;
    private Scanner scannerSelected;
    private JLabel versionLabel;
    private JLabel exportMessageLabel;
	
	public ProductScanner(Product product){
		this.product = product;
		this.productController = new ProductScannerController(product, this);
		this.scannerChooserController = new ScannerChooserController(this, this.productController);
		initComponents();
	}
	
	public Scanner getScannerSelected() {
		if(scannerSelected == null){
			scannerSelected = scannerChooserController.getValidScannerConfigured();
		}
		
		return scannerSelected;
	}
	
	private void initComponents() {
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		
		int metadataAreaSize = screenSize.getWidth() > 1024 ? 35 : 70;
		boolean smallIcons = screenSize.getWidth() > 1024 ? false : true;
		int topContainerSize = smallIcons ? 46 : 50;
		
		
		leftPanel = new JPanel();
		logoLabel = new JLabel();
		imagesListPanel = new BorderPanel();
		tableScrollPane = new JScrollPane();
		
		rightPanel = new JPanel();
		menuContainerPanel = new JPanel();
		buttonsPanel = new JPanel();
		menuPanel = new JPanel();
		imageContainerPanel = new BorderPanel();
		imagePanel = new ImagePanel(product.getModel().getScale());
		documentsPanel = new JPanel();
		metadataPanel = new BorderPanel();
		documentsComboBox = getDocuments();
		
		loadingPanel = new LoadingPanel();
		imageActionsBar = new ImageActionsBar(productController, imagePanel, imageContainerPanel);
		imageActionsBar.setVisible(false);
		
		exportMessageLabel = new JLabel();
		exportMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		exportMessageLabel.setVerticalAlignment(SwingConstants.CENTER);
		
		DocumentTableModel tableModel = new DocumentTableModel(new String[]{"Doc", "Tipo"}, product);
		documentTable = new JTable(tableModel);
		documentTable.setDragEnabled(true);
		documentTable.setDropMode(DropMode.INSERT_ROWS);
		documentTable.setTransferHandler(new TableRowTransferHandler(documentTable));
		documentTable.setDefaultRenderer(Object.class, new TableRenderer());
		documentTable.setRowSelectionAllowed(true);
		documentTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		
		TableColumnModel tableColumnModel = documentTable.getColumnModel();
		tableColumnModel.getColumn(0).setMaxWidth(35);
		tableColumnModel.getColumn(0).setPreferredWidth(35);
		
		documentTable.addMouseListener(new TableActionAdapter(productController));
		ListSelectionModel model = documentTable.getSelectionModel();
		model.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		model.addListSelectionListener(new TableDocumentSelectionListener(documentTable, productController, imageActionsBar.getImageViewerController()));
		
		

		if(smallIcons){
			scannButton = new TranslucentButton("small/scannButton.png", "small/scannButtonOver.png", "Digitalizar", "Clique aqui para iniciar a digitalização");
			closeLotButton = new TranslucentButton("small/closeLotButton.png", "small/closeLotButtonOver.png", "Salvar e Enviar", "Clique aqui para salvar e enviar os documentos");
			documentImportButton = new TranslucentButton("small/documentImport.png", "small/documentImportOver.png", "Importar", "Importar Documento");
			configScannerButton = new TranslucentButton("small/settingsScanButton.png", "small/settingsScanButtonOver.png", "Configurar Scanner", "Configurar Scanner");
			clearLotButton = new TranslucentButton("small/clear.png", "small/clearOver.png", "Apagar Documentos", "Clique aqui para apagar todos os documentos. Para apagar individualmente, clique com o botão direito sobre o formulário e clique em remover");
			brighnessContrastButton = new TranslucentButton("small/brighnessContrast.png", "small/brighnessContrastOver.png", "Brilho e Contraste", "Clique aqui para Ajustar o brilho e contraste");
		} else{
			scannButton = new TranslucentButton("scannButton.png", "scannButtonOver.png", "Digitalizar", "Clique aqui para iniciar a digitalização");
			closeLotButton = new TranslucentButton("closeLotButton.png", "closeLotButtonOver.png", "Salvar e Enviar", "Clique aqui para salvar e enviar os documentos");
			documentImportButton = new TranslucentButton("documentImport.png", "documentImportOver.png", "Importar", "Importar Documento");
			configScannerButton = new TranslucentButton("settingsScanButton.png", "settingsScanButtonOver.png", "Configurar Scanner", "Configurar Scanner");
			clearLotButton = new TranslucentButton("clear.png", "clearOver.png", "Apagar Documentos", "Clique aqui para apagar todos os documentos. Para apagar individualmente, clique com o botão direito sobre o formulário e clique em remover");
			brighnessContrastButton = new TranslucentButton("brighnessContrast.png", "brighnessContrastOver.png", "Brilho e Contraste", "Clique aqui para Ajustar o brilho e contraste");
		}
		
		scannButton.addActionListener(new ScannListener(productController, this.scannerChooserController));
		scannButton.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, KeyEvent.ALT_MASK), "scann");
		scannButton.getActionMap().put("scann", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scannButton.doClick();
			}
		});

		closeLotButton.addActionListener(new CloseLotListener(productController));
		closeLotButton.setEnabled(false);
		closeLotButton.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK), "closeLot");
		closeLotButton.getActionMap().put("closeLot", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeLotButton.doClick();
			}
		});
		
		documentImportButton.addActionListener(new DocumentImportListener(productController));
		documentImportButton.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK), "documentImport");
		documentImportButton.getActionMap().put("documentImport", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				documentImportButton.doClick();
			}
		});
		
		configScannerButton.addActionListener(new ConfigScannerListener(productController));
//		configScannerButton.getInputMap(JOptionPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK), "documentImport");
		configScannerButton.getActionMap().put("configScanner", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				configScannerButton.doClick();
			}
		});
		
		clearLotButton.addActionListener(new ClearLotListener(this, productController));
		
		brighnessContrastButton.addActionListener(new ContrastConfigurationListener(productController, scannerChooserController));
		
		logoLabel.setIcon(ImageUtil.getImage("logoK.png"));
		
		versionLabel = new JLabel();
		versionLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		try {
//			versionLabel.setText(WebscannerUtil.maskIt("GCCD A.A.AAA", WebScannerConfig.getImplementationVersion()) + " v");
//			Leandro Estima
			versionLabel.setText(WebscannerUtil.maskIt("V "+"AAA", WebScannerConfig.getProperty("versao"))+" - "+ ApplicationData.getParam("ambiente"));
		} catch (ParseException e1) {
			logger.error("Erro no parse da mascara da versao do GCCD");
		}
		versionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		{
			setLayout(new FormLayout(
				"$lcgap, 105dlu, $lcgap, default:grow, $lcgap",
				"fill:250dlu:grow, $lgap, fill:"+ metadataAreaSize +"dlu, $lgap"));
			setBackground(new Color(255, 255, 255, 0));
			setOpaque(false);

			//======== leftPanel ========
			{
				leftPanel.setLayout(new FormLayout(
					"10dlu:grow",
					"fill:" + topContainerSize + "dlu, $lgap, fill:default:grow"));
				leftPanel.setBackground(new Color(255, 255, 255, 0));
				leftPanel.setOpaque(false);
				leftPanel.add(logoLabel, CC.xy(1, 1));

					//======== imagesListPanel ========
					{
						imagesListPanel.setBackground(new Color(255, 255, 255, 0));
//						imagesListPanel.setLayout(new BoxLayout(imagesListPanel, BoxLayout.Y_AXIS));
						imagesListPanel.setLayout(new FormLayout("1dlu:grow", "fill:1dlu:grow, $lgap, 8dlu"));
						
						//======== tableScrollPane ========
						{
							tableScrollPane.setViewportView(documentTable);
							tableScrollPane.setOpaque(false);
							tableScrollPane.getViewport().setOpaque(false);
						}
						
						Border border = new EmptyBorder(30, 8, 10, 10);
						imagesListPanel.setBorder(BorderFactory.createTitledBorder(border, product.getName(), TitledBorder.LEFT, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 15)));
						
						imagesListPanel.add(tableScrollPane, CC.xy(1, 1));
						imagesListPanel.add(versionLabel, CC.xy(1, 3));
					}
				leftPanel.add(imagesListPanel, CC.xy(1, 3));
			}
			add(leftPanel, CC.xywh(2, 1, 1, 3));

			//======== rightPanel ========
			{
				rightPanel.setLayout(new FormLayout(
					"177dlu:grow",
					"fill:" + topContainerSize + "dlu, $lgap, fill:124dlu:grow, $lgap, fill:default"));
				rightPanel.setBackground(new Color(255, 255, 255, 0));
				rightPanel.setOpaque(false);

				//======== menuContainerPanel ========
				{
					menuContainerPanel.setLayout(new FormLayout(
						"default:grow, $lcgap, default:grow",
						"bottom:default:grow"));
					menuContainerPanel.setBackground(new Color(255, 255, 255, 0));
					menuContainerPanel.setOpaque(false);
					
					//======== buttonsPanel ========
					{
						buttonsPanel.setBackground(new Color(255, 255, 255, 0));
						buttonsPanel.setOpaque(false);
						buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
						buttonsPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
						
						if(product.getModel().isScannable()){
							buttonsPanel.add(scannButton);
							buttonsPanel.add(Box.createHorizontalStrut(25));
						}
						
						buttonsPanel.add(closeLotButton);
						buttonsPanel.add(Box.createHorizontalStrut(25));

						if(product.getModel().isImportable()){
							buttonsPanel.add(documentImportButton);
							buttonsPanel.add(Box.createHorizontalStrut(25));
						}
						
						if(product.getModel().isScannerConfigurable()){
							buttonsPanel.add(configScannerButton);
							buttonsPanel.add(Box.createHorizontalStrut(25));
						}
						
						buttonsPanel.add(clearLotButton);
						buttonsPanel.add(Box.createHorizontalStrut(25));
						
						if(product.getModel().isBrightness()){
							buttonsPanel.add(brighnessContrastButton);
						}
					}
					menuContainerPanel.add(buttonsPanel, CC.xy(1, 1));
					JPanel panelBotoes;
					//======== menuPanel ========
					{
						panelBotoes = new JPanel();
						panelBotoes.setBackground(new Color(255, 255, 255, 0));
						panelBotoes.setOpaque(false);
						panelBotoes.setLayout(new FormLayout("default:grow","fill:default, bottom:default:grow"));
						panelBotoes.setBorder(BorderFactory.createEmptyBorder());
						
						JPanel pnlConf = new JPanel();
						pnlConf.setBackground(new Color(255, 255, 255, 0));
						pnlConf.setOpaque(false);
						pnlConf.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 5));
						
						JButton configurationButton;
						JButton exitButton;

						if(smallIcons){
							configurationButton = new TranslucentButton("small/configurationButton.png", "small/configurationButtonOver.png", "Configurações do Scanner");
							exitButton = new TranslucentButton("small/exit.png", "small/exit.png", "Sair");
						} else{
							configurationButton = new TranslucentButton("configurationButton.png", "configurationButtonOver.png", "Configurações do Scanner");
							exitButton = new TranslucentButton("exit.png", "exit.png", "Sair");
						}
						
						configurationButton.addActionListener(new ConfigurationListener(this, productController));
						
						exitButton.addActionListener(new ExitListener(this, product));
						
						pnlConf.add(configurationButton);
						
						if(ApplicationData.getBuild() == Build.DESKTOP || ApplicationData.getBuild() == Build.DESENV || ApplicationData.getBuild() == Build.EMBARCADO){
							pnlConf.add(Box.createRigidArea(new Dimension(10, 0)));
							pnlConf.add(exitButton);
						}
						
						panelBotoes.add(pnlConf, CC.xy(1, 1));
						
						menuPanel.setBackground(new Color(255, 255, 255, 0));
						menuPanel.setOpaque(false);
						menuPanel.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));
						menuPanel.setPreferredSize(new Dimension(150,150));
//						imageActionsBar.setBorder(BorderFactory.createLineBorder(Color.blue));
						menuPanel.add(imageActionsBar);
						
						panelBotoes.add(menuPanel, CC.xy(1, 2));
					}
					menuContainerPanel.add(panelBotoes, CC.xy(3, 1));
//					menuContainerPanel.setBorder(BorderFactory.createLineBorder(Color.red));
				}
				rightPanel.add(menuContainerPanel, CC.xy(1, 1));

				//======== imagePanel ========
				{
					imagePanel.setLayout(null);
					
					scrollImagePanel = new JScrollPane();					
					scrollImagePanel.setBackground(new Color(255, 255, 255, 0));
					scrollImagePanel.setOpaque(false);
					scrollImagePanel.setBorder(null);
					scrollImagePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
					scrollImagePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					scrollImagePanel.setViewportView(imagePanel);
					
					ScrollPaneAdapter listener = new ScrollPaneAdapter(imagePanel);
					scrollImagePanel.getViewport().addMouseListener(listener);
					scrollImagePanel.getViewport().addMouseMotionListener(listener);
					
					imageContainerPanel.setBorder(new EmptyBorder(ImagePanel.BORDER_SIZE, ImagePanel.BORDER_SIZE, ImagePanel.BORDER_SIZE, ImagePanel.BORDER_SIZE));
					imageContainerPanel.setBackground(new Color(255, 255, 255, 0));
					imageContainerPanel.setOpaque(false);
					//imageContainerPanel.setLayout(new BoxLayout(imageContainerPanel, BoxLayout.PAGE_AXIS));
					imageContainerPanel.setLayout(new FormLayout("center:1dlu:grow", "1dlu:grow"));
					imageContainerPanel.add(scrollImagePanel, CC.xy(1, 1));
				}
				rightPanel.add(imageContainerPanel, CC.xy(1, 3));

				//======== documentsPanel ========
				{
					documentsPanel.setBackground(new Color(255, 255, 255, 0));
					documentsPanel.setOpaque(false);
					documentsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
					documentsPanel.add(documentsComboBox);
				}
				rightPanel.add(documentsPanel, CC.xy(1, 5));
			}
			add(rightPanel, CC.xy(4, 1));

			//======== metadataPanel ========
			{
				metadataPanel.setBackground(new Color(255, 255, 255, 0));
				metadataPanel.setOpaque(false);
				metadataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//				metadataPanel.setLayout(new BoxLayout(metadataPanel, BoxLayout.X_AXIS));
				metadataPanel.setBorder(new EmptyBorder(0, 12, 0, 12));
			}
			add(metadataPanel, CC.xy(4, 3));
		}
	}
	
	private JComboBox getDocuments(){
		DocumentComboBoxModel model = new DocumentComboBoxModel(product.getModel().getDocumentModels());
		model.setSelectedItem("Selecione");
		JComboBox documents = new JComboBox(model);
		documents.addItemListener(new ComboBoxDocumentSelectionListener(this.productController, new MetadataController(metadataPanel)));
		
		documents.setRenderer(new ComboBoxRenderer());
		documents.setBackground(Color.white);
		
		documents.setEnabled(false);
		
		return documents;
	}
	
	public void enableDocumentSelection(boolean flag){
		this.documentsComboBox.setEnabled(flag);
	}
	
	public void updateLeftPanel(){
		this.leftPanel.repaint();
		this.leftPanel.revalidate();
	}

	public void clearDocumentTableSelection(){
		this.documentTable.clearSelection();
	}
	
	public void updateTable() {
		this.documentTable.repaint();
		this.documentTable.revalidate();		
	}
	
	public void setViewerLastDocument() {
		updateTable();
		if (documentTable.getRowCount() > 0) {
			documentTable.setRowSelectionInterval(documentTable.getRowCount() - 1, documentTable.getRowCount() -1);
			documentTable.scrollRectToVisible(documentTable.getCellRect(documentTable.getRowCount()-1, 0, true));
		} 
	}

	public void updateDocumentsCombobox() {
		Document document = this.productController.getActualDocument();
		
		DocumentComboBoxModel model = (DocumentComboBoxModel) this.documentsComboBox.getModel();
		
		model.setSelectedItem("Selecione");
		if(document != null){
			if(document.getName() != null){
				model.setSelectedItem(document);
			}
		}else{
			documentsComboBox.setEnabled(false);
		}
		
		this.documentsComboBox.repaint();
		this.documentsComboBox.revalidate();
	}
	
	public void updateMessage(String message) {
		exportMessageLabel.setText(message);
	}
	
	public void showLoading(String message) {
		
//		clearMessages();
		
		Object object = getParent().getParent().getParent().getParent();
		if(object instanceof JRootPane){
			JRootPane rootPane = (JRootPane) object; 
			
			JPanel messagePanel = new JPanel(new BorderLayout());
			messagePanel.setBackground(Color.white);
			messagePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			messagePanel.setPreferredSize(new Dimension(400, 100));

			exportMessageLabel.setText(message);
			messagePanel.add(exportMessageLabel);
			
			loadingPanel.setLayout(new GridBagLayout());
			loadingPanel.add(messagePanel);
			
			JPanel glassPane = (JPanel) rootPane.getGlassPane();
			glassPane.setVisible(true);
			glassPane.setLayout(new BorderLayout());
			glassPane.add(loadingPanel, BorderLayout.CENTER);
			loadingPanel.requestFocus();
			
			glassPane.repaint();
			glassPane.revalidate();
		}
	}
	
	public void showLoading(boolean flag){
		JRootPane rootPane = getRootPane();
		JPanel glassPane = (JPanel) rootPane.getGlassPane();
		
		if(flag){
//			clearMessages();
			
			glassPane.setVisible(flag);
			glassPane.setLayout(new BorderLayout());
			glassPane.add(loadingPanel, BorderLayout.CENTER);
			loadingPanel.requestFocus();
		}else{
			loadingPanel.removeAll();
			glassPane.remove(loadingPanel);
			
			if(glassPane.getComponentCount() == 0){
				glassPane.setVisible(flag);
			}
		}
		
		glassPane.repaint();
		glassPane.revalidate();
	}
	
	public void showMessage(Message message){
		JRootPane rootPane = getRootPane(); 
		JPanel glassPane = (JPanel) rootPane.getGlassPane();
		
		MessagePanel messagePanel = new MessagePanel(message);
		if(!Arrays.asList(glassPane.getComponents()).contains(messagePanel)){
			glassPane.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 20));
			glassPane.add(messagePanel);
			glassPane.setVisible(true);
			glassPane.repaint();
			glassPane.revalidate();
		}
	}
	
//	private void clearMessages(){
//		JRootPane root = getRootPane();
//		JPanel glassPanel = (JPanel) root.getGlassPane();
//		glassPanel.removeAll();
//	}
	
	public void clearImagePanel() {
		imageActionsBar.setVisible(false);
		scrollImagePanel.setVisible(false);
		imageContainerPanel.repaint();
		imageContainerPanel.revalidate();
	}

	public void showImportDialog() {
		showLoading(true);
		DocumentImportDialog dialog = new DocumentImportDialog(new DocumentImportController(productController), product.getModel().getExtensions());
		dialog.setVisible(true);
		
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				showLoading(false);
			}
		});
	}
	
	public void showConfigScannerDialog() {
		showLoading(true);
		ConfigScannerDialog dialog = new ConfigScannerDialog(new ConfigScannerController(productController));
		dialog.setVisible(true);
		
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				showLoading(false);
			}
		});
	}
	
	public void showMoveToDialog(final JTable table, final Document document, final int row) {
		showLoading(true);
		MoveToDialog dialog = new MoveToDialog(new MoveToController(productController), table, document, row);
		dialog.setVisible(true);
		
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				showLoading(false);
			}
		});
	}

	public void updateMetadataPanel() {
		this.metadataPanel.removeAll();
		this.metadataPanel.repaint();
		this.metadataPanel.revalidate();
	}
	
	public void setCloseLotButtonEnabled(boolean enabled){
		closeLotButton.setEnabled(enabled);
	}
	
	public void selectDocument(Document document){
		int index = this.product.getDocuments().indexOf(document);
		selectDocument(index);
	}

	public void selectDocument(int index) {
		updateTable();
		this.documentTable.setRowSelectionInterval(index, index);
	}
	
	public void setScannerSelected(Scanner scanner){
		this.scannerSelected = scanner;

//		this.contrastConfigurationController = new ContrastConfigurationController(this.productController);
		
//		if (scannerSelected !=null){
//			if (scannerSelected.getContrast() == null && scannerSelected.getBrightness() == null){
//				brighnessContrastButton.setEnabled(false);
//			}else{
//				brighnessContrastButton.setEnabled(true);
//			}
//		}else{
//			brighnessContrastButton.setEnabled(false);
//		}
	}
	
	public void setEnableActions(boolean b) {
		scannButton.setEnabled(b);
		documentImportButton.setEnabled(b);
		clearLotButton.setEnabled(b);
		closeLotButton.setEnabled(b && product.getDocuments().size() > 0);
		brighnessContrastButton.setEnabled(b);
		
		imageActionsBar.setEnabledActions(b);
	}

	public boolean askConfirmation(Set<Message> messages) {
		StringBuilder confirmationMessage = new StringBuilder();
		String lineSeparator = System.getProperty("line.separator");
		
		confirmationMessage.append("Atenção: ");
		confirmationMessage.append(lineSeparator);
		
		for(Message message : messages){
			confirmationMessage.append(message.getText()).append(lineSeparator);
		}
		confirmationMessage.append("Deseja salvar e enviar a imagem?");
		
		int option = JOptionPane.showConfirmDialog(this, confirmationMessage, "Forçar validação", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		return option == JOptionPane.OK_OPTION;
	}
	
	public boolean askResume(String message){
		StringBuilder confirmationMessage = new StringBuilder();
		String lineSeparator = System.getProperty("line.separator");
	
		confirmationMessage.append("Atenção: ");
		confirmationMessage.append(lineSeparator);
		confirmationMessage.append(message);
		
		int option = JOptionPane.showConfirmDialog(this, confirmationMessage, "Prosseguir", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		return option == JOptionPane.YES_OPTION;
	}
	
	public void askResumeConfirme(String message){
		JOptionPane.showMessageDialog(this, message, "Atenção: ", JOptionPane.WARNING_MESSAGE);
	}

	public void showIqfMessage(Set<String> message) {
		StringBuilder builder = new StringBuilder();
		builder.append("<font size=\"3\" face = \"Calibri\">");
		for (String s : message) {
			builder.append(s);
		}
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBorder(null);
		textPane.setBackground(null);
		textPane.setOpaque(false);
		textPane.setContentType("text/html");
		textPane.setForeground(Color.black);
		textPane.setText(builder.toString());
		
		SimpleAttributeSet attribute = new SimpleAttributeSet();
		StyleConstants.setAlignment(attribute, StyleConstants.ALIGN_LEFT);
		textPane.setParagraphAttributes(attribute, false);
		
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.setAutoscrolls(true);
		messagePanel.setOpaque(false);
		messagePanel.setBackground(new Color(255, 255, 255, 0));
		messagePanel.add(textPane);
		
		JOptionPane.showMessageDialog(this, messagePanel, "Apontamento de qualidade", JOptionPane.WARNING_MESSAGE);
	}
}