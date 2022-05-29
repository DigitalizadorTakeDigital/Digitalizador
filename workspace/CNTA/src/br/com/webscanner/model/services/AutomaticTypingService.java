/**
 * 
 */
package br.com.webscanner.model.services;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.media.jai.operator.TransposeType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.OCRException;
import br.com.webscanner.model.domain.Content;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.extraction.ExtractionType;
import br.com.webscanner.model.domain.typification.BarCodeRecognitionModel;
import br.com.webscanner.model.domain.typification.BlankPageRecognitionModel;
import br.com.webscanner.model.domain.typification.DefaultRecognitionModel;
import br.com.webscanner.model.domain.typification.DocumentRecognitionModel;
import br.com.webscanner.model.domain.typification.DocumentRecognizable;
import br.com.webscanner.model.domain.typification.ExtractableField;
import br.com.webscanner.model.domain.typification.Position;
import br.com.webscanner.model.domain.typification.PositionGroup;
import br.com.webscanner.model.domain.typification.Recognition;
import br.com.webscanner.model.domain.typification.Recognizable;
import br.com.webscanner.model.domain.typification.specific.SpecificRecognizable;
import br.com.webscanner.model.domain.typification.specific.SpecificRecognizableCheck;
import br.com.webscanner.util.ImageUtil;
import br.com.webscanner.util.QrPrevUtils;
import br.com.webscanner.util.ValidationUtil;

/** 
 * @author fernando.germano
 *
 */
public class AutomaticTypingService {
	private static Logger logger = LogManager.getLogger(AutomaticTypingService.class.getName());
	
		public static boolean QrNaoLido;
	
	public static void typing(List<Recognition> recognitions, List<DocumentModel> models, String cmc7, Document document) {
		
		if(recognitions != null){
			typified : for(Recognition recognition : recognitions){
				ExtractionType type = recognition.getType();
				switch (type) {
				case CODE128:
					if(typingByCode128(document, recognition.getRecognizable())){
						OCRService.extractFields(document);
						break typified;
					}
					break;
				case ITF:
					if(typingByCodeITF(document, recognition.getRecognizable())){
						OCRService.extractFields(document);
						break typified;
					}
					break;
				case CMC7:
					if(cmc7 != null && !cmc7.isEmpty()){
						if(typingByCmc7(document, cmc7, recognition.getRecognizable())){
							break typified;
						}
					}
					break;
				case TEXT:
					if(typingByText(document, recognition.getRecognizable())){
						OCRService.extractFields(document);
						break typified;
					}
					break;
				case QRCODE:
					if(typingByQRCode(document, recognition.getRecognizable())){
						OCRService.extractFields(document);
						break typified;
					}
					break;
				case BLANKPAGE:
					if (typingByBlankPage(document, recognition.getRecognizable())){
						break typified;
					}
					break;
				case DEFAULT:
					document.setDocumentModel(((DefaultRecognitionModel)(recognition.getRecognizable())).getModel());
					break;
				case SPECIFIC:
					SpecificRecognizable recognizable = (SpecificRecognizable) recognition.getRecognizable();
					if (recognizable.recognize(document)){
						break typified;
					}
					break;
				case SPECIFICCHECK:
					SpecificRecognizableCheck recognizableCheck = (SpecificRecognizableCheck) recognition.getRecognizable();
					if (recognizableCheck.recognize(document, cmc7)){
						break typified;
					}
					break;			
				case CODE39:
					if(typingByCode39(document, recognition.getRecognizable())){
						OCRService.extractFields(document);
						break typified;
					}
					
					break;
				default:
					break;
				}
			}
		}
	}
	
	private static boolean typingByCode39(Document document, Recognizable recognizable) {
		logger.info("Iniciando tipificação de documento por OCR - Code39.");
		
		if(recognizable instanceof PositionGroup){
			PositionGroup positionsGroup = (PositionGroup) recognizable;
			
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageUtil.getFileImage(document.getContent().getMainFile());
			} catch(IOException e) {
				logger.error("Não foi possível carregar o arquivo de imagem do documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());
				return false;
			}

			for(Position position : positionsGroup.getPositions()){
				int x = position.getX();
				int y = position.getY();
				int width = position.getWidth();
				int height = position.getHeight();
				String code = null;
				
				try{
					BufferedImage subImage = ImageUtil.getSubImage(bufferedImage, x, y, width, height);
					//String nomeImg = document.getName() + ".png";
					//ImageIO.write(subImage,"png", new File("C:\\teste\\" + nomeImg));
					code = OCRService.doOCR(subImage, ExtractionType.CODE39);
				} catch (OCRException e) {
					logger.warn("Não foi possível realizar OCR no documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());
					continue;
				}
				
	
				if(code != null){
				code =code.trim();
					for(DocumentRecognizable documentRecognizable : position.getDocumentRecognizables()){
						BarCodeRecognitionModel barCodeRecognitionModel = (BarCodeRecognitionModel) documentRecognizable; 
						for(String documentPattern : barCodeRecognitionModel.getPatterns()){
							String regex = documentPattern.replace("*", "[0-9]"); 
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern.matcher(code);
							
							if(matcher.matches()){
								TransposeType orientation = barCodeRecognitionModel.getOrientation();
								if (orientation != null) {
									Content content = document.getContent();
									content.rotacionar(orientation);
								}
								
								DocumentModel model = barCodeRecognitionModel.getDocumentModel();
								document.setDocumentModel(model);
								
								for(ExtractableField extractableField : barCodeRecognitionModel.getFields()){
									Field field = document.getFieldById(extractableField.getFieldModel().getId());
									
									if(field != null){
										try {
											String value = code.substring(extractableField.getIndex(), extractableField.getIndex() + extractableField.getLength());
											field.setValid(true);
											field.setValue(value);
										} catch (IndexOutOfBoundsException e) {
											logger.error("O índice e legth definidos no arquivo de configuração não foram encontrados no valor extraído do QRCode");
											continue;
										}
									}
								}
								
								return true;
							}
						}
					}
				}
			}
		}
		logger.info("Fim da tipificação. O documento não foi tipificado.");
		return false;
	}

	private static boolean typingByCode128(Document document, Recognizable	recognizable){
		logger.info("Iniciando tipificação de documento por OCR - Code128.");
		
		if(recognizable instanceof PositionGroup){
			PositionGroup positionsGroup = (PositionGroup) recognizable;
			
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageUtil.getFileImage(document.getContent().getMainFile());
			} catch(IOException e) {
				logger.error("Não foi possível carregar o arquivo de imagem do documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());
				return false;
			}

			for(Position position : positionsGroup.getPositions()){
				int x = position.getX();
				int y = position.getY();
				int width = position.getWidth();
				int height = position.getHeight();
				String code = null;
				
				try{
					BufferedImage subImage = ImageUtil.getSubImage(bufferedImage, x, y, width, height);
                  if(x == 2242)  {
                	subImage = rotateClockwise90(subImage);
                    subImage = rotateClockwise90(subImage);
                    subImage = rotateClockwise90(subImage);
                  }
                  String nomeImg = document.getName() + ".png";
				 // ImageIO.write(subImage,"png", new File("C:\\teste\\" + nomeImg));
					//ImageIO.write(subImage, "png" , new File("C:\\teste\\imagem12.png"));
					code = OCRService.doOCR(subImage, ExtractionType.CODE128);
				} catch (OCRException e) {
					logger.warn("Não foi possível realizar OCR no documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());
					continue;
				} 
				
	
				if(code != null){
					for(DocumentRecognizable documentRecognizable : position.getDocumentRecognizables()){
						BarCodeRecognitionModel barCodeRecognitionModel = (BarCodeRecognitionModel) documentRecognizable; 
						for(String documentPattern : barCodeRecognitionModel.getPatterns()){
							String regex = documentPattern.replace("*", "[0-9]"); 
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern.matcher(code);
							
							if(matcher.matches()){
								TransposeType orientation = barCodeRecognitionModel.getOrientation();
								if (orientation != null) {
									Content content = document.getContent();
									content.rotacionar(orientation);
								}
								
								DocumentModel model = barCodeRecognitionModel.getDocumentModel();
								document.setDocumentModel(model);
								
								for(ExtractableField extractableField : barCodeRecognitionModel.getFields()){
									Field field = document.getFieldById(extractableField.getFieldModel().getId());
									
									if(field != null){
										try {
											String value = code.substring(extractableField.getIndex(), extractableField.getIndex() + extractableField.getLength());
											field.setValid(true);
											field.setValue(value);
										} catch (IndexOutOfBoundsException e) {
											logger.error("O índice e legth definidos no arquivo de configuração não foram encontrados no valor extraído do QRCode");
											continue;
										}
									}
								}
								
								return true;
							}
						}
					}
				}
			}
		}
		logger.info("Fim da tipificação. O documento não foi tipificado.");
		return false;
	}
	
	private static boolean typingByCodeITF(Document document, Recognizable	recognizable){
		logger.info("Iniciando tipificação de documento por OCR - CodeITF.");
		
		if(recognizable instanceof PositionGroup){
			PositionGroup positionsGroup = (PositionGroup) recognizable;
			
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageUtil.getFileImage(document.getContent().getMainFile());
			} catch(IOException e) {
				logger.error("Não foi possível carregar o arquivo de imagem do documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());
				return false;
			}

			for(Position position : positionsGroup.getPositions()){
				int x = position.getX();
				int y = position.getY();
				int width = position.getWidth();
				int height = position.getHeight();
				String code = null;
				
				try{
					BufferedImage subImage = ImageUtil.getSubImage(bufferedImage, x, y, width, height);
					code = OCRService.doOCR(subImage, ExtractionType.ITF);
				} catch (OCRException e) {
					logger.warn("Não foi possível realizar OCR no documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());
					continue;
				}
				
				if(code != null){
					for(DocumentRecognizable documentRecognizable : position.getDocumentRecognizables()){
						BarCodeRecognitionModel barCodeRecognitionModel = (BarCodeRecognitionModel) documentRecognizable; 
						for(String documentPattern : barCodeRecognitionModel.getPatterns()){
							String regex = documentPattern.replace("*", "[0-9]"); 
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern.matcher(code);
							
							if(matcher.find()){
								TransposeType orientation = barCodeRecognitionModel.getOrientation();
								if (orientation != null) {
									Content content = document.getContent();
									content.rotacionar(orientation);
								}
								
								DocumentModel model = barCodeRecognitionModel.getDocumentModel();
								document.setDocumentModel(model);
								
								for(ExtractableField extractableField : barCodeRecognitionModel.getFields()){
									Field field = document.getFieldById(extractableField.getFieldModel().getId());
									
									if(field != null){
										try {
											String value = code.substring(extractableField.getIndex(), extractableField.getIndex() + extractableField.getLength());
											field.setValid(true);
											field.setValue(value);
										} catch (IndexOutOfBoundsException e) {
											logger.error("O índice e legth definidos no arquivo de configuração não foram encontrados no valor extraído do QRCode");
											continue;
										}
									}
								}
								
								return true;
							}
						}
					}
				}
			}
		}
		logger.info("Fim da tipificação. O documento não foi tipificado.");
		return false;
	}
	
	private static boolean typingByCmc7(Document document, String cmc7, Recognizable recognizable) {
		if(recognizable instanceof BlankPageRecognitionModel){
			BlankPageRecognitionModel chequeRecognitionModel = (BlankPageRecognitionModel) recognizable;
			
			DocumentModel model = chequeRecognitionModel.getDocumentModel();
			String[] cmc7Part =  cmc7.split(" ");
			
			if (model != null){
				document.setDocumentModel(model);
				if(cmc7Part.length == 3){
				
					Field cmc71 = document.getFieldByName("cmc71");
					if(cmc71 != null){
						cmc71.setValue(cmc7Part[0].length() > 0 ? cmc7Part[0] : "");
						cmc71.setValid(true);
					}
				
					Field cmc72 = document.getFieldByName("cmc72");
					if(cmc72 != null){
						cmc72.setValue(cmc7Part[1].length() > 1 ? cmc7Part[1] : "");
						cmc72.setValid(true);
					}
				
					Field cmc73 = document.getFieldByName("cmc73");
					if(cmc73 != null){
						cmc73.setValue(cmc7Part[2].length() > 2 ? cmc7Part[2] : "");
						cmc73.setValid(ValidationUtil.isCMC7Valid(cmc7Part[0], cmc7Part[1], cmc7Part[2]));
					}
					
					Field cmc7check = document.getFieldByName("cmc7check");
					if(cmc7check != null){
						cmc7check.setValue(cmc7);
					}
				}
				return true;
			}
		}
		return false;
	}

	private static boolean typingByText(Document document, Recognizable recognizable){
		if(recognizable instanceof PositionGroup){
			PositionGroup positionsGroup = (PositionGroup) recognizable;
			
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageUtil.getFileImage(document.getContent().getMainFile());
			} catch(IOException e){
				logger.error("Não foi  possível carregar o arquivo de imagem do documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());
				return false;
			}
			
			for(Position position : positionsGroup.getPositions()){
				int x = position.getX();
				int y = position.getY();
				int width = position.getWidth();
				int height = position.getHeight();
				String text = null;
				
				try{
					BufferedImage subImage = ImageUtil.getSubImage(bufferedImage, x, y, width, height);
					text = OCRService.doOCR(subImage, ExtractionType.TEXT);
				} catch (OCRException e) {
					logger.warn("Não foi possível realizar OCR no documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());
					continue;
				}
				
				if(text != null){
					for(DocumentRecognizable documentRecognizable : position.getDocumentRecognizables()){
						DocumentRecognitionModel documentRecognitionModel = (DocumentRecognitionModel) documentRecognizable; 
						for(String pattern : documentRecognitionModel.getPatterns()){
							if(pattern.equals(text)){
								DocumentModel model = documentRecognitionModel.getDocumentModel();
								document.setDocumentModel(model);
								return true;
							}
						}
					}
				}
				
			}
		}
		return false;
	}
	
	private static boolean typingByQRCode(Document document, Recognizable recognizable) {
		if(recognizable instanceof PositionGroup){
			PositionGroup positionsGroup = (PositionGroup) recognizable;
			
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageUtil.getFileImage(document.getContent().getMainFile());
			} catch(IOException e){
				logger.error("Não foi  possível carregar o arquivo de imagem do documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());
				return false;
			}

			for(Position position : positionsGroup.getPositions()){
				int x = position.getX();
				int y = position.getY();
				int width = position.getWidth();
				int height = position.getHeight();
				String ocrExtract = null;
				
				try{
					BufferedImage subImage = ImageUtil.getSubImage(bufferedImage, x, y, width, height);
					ocrExtract = OCRService.doOCR(subImage, ExtractionType.QRCODE);
				} catch (OCRException e) {
					logger.warn("Erro ao realizar o ocr do typo QRCODE. Message {}", e.getMessage());
					continue;
				}
				
				if(ocrExtract != null){
					String documentPattern = ocrExtract.substring(0, 16);
					for(DocumentRecognizable documentRecognizable : position.getDocumentRecognizables()){
						BarCodeRecognitionModel qrCodeRecognitionModel = (BarCodeRecognitionModel) documentRecognizable;
						for(String pattern : qrCodeRecognitionModel.getPatterns()){
							if(pattern.equals(documentPattern)){
								DocumentModel model = qrCodeRecognitionModel.getDocumentModel();
								document.setDocumentModel(model);
								
								for(ExtractableField qrCodeField : qrCodeRecognitionModel.getFields()){
									Field field = document.getFieldById(qrCodeField.getFieldModel().getId());
									if(field != null){
										//0000000656239
										try {
											String value = ocrExtract.substring(qrCodeField.getIndex(), qrCodeField.getIndex() + qrCodeField.getLength());
											field.setValid(true);
											field.setValue(value);
										} catch (IndexOutOfBoundsException e) {
											logger.error("O índice e legth definidos no arquivo de configuração não foram encontrados no valor extraído do QRCode");
											continue;
										}
										break;
									}
								}
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private static boolean typingByBlankPage(Document document, Recognizable recognizable ) {
		logger.info("Iniciando tipificação de documentos em Branco.");
		
		if(recognizable instanceof BlankPageRecognitionModel){
			BlankPageRecognitionModel blankPageRecognitionModel = (BlankPageRecognitionModel) recognizable;
			
			File imageFile = document.getContent().getMainFile();
			
			BufferedImage image;
			try {
				image = ImageUtil.getFileImage(imageFile);
				double blankRate = ImageUtil.getColorRate(image, Color.WHITE);
				
				if(blankRate > blankPageRecognitionModel.getThreshold()){
					document.setDocumentModel(blankPageRecognitionModel.getDocumentModel());
					return true;
				}
//				long index = image.getHeight() * image.getWidth() / imageFile.length();
//				
//				if (index > ((image.getHeight() + image.getWidth()) / 9.3)){
//					document.setDocumentModel(chequeRecognitionModel.getDocumentModel());
//					return true;
//				}
			} catch (IOException e) {
				logger.error("Arquivo não encontrado. {}", e.getMessage());
			}
		}
		
		return false;
	}
	
	public static BufferedImage rotateClockwise90(BufferedImage src) {
	    int width = src.getWidth();
	    int height = src.getHeight();

	    BufferedImage dest = new BufferedImage(height, width, src.getType());

	    Graphics2D graphics2D = dest.createGraphics();
	    graphics2D.translate((height - width) / 2, (height - width) / 2);
	    graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
	    graphics2D.drawRenderedImage(src, null);

	    return dest;
	}
}