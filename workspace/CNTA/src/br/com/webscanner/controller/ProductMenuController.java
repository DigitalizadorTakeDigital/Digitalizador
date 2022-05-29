/**
 * 
 */
package br.com.webscanner.controller;

import br.com.webscanner.model.domain.Product;
import br.com.webscanner.view.CenterPanel;
import br.com.webscanner.view.MainViewer;
import br.com.webscanner.view.ProductScanner;

/**
 * Classe intermediadora responsável pela exibição do menu inicial de produtos.
 * @author Jonathan Camara
 *
 */
public class ProductMenuController {
	private MainViewer main;
	
	public ProductMenuController(MainViewer main) {
		this.main = main;
	}
	
	public void showMenu(){
		this.main.showComponent(new CenterPanel(this));
	}
	
	public void showProductScanner(Product product){
		this.main.showComponent(new ProductScanner(product));
	}
}