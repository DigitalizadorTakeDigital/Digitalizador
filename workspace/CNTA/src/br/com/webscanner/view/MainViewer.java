package br.com.webscanner.view;

import javax.swing.JComponent;

public interface MainViewer {
	/**
	 * Seta um componente na janela principal do aplicativo.
	 * @param component
	 */
	void showComponent(JComponent component);
}
