/**
 * 
 */
package br.com.webscanner.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.webscanner.controller.ProductMenuController;
import br.com.webscanner.model.domain.MenuProduct;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.xml.XmlUtil;

/**
 * Classe que concentra a view dos Produtos disponíveis para utilização.
 * @author Jonathan Camara
 */
public class CenterPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel container;
	private JLabel logoPanel;
	private BackgroundPanel contextContainerPanel;
	private JPanel contextPanel;
	
	public CenterPanel(ProductMenuController controller) {
		setBackground(new Color(255,255,255,0));
		setOpaque(false);
		
		this.container = new JPanel();
		this.container.setPreferredSize(new Dimension(600, 650));
		this.container.setLayout(new BoxLayout(this.container, BoxLayout.PAGE_AXIS));
		this.container.setBackground(new Color(255,255,255,0));
		this.container.setOpaque(false);
		this.container.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		this.logoPanel = new JLabel();
		this.logoPanel.setIcon(ImageUtil.getImage("logoK.png"));
		this.logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		try {
			this.contextContainerPanel = new BackgroundPanel(ImageUtil.getImage("contextContainer.png").getImage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.contextContainerPanel.setBackground(new Color(255,255,255,0));
		this.contextContainerPanel.setOpaque(false);
		this.contextContainerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		this.contextPanel = new JPanel();
//		this.contextPanel.setBackground(Color.red);
		this.contextPanel.setPreferredSize(new Dimension(420, 300));
		this.contextPanel.setLayout(new FlowLayout(0));
		this.contextPanel.setBackground(new Color(255,255,255,0));
		this.contextPanel.setOpaque(false);
		
		this.container.add(Box.createRigidArea(new Dimension(0, 30)));
		this.container.add(this.logoPanel);
		this.container.add(Box.createRigidArea(new Dimension(0, 20)));
		this.container.add(this.contextContainerPanel);
		this.contextContainerPanel.add(Box.createRigidArea(new Dimension(0, 530)));
		this.contextContainerPanel.add(this.contextPanel);
		add(container);
		
		this.createProductList(this.contextPanel, controller);
	}
	
	private void createProductList(JPanel panel, ProductMenuController controller){
		List<MenuProduct> products = XmlUtil.getMenuProducts();
		
		for(MenuProduct product : products){
			panel.add(new ProductItemPanel(product, controller));
		}
	}
}
