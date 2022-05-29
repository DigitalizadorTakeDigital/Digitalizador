/**
 * 
 */
package br.com.webscanner.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import br.com.webscanner.util.ImageUtil;

/**
 * Classe que representa um botão personalizado com fundo translúcido.
 * @author Jonathan Camara
 */
@SuppressWarnings("serial")
public class TranslucentButton extends JButton {
	private static final int TOOLTIP_WIDTH = 500; 
	private String imageName;
	private String imageOverName;
	private String toolTipText;
	private String label;
	
	/**
	 * Cria um botão com uma imagem.
	 * @param imageName - Nome da imagem dentro da aplicação.
	 * @param imageOverName - Nome da imagem que será o over.
	 * @param toolTipText - Texto que será exibido no toolTip do botão. Caso <b>null</b> não será exibido toolTip.
	 */
	public TranslucentButton(String imageName, String imageOverName, String toolTipText) {
		this.imageName = imageName;
		this.imageOverName = imageOverName;
		this.toolTipText = toolTipText;
		construct();
	}
	
	public TranslucentButton(String imageName, String imageOverName, String label, String toolTipText) {
		this.imageName = imageName;
		this.imageOverName = imageOverName;
		this.label = label;
		this.toolTipText = toolTipText;
		construct();
	}
	
	private void construct(){
		if(toolTipText != null){
			int width = SwingUtilities.computeStringWidth(getFontMetrics(getFont()), toolTipText);

			if(width > TOOLTIP_WIDTH){
				width = TOOLTIP_WIDTH;
			}
			
			setToolTipText(String.format("<html><p width='%d'>%s</p></html>", width, toolTipText));
		}
		
		if(label != null){
			setText(label);
			setHorizontalTextPosition(SwingConstants.CENTER);
			setVerticalTextPosition(SwingConstants.BOTTOM);
		}
		
		setIcon(ImageUtil.getImage(imageName));
		setMargin(new Insets(0, 0, 0, 0));
		setBorder(null);
		setBackground(new Color(255, 255, 255, 0));
		setOpaque(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setRolloverIcon(ImageUtil.getImage(imageOverName));
		setContentAreaFilled(false);
		setBorderPainted(false);
		setDoubleBuffered(true);
		setFocusPainted(false);
	}
	
	@Override
	public JToolTip createToolTip() {
		JToolTip toolTip =  super.createToolTip();
		toolTip.setBackground(new Color(255, 255, 132));
		toolTip.setForeground(Color.black);
		
		return toolTip;
	}
}
