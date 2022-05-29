/**
 * 
 */
package br.com.webscanner.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Classe responsavel por exibir as imagens dos documentos escaneados, bem como executar opera��es de zoom e flip.
 * @author Diego
 *
 */
@SuppressWarnings("serial")
public class ImagePanel extends JPanel {
	public static final int BORDER_SIZE = 10;
	public static final double ORIGINAL_SCALE = 1.0;
	public static final double ZOOM_RATE = 0.1;
	public static final double MAXIMUM_SCALE = 2.0;
	private BufferedImage image;
	private double scale;
	private double configuredScale;

	public ImagePanel(double scale){
		this.configuredScale = scale;
		this.scale = scale;
		this.image = null;
	}

	@Override
	public void paintComponent(Graphics g){		
		if(image != null){
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);       
			AffineTransform at = scaleImage(this.scale);	        
			g2.drawRenderedImage(image, at);
//			TODO: Manter para testes.
//			<position x="865" y="20" width="765" height="130"> // Cesta
//			int x = 27;
//			int y = 35;
//			int w = 199;
//			int h = 742;
//			if((x + w) < image.getWidth() && (y + h) < image.getHeight()){
//				g2.setColor(Color.red);
//				g2.drawRect((int)(scale * x), (int)(scale * y), (int)(scale * w), (int)(scale * h));
//			}
			
//			x = 1445;
//			y = 39;
//			w = 191;
//			h = 738;
//			//<extraction type="code128"x="1445" y="39" width="191" height="738">
//			if((x + w) < image.getWidth() && (y + h) < image.getHeight()){
//				g2.setColor(Color.red);
//				g2.drawRect((int)(scale * x), (int)(scale * y), (int)(scale * w), (int)(scale * h));
//			}
		}else{
			super.paintComponent(g);
		}
	}

	public void setScale(double scale){
		this.scale = scale;
		this.updateContainer();
	}

	public double getScale(){
		return this.scale;
	}

	public void setImage (BufferedImage image){
		this.image = image;
		this.scale = configuredScale < getSmallerScale() ? getSmallerScale() : configuredScale;
		autoResizeImage();
		repaint();
		revalidate();
	
		Object object = null;
		if((object = this.getParent().getParent()) instanceof JScrollPane){
			JScrollPane scrollPane = (JScrollPane)object;
			scrollPane.getVerticalScrollBar().setValue(0);
			scrollPane.getHorizontalScrollBar().setValue(0);
		}
	}	
	
	public boolean zoomIn(){
		if((scale + ZOOM_RATE) < MAXIMUM_SCALE){
			setScale(scale + ZOOM_RATE);
			return true;
		}else{		
			setScale(MAXIMUM_SCALE);
			return false;
		}
	}

	public boolean zoomOut(){
		double smallerScale;
		if(isImageSmallerThanPanel()){
			smallerScale = configuredScale;	
		} else{
			smallerScale = getSmallerScale();
		}

		if((this.scale - ZOOM_RATE) > (smallerScale)){
			setScale(scale - ZOOM_RATE);
			return true;
		}
		else{
			setScale(smallerScale);
			return false;
		}
	}

	public AffineTransform scaleImage(double scale){
		int w = getWidth();
		int h = getHeight();
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();        
		double x = (w - scale * imageWidth)/2;
		double y = (h - scale * imageHeight)/2;
		AffineTransform at = AffineTransform.getTranslateInstance(x,y);
		at.scale(scale, scale); 
		return at;
	}

	public Dimension getPreferredSize(){
		if(image != null){
			int w = (int)(scale * image.getWidth());
			int h = (int)(scale * image.getHeight());
			return new Dimension(w, h);
		}else{
			return new Dimension(getWidth(), getHeight());
		}
	}

	public void updateContainer() {
		Object object = this.getParent().getParent().getParent();
		if(object instanceof JPanel){
			JPanel container = (JPanel) object;
			container.repaint();
			container.revalidate();
			this.revalidate();
			this.repaint();
		}
	}
	
	public void updateContainerImagePanel(){
		this.updateContainer();
	}

	public boolean isImageSmallerThanPanel(){
		boolean imageSmaller = false;
		Dimension dimension = null;

		Object object = this.getParent().getParent().getParent();
		if(object instanceof JPanel){
			JPanel container = (JPanel) object;
			dimension = container.getSize();
		}
		if(dimension != null){
			double containerWidth = dimension.getWidth() - 2 * BORDER_SIZE;
			double containerHeight = dimension.getHeight() - 2 * BORDER_SIZE;
			double imageWidth = image.getWidth();
			double imageHeight = image.getHeight();	
			imageSmaller = imageWidth < containerWidth && imageHeight < containerHeight;
		}
		return imageSmaller;
	}

	private double getSmallerScale(){
		Dimension dimension = null;
		double smallerScale = this.scale;

		Object object = this.getParent().getParent().getParent();
		if(object instanceof JPanel){
			JPanel container = (JPanel) object;
			dimension = container.getSize();
		}
		if(dimension != null){
			double containerWidth = dimension.getWidth() - 2 * BORDER_SIZE;
			double containerHeight = dimension.getHeight() - 2 * BORDER_SIZE;		
			double scaleWidth = containerWidth/image.getWidth();
			double scaleHeight = containerHeight/image.getHeight();
			smallerScale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
		}
		return smallerScale;
	}

	private void autoResizeImage(){
		if(this.isImageSmallerThanPanel()){
			this.setImageRealSize();
		} else {
			this.setImageWindowSize();
		}
	}

	/**
	 * Exibe a imagem em sua escala real.
	 */
	public void setImageRealSize(){
		this.setScale(ORIGINAL_SCALE);
	}
	
	/**
	 * Exibe a image na escala de seu container.
	 */
	public void setImageWindowSize(){
		this.setScale(configuredScale < getSmallerScale() ? getSmallerScale() : configuredScale);
	}
	
	public boolean hasScaleDefined(){
		return configuredScale > getSmallerScale();
	}

	
}