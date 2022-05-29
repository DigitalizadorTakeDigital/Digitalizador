package br.com.webscanner.view;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;


/**
 * Classe utilizada para exibir uma imagem no plano de fundo de um JPanel.
 * @author Jonathan Camara
 *
 */
public class BackgroundPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private Image image;
	
	public BackgroundPanel(){}

	public BackgroundPanel(Image image) throws Exception {
		this.image = image;
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.drawImage(image, 0, 0, this);
		g2d.dispose();
	}
	
	 public void setImage (Image image){
	      this.image = image;
	      setPreferredSize (new Dimension(image.getWidth (this), image.getHeight (this)));
	      revalidate();
	 }
}