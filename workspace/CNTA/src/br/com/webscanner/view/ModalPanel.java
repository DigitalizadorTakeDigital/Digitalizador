package br.com.webscanner.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.JRootPane;

@SuppressWarnings("serial")
public class ModalPanel extends JPanel{
	
	
	public ModalPanel() {
		this.addMouseListener(new MouseAdapter() {});
		this.addMouseMotionListener(new MouseMotionAdapter() {});
		this.addKeyListener(new KeyAdapter() {});
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int w = 400;
		int h = 400;
		Object object = this.getParent().getParent();
		if(object instanceof JRootPane){
			JRootPane rootPane = (JRootPane) object;
			
			w = rootPane.getWidth();
			h = rootPane.getHeight();
		}

		Graphics2D g2 = (Graphics2D)g.create();

		setBackground(new Color(255, 255, 255, 0));
		setOpaque(false);
		
		Composite urComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
		g2.fillRect(0, 0, w, h);
		g2.setComposite(urComposite);

		if(getComponentCount() > 0){
			Component component = getComponent(0);
			if(component instanceof JPanel){
				JPanel dialog = (JPanel) getComponent(0);
				dialog.repaint();
			}
		}
		
		g2.dispose();
	}
}
