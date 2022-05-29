/**
 * 
 */
package br.com.webscanner.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * Painel com cantos arredondados e fundo translucido.
 * @author Jonathan Camara
 *
 */
public class BorderPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void paintComponent(Graphics g) {
	    int x = 1;
	    int y = 1;
	    int w = getWidth() -4;
	    int h = getHeight() -4;
	    int arc = 40;

	    Graphics2D g2 = (Graphics2D) g.create();
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    g2.setColor(new Color(255, 255, 255, 150));
	    g2.fillRoundRect(x, y, w, h, arc, arc);

	    g2.setStroke(new BasicStroke(1f));
	    g2.setColor(Color.LIGHT_GRAY);
	    g2.drawRoundRect(x, y, w, h, arc, arc); 

	    g2.dispose();
	}
}
