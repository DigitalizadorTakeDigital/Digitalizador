/**
 * 
 */
package br.com.webscanner.model.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import br.com.webscanner.exception.OCRException;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.extraction.Extraction;
import br.com.webscanner.model.domain.extraction.ExtractionType;
import br.com.webscanner.model.domain.typification.ExtractableField;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.WebscannerUtil;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.oned.Code128Reader;
import com.google.zxing.oned.Code39Reader;
import com.google.zxing.oned.ITFReader;
import com.google.zxing.qrcode.QRCodeReader;

import OnBarcode.Barcode.BarcodeScanner.BarcodeScanner;
import OnBarcode.Barcode.BarcodeScanner.BarcodeType;

/**
 * Classe responsável pela realização de OCR.
 * @author Diego
 *
 */
public class OCRService {
	private static Logger logger = LogManager.getLogger(OCRService.class.getName());
	
	static{
		try {
			logger.info("Carregando DLL liblept168");
			System.load(System.getProperty("java.io.tmpdir") + "/" + WebscannerUtil.getFileName(WebScannerConfig.getProperty("urlLiblept168")));
			logger.info("Carregando DLL tesseract302");
			System.load(System.getProperty("java.io.tmpdir") + "/" + WebscannerUtil.getFileName(WebScannerConfig.getProperty("urlTesseract")));
		} catch (Error e) {
			logger.error("Falha ao carregar as dlls de OCR");
		}
	}
	
	public static String doOCR(BufferedImage bufferedImage, ExtractionType type) throws OCRException{
		String returnOCR = null;
		
		if(bufferedImage == null){
			return null;
		}
		
		if(type.equals(ExtractionType.TEXT)){
			returnOCR = doOCROnText(bufferedImage);
		} else {
			returnOCR = doOCROnBarcode(bufferedImage, type);
		}
		
		return returnOCR;
	}
	
	public static String[] lerOCR2of5(BufferedImage arquivoImagem)throws OCRException{
		
//		String[] dadosBarCodes = BarcodeScanner.Scan(arquivoImagem, BarcodeType.Interleaved2of5);
		String[] dadosBarCodes = BarcodeScanner.ScanSingleBarcode(arquivoImagem, BarcodeType.Interleaved2of5);
		return dadosBarCodes;
		
	}
	
	public static String[] lerOCR3of9(File arquivoImagem)throws OCRException{
		
		String[] dadosBarCodes = BarcodeScanner.Scan(arquivoImagem.getAbsolutePath(), BarcodeType.Code39Extension);
		
		return dadosBarCodes;
		
	}
//	}
	
	private static String doOCROnBarcode(BufferedImage bufferedImage, ExtractionType type) throws OCRException{
		String returnOCR = null;

		if(type.equals(ExtractionType.CODE128)){
			LuminanceSource ls = new BufferedImageLuminanceSource(bufferedImage);
			BinaryBitmap bbmp = new BinaryBitmap(new HybridBinarizer(ls));
			Hashtable<DecodeHintType, Object> hint = new Hashtable<DecodeHintType, Object>();
			Result result = null;
			hint.put(DecodeHintType.TRY_HARDER, BarcodeFormat.CODE_128);
			Code128Reader code128Reader = new Code128Reader();
			try {
				result = code128Reader.decode(bbmp, hint);
			} catch (NotFoundException e) {
				throw new OCRException("Não foi encontrado o codigo de barras na imagem");
			} catch (FormatException e) {
				throw new OCRException("Formato de imagem inválido");
			}
			returnOCR = result.getText();
		}else if(type.equals(ExtractionType.ITF)){
			LuminanceSource ls = new BufferedImageLuminanceSource(bufferedImage);
			BinaryBitmap bbmp = new BinaryBitmap(new HybridBinarizer(ls));
			Hashtable<DecodeHintType, Object> hint = new Hashtable<DecodeHintType, Object>();
			Result result = null;
			hint.put(DecodeHintType.TRY_HARDER, BarcodeFormat.ITF);
			ITFReader itfReader = new ITFReader();
			try {
				result = itfReader.decode(bbmp, hint);
			} catch (NotFoundException e) {
				throw new OCRException("Não foi encontrado o codigo de barras na imagem");
			} catch (FormatException e) {
				throw new OCRException("Formato de imagem inválido");
			}
			returnOCR = result.getText();
		}else if (type.equals(ExtractionType.QRCODE)){
			Map<DecodeHintType,Object> HINTS = new HashMap<DecodeHintType, Object>();
			Map<DecodeHintType,Object> HINTS_PURE = new HashMap<DecodeHintType, Object>();
			
			HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
			HINTS.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
			HINTS_PURE.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
			
			LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
			BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));

			Reader reader = new MultiFormatReader();
			try {
				// Look for multiple barcodes
				MultipleBarcodeReader multiReader = new GenericMultipleBarcodeReader(reader);
				Result[] theResults = multiReader.decodeMultiple(bitmap, HINTS);
				if (theResults != null) {
					return theResults[0].getText();
				}
			} catch (Exception re) {}

			try {
				// Look for pure barcode
				Result theResult = reader.decode(bitmap, HINTS_PURE);
				if (theResult != null) {
					return theResult.getText();
				}
			} catch (Exception re) {}
			
			try {
				// Look for normal barcode in photo
				Result theResult = reader.decode(bitmap, HINTS);
				if (theResult != null) {
					return theResult.getText();
				}
			} catch (Exception re) {}

			try {
				// Try again with other binarizer
				BinaryBitmap hybridBitmap = new BinaryBitmap(new HybridBinarizer(source));
				Result theResult = reader.decode(hybridBitmap, HINTS);
				if (theResult != null) {
					return theResult.getText();
				}
			} catch (Exception re) {
			}
			
			try {
				QRCodeReader qrCodeReader = new QRCodeReader();
				LuminanceSource ls = new BufferedImageLuminanceSource(bufferedImage);
				BinaryBitmap bbmp = new BinaryBitmap(new HybridBinarizer(ls));
				return qrCodeReader.decode(bbmp, HINTS).getText();
			} catch (Exception re) {
				throw new OCRException("Não foi encontrado o codigo de barras na imagem");
			}
		}else if(type.equals(ExtractionType.CODE39)){
					LuminanceSource ls = new BufferedImageLuminanceSource(bufferedImage);
					BinaryBitmap bbmp = new BinaryBitmap(new HybridBinarizer(ls));
					Hashtable<DecodeHintType, Object> hint = new Hashtable<DecodeHintType, Object>();
					Result result = null;
					hint.put(DecodeHintType.TRY_HARDER, BarcodeFormat.CODE_39);
					Code39Reader code39Reader = new Code39Reader();
					try {
						result = code39Reader.decode(bbmp, hint);
					} catch (NotFoundException e) {
						throw new OCRException("Não foi encontrado o codigo de barras na imagem");
					} catch (FormatException e) {
						throw new OCRException("Formato de imagem inválido");
					}
					returnOCR = result.getText();
				}
				
				return returnOCR;
			}
		
	
//	private static String doOCROnBarcode(BufferedImage bufferedImage, ExtractionType type) throws OCRException{
//		String returnOCR = null;
//		
//		LuminanceSource ls = new BufferedImageLuminanceSource(bufferedImage);
//		BinaryBitmap bbmp = new BinaryBitmap(new HybridBinarizer(ls));
//		Hashtable<DecodeHintType, Object> hint = new Hashtable<DecodeHintType, Object>();
//		Result result = null;
//
//		if(type.equals(ExtractionType.CODE128)){
//			hint.put(DecodeHintType.TRY_HARDER, BarcodeFormat.CODE_128);
//			Code128Reader code128Reader = new Code128Reader();
//			try {
//				result = code128Reader.decode(bbmp, hint);
//			} catch (NotFoundException e) {
//				throw new OCRException("Não foi encontrado o codigo de barras na imagem");
//			} catch (FormatException e) {
//				throw new OCRException("Formato de imagem inválido");
//			}
//			returnOCR = result.getText();
//		}else if (type.equals(ExtractionType.QRCODE)){
//			hint.put(DecodeHintType.TRY_HARDER, BarcodeFormat.QR_CODE);
//			QRCodeReader qrCodeReader = new QRCodeReader();
//			
//			try {
//				result = qrCodeReader.decode(bbmp, hint);
//			} catch (NotFoundException e) {
//				throw new OCRException("Não foi encontrado o QRCODE na imagem");
//			} catch (ChecksumException e) {
//				throw new OCRException("O código de barras foi encontrado, porém houve um problema na verificação do checksum do mesmo.");
//			} catch (FormatException e) {
//				throw new OCRException("Formato de imagem inválido");
//			}
//			returnOCR = result.getText();
//		}
//		return returnOCR;
//	}
	
	private static String doOCROnText(BufferedImage bufferedImage) throws OCRException{
		//Tesseract tess = Tesseract.getInstance();
		Tesseract1 tess = new Tesseract1();
		tess.setLanguage("por");
		tess.setDatapath(System.getProperty("java.io.tmpdir"));
//		tess.setDatapath("C:\\Users\\M190660\\Desktop\\");
		try {
			String returnOCR = tess.doOCR(bufferedImage);
			returnOCR = returnOCR.replaceAll("\n", " ");
			return returnOCR.trim();
		} catch (TesseractException e) {
			throw new OCRException("Erro ao realizar a extração de texto na imagem. " + e.getMessage());
		}
	}

	public static void extractFields(Document document){
		List<Extraction> extractions = document.getDocumentModel().getExtractions();
		if(extractions != null){
			for(Extraction extraction : extractions){
				BufferedImage bufferedImage = null;
				try {
					bufferedImage = ImageUtil.getFileImage(document.getContent().getMainFile());
				} catch (IOException e) {
					logger.warn("Erro ao recuperar arquivo de imagem de documento. Mensagem de erro: " + e.getMessage());
					return;
				}
				
				int x = extraction.getX();
				int y = extraction.getY();
				int w = extraction.getW();
				int h = extraction.getH();
				ExtractionType type = extraction.getType();
				BufferedImage subImage = ImageUtil.getSubImage(bufferedImage, x, y, w, h);
				
				String value = null;
				try {
					value = doOCR(subImage, type);
					if (value != null){
						for(ExtractableField extractableField : extraction.getExtractableFields()){
							if(extractableField.getLength() != 0){
								try {
									String extractValue = value.substring(extractableField.getIndex(), extractableField.getIndex() + extractableField.getLength());
									Field field = document.getFieldById(extractableField.getFieldModel().getId());
									field.setValue(extractValue);
								} catch (IndexOutOfBoundsException e) {
									logger.warn("O índice e legth definidos no arquivo de configuração não foram encontrados no valor extraído. {}", value);
								}
							}else{
								Field field = document.getFieldById(extractableField.getFieldModel().getId());
								field.setValue(value);
							}
						}
					}
				} catch (OCRException e) {
					logger.warn("Erro ao extrair campo de documento. Mensagem: {}", e.getMessage());
				}
			}
		}
	}
}
