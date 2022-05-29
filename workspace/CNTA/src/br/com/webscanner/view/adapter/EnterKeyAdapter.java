/**
 * 
 */
package br.com.webscanner.view.adapter;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Transfere o foco para o próximo componente quando a tecla enter é pressionada.
 * @author Jonathan Camara
 */
public class EnterKeyAdapter extends KeyAdapter{
	public static final int ENTER = 10;
	private Component component;
	
	public EnterKeyAdapter(Component component) {
		this.component = component;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		transferFocus(arg0.getKeyCode());
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) {
		transferFocus(arg0.getKeyCode());
	}

	private void transferFocus(int keyCode) {
		if(keyCode == ENTER){
			component.transferFocus();
		}
	}
}