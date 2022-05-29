package br.com.webscanner.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import br.com.webscanner.controller.ProductScannerController;
import br.com.webscanner.view.task.CloseLotTask;


/**
 * @author Jonathan Camara
 *
 */
public class CloseLotListener implements ActionListener {
	private ProductScannerController controller;
	
	public CloseLotListener(ProductScannerController controller) {
		this.controller = controller;
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		controller.setEnableActions(false);
		
		CloseLotTask task = new CloseLotTask(controller);
		task.execute();		
	}
}