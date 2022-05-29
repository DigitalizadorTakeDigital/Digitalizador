/**
 * 
 */
package br.com.webscanner.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import br.com.webscanner.controller.ProductMenuController;
import br.com.webscanner.exception.XmlConfigurationException;
import br.com.webscanner.model.domain.MenuProduct;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.xml.XmlUtil;

/**
 * Representa um produto na lista de produtos
 * @author Jonathan Camara
 */
public class ProductItemPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel productLabel;
	private JLabel icon;
	private MenuProduct product;
	
	public ProductItemPanel(final MenuProduct menuProduct, final ProductMenuController controller) {
		this.product = menuProduct;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBackground(new Color(255,255,255,0));
		setOpaque(false);
		
		this.icon = new JLabel();
		this.icon.setIcon(ImageUtil.getImage("arrow.png"));
		this.icon.setBounds(0, 0, 14, 11);

		this.productLabel = new JLabel();
		this.productLabel.setText(this.product.getName());
		this.productLabel.setFont(new Font("Arial", Font.BOLD, 18));
		this.productLabel.setVerticalTextPosition(JLabel.CENTER);
		this.productLabel.setHorizontalTextPosition(JLabel.LEFT);
		this.productLabel.setForeground(Color.white);
		this.productLabel.setPreferredSize(new Dimension(380, 20));
		this.productLabel.setHorizontalAlignment(SwingConstants.LEFT);
		this.productLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				super.mouseEntered(arg0);
				productLabel.setForeground(Color.black);
				productLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				super.mouseExited(arg0);
				productLabel.setForeground(Color.white);
				productLabel.setCursor(Cursor.getDefaultCursor());
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				super.mouseClicked(arg0);
				try {
					controller.showProductScanner(XmlUtil.getProduct(menuProduct));
				} catch (XmlConfigurationException e) {
					//TODO: Exibir mensagem 
					e.printStackTrace();
				}
			}
		});

		this.add(this.icon);
		this.add(Box.createRigidArea(new Dimension(10, 0)));
		this.add(this.productLabel);
	}
}