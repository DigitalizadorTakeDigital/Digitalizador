/**
 * 
 */
package br.com.webscanner.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import br.com.webscanner.model.domain.Message;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Painel container das mensagens que ser�o exibidas na tela.
 * @author Jonathan Camara
 *
 */
@SuppressWarnings("serial")
public class MessagePanel extends JPanel{
	private Message message;
	
	public enum MessageLevel{
		INFO, WARN, ERROR
	}
	
	public MessagePanel(Message message) {
		this.message = message;
		construct();
	}
	
	private void construct() {
		setOpaque(false);
		setLayout(new FormLayout("default:grow", "top:default, center:default:grow"));
		setPreferredSize(new Dimension(300, 130));
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBorder(null);
		textPane.setBackground(null);
		textPane.setOpaque(false);
		
        textPane.setFont(new Font("Tahoma", Font.BOLD, 14));
		textPane.setForeground(Color.white);
		textPane.setText(message.getText().trim());
		
		SimpleAttributeSet attribute = new SimpleAttributeSet();
		StyleConstants.setAlignment(attribute, StyleConstants.ALIGN_CENTER);
		textPane.setParagraphAttributes(attribute, false);
		
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.LINE_AXIS));
		messagePanel.setOpaque(false);
		messagePanel.setBackground(new Color(255, 255, 255, 0));
		messagePanel.add(textPane);
		
		JPanel closeContainer = new JPanel();
		closeContainer.setOpaque(false);
		closeContainer.setBackground(new Color(255, 255, 255, 0));
		closeContainer.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		TranslucentButton closeButton = new TranslucentButton("close.png", "close.png", "Fechar");
		closeContainer.add(closeButton);
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Object object = MessagePanel.this.getParent().getParent();
				if(object instanceof JRootPane){
					JRootPane rootPane = (JRootPane) object; 
					JPanel glassPane = (JPanel) rootPane.getGlassPane();
					
					glassPane.remove(MessagePanel.this);
					glassPane.repaint();
					glassPane.revalidate();
					if(glassPane.getComponentCount() == 0){
						glassPane.setVisible(false);
					}
				}
			}
		});
		
		add(closeContainer, CC.xy(1, 1));
		add(messagePanel, CC.xywh(1, 1, 1, 2, CC.CENTER, CC.CENTER));
	}

	@Override
	protected void paintComponent(Graphics g) {
	    int x = 1;
	    int y = 1;
	    int w = getWidth() - 4;
	    int h = getHeight() - 4;
	    int arc = 40;

	    Graphics2D g2 = (Graphics2D) g.create();
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    if(message.getLevel() == MessageLevel.ERROR){
	    	g2.setColor(new Color(255, 0, 0, 190));
	    }else if(message.getLevel() == MessageLevel.INFO){
	    	g2.setColor(new Color(0, 105, 190, 190));
	    }else{
	    	g2.setColor(new Color(255, 111, 15, 190));
	    }
	    
	    g2.fillRoundRect(x, y, w, h, arc, arc);
	    g2.setStroke(new BasicStroke(3f));
	    g2.setColor(Color.LIGHT_GRAY);
	    g2.drawRoundRect(x, y, w, h, arc, arc); 

	    g2.dispose();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessagePanel other = (MessagePanel) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}
}