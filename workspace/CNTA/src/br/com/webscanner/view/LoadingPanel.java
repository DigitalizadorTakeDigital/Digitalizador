/**
 * 
 */
package br.com.webscanner.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.JRootPane;

/**
 * @author Jonathan Camara
 *
 */
@SuppressWarnings("serial")
public class LoadingPanel extends JPanel {
	
	public LoadingPanel() {
		this.addMouseListener(new MouseAdapter() {});
		this.addMouseMotionListener(new MouseMotionAdapter() {});
		this.addKeyListener(new KeyAdapter() {});
	}
//	private static int contador = 0;
	
	@Override
	public void paint(Graphics g) {
		int w = 400;
		int h = 400;
		Object object = this.getParent().getParent();
		if(object instanceof JRootPane){
			JRootPane rootPane = (JRootPane) object;
			
			w = rootPane.getWidth();
			h = rootPane.getHeight();
		}
		Graphics2D g2 = (Graphics2D)g.create();
//		float fade = (float)mFadeCount / (float)mFadeLimit;
		// Gray it out.
		setBackground(new Color(255, 255, 255, 0));
		setOpaque(false);
		Composite urComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
		g2.fillRect(0, 0, w, h);
		g2.setComposite(urComposite);

		//retirado o loading
		//Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("br/com/webscanner/resources/images/loading.gif"));
		//g2.drawImage(image, (w - image.getWidth(this)) /2, (h - image.getHeight(this)) /2, this);
		g2.dispose();
		super.paint(g);
	}
}
