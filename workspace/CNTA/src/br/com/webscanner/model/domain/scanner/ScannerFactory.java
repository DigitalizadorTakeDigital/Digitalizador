/**
 * 
 */
package br.com.webscanner.model.domain.scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.Scanner.DriverScanner;
import br.com.webscanner.model.domain.scanner.impl.cap.CapiScanner;
import br.com.webscanner.model.domain.scanner.impl.hp.HPScanner;
import br.com.webscanner.model.domain.scanner.impl.kodak.WindowsScanner;
import br.com.webscanner.model.domain.scanner.impl.lexmark.LexmarkScanner;
import br.com.webscanner.model.domain.scanner.impl.mock.ScannerMock;
import br.com.webscanner.model.domain.scanner.impl.ranger.RangerScanner;
import br.com.webscanner.model.domain.scanner.impl.twain.TwainScanner;

/**
 * Factory da classe {@link Scannable}. Retorna uma instância implementadora de Scanner de acordo com 
 * o driver configurado no propertie da aplicação.
 * @author Jonathan Camara
 */
public class ScannerFactory {
	private static Logger logger = LogManager.getLogger(ScannerFactory.class.getName());
	
	private ScannerFactory() {}
	
	public static Scannable getInstance(Scanner scanner){
		logger.info("Recuperando a instância do scanner para o driver {}", scanner.getDriver());
		
		DriverScanner driver = scanner.getDriver(); 
		
		if (scanner.getName().toUpperCase().contains("KODAK")) {
			return new WindowsScanner(scanner);
		}else if(driver == DriverScanner.TWAIN) {
			return new TwainScanner();
		} else if (driver == DriverScanner.CAPI) {
			return new CapiScanner();
		} else if (driver == DriverScanner.MOCK) {
			return new ScannerMock();
		} else if(driver == DriverScanner.LEXMARK) {
			return new LexmarkScanner();
		} else if(driver == DriverScanner.HP) {
			return new HPScanner();
		} else if (driver == DriverScanner.RANGER) {
			return new RangerScanner();
		}
		else {
			return null;
		}
	}
}
