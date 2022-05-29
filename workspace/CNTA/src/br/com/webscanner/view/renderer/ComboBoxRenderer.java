/**
 * 
 */
package br.com.webscanner.view.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Jonathan Camara
 *
 */
@SuppressWarnings("serial")
public class ComboBoxRenderer extends JLabel implements ListCellRenderer {
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = new JLabel(value.toString());
		list.setSelectionBackground(Color.white);
		list.setBackground(Color.white);
		
		if(isSelected){
			renderer.setOpaque(true);
			renderer.setForeground(Color.white);
			renderer.setBackground(new Color(178, 0, 0));
		}
		
		return renderer;
	}
}