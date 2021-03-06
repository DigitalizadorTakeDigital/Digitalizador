/**
 * 
 */
package br.com.webscanner.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import br.com.webscanner.controller.ImageViewerController.Type;
import br.com.webscanner.exception.ProductConfigurationException;
import br.com.webscanner.exception.SpecificRecognitionXmlBuilderException;
import br.com.webscanner.exception.XmlConfigurationException;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.model.domain.ApplicationData;
import br.com.webscanner.model.domain.ApplicationData.Build;
import br.com.webscanner.model.domain.DocumentGeneric;
import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.domain.Extension;
import br.com.webscanner.model.domain.FieldGeneric;
import br.com.webscanner.model.domain.FieldModel;
import br.com.webscanner.model.domain.FieldType;
import br.com.webscanner.model.domain.Group;
import br.com.webscanner.model.domain.Item;
import br.com.webscanner.model.domain.MenuProduct;
import br.com.webscanner.model.domain.ProcessAgency;
import br.com.webscanner.model.domain.Product;
import br.com.webscanner.model.domain.ProductModel;
import br.com.webscanner.model.domain.Scanner;
import br.com.webscanner.model.domain.Scanner.DriverScanner;
import br.com.webscanner.model.domain.ScannerConfig;
import br.com.webscanner.model.domain.ScannerProperties;
import br.com.webscanner.model.domain.ValidationFunction;
import br.com.webscanner.model.domain.converter.DocumentConverter;
import br.com.webscanner.model.domain.export.Exportable;
import br.com.webscanner.model.domain.export.SystemField;
import br.com.webscanner.model.domain.export.transp01.TRANSP01Export;
import br.com.webscanner.model.domain.export.transp02.TRANSP02Export;
import br.com.webscanner.model.domain.export.xml.DocumentXmlExportModel;
import br.com.webscanner.model.domain.extraction.Extraction;
import br.com.webscanner.model.domain.extraction.ExtractionType;
import br.com.webscanner.model.domain.image.Image;
import br.com.webscanner.model.domain.image.ImageScanned;
import br.com.webscanner.model.domain.preinitialization.PreInitializable;
import br.com.webscanner.model.domain.rule.Rule;
import br.com.webscanner.model.domain.typification.BarCodeRecognitionModel;
import br.com.webscanner.model.domain.typification.BlankPageRecognitionModel;
import br.com.webscanner.model.domain.typification.DefaultRecognitionModel;
import br.com.webscanner.model.domain.typification.DocumentRecognitionModel;
import br.com.webscanner.model.domain.typification.ExtractableField;
import br.com.webscanner.model.domain.typification.Position;
import br.com.webscanner.model.domain.typification.PositionGroup;
import br.com.webscanner.model.domain.typification.Recognition;
import br.com.webscanner.model.domain.typification.Recognizable;
import br.com.webscanner.model.domain.validatable.Validatable;
import br.com.webscanner.model.domain.validator.Validator;
import br.com.webscanner.model.domain.xml.SpecificRecognitionXmlBuilder;
import br.com.webscanner.util.FileManagement;

/**
 * Classe respons??vel pela leitura do XML base dos produtos.
 * @author Jonathan Camara
 */
public class XmlUtil {
	private static Logger logger = LogManager.getLogger(XmlUtil.class.getName());
	
	public static List<MenuProduct> getMenuProducts(){
		logger.info("Recuperando produtos do menu");
		
		InputStream is = null;
		List<MenuProduct> menuProducts = null;
		try {
			menuProducts = new ArrayList<MenuProduct>();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			if(ApplicationData.getBuild() == Build.DESENV){
				is = XmlUtil.class.getClassLoader().getResourceAsStream("br/com/webscanner/resources/xml/menu.xml");
			}else{
				logger.info("urlXml {}", WebScannerConfig.getProperty("urlXml"));
				
				try {
					String environment = ApplicationData.getParam("ambiente");
					String url = WebScannerConfig.getProperty("urlXml");
					if(environment != null){
						url = String.format(url, environment);
					} else{
						url = String.format(url, "DESE");
					}

					logger.info("URL formatada {}", url);
					is = new URL(url).openStream();
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			InputSource source = new InputSource(is);		
			org.w3c.dom.Document doc = builder.parse(source);
			
			NodeList productsNode = doc.getElementsByTagName("product");
			for(int i = 0; i < productsNode.getLength(); i++){
				Element productElement = (Element) productsNode.item(i);
				String id = productElement.getAttribute("id");
				String name = productElement.getAttribute("name");
				String path = productElement.getAttribute("path");
				
				menuProducts.add(new MenuProduct(id, name, path));
			}
		} catch (ParserConfigurationException e) {
			logger.error("Erro ao criar o documentBuilder. Mensagem: {}", e.getMessage());
		} catch (SAXException e) {
			logger.error("Erro ao realizar o parse do xml. Verificar se o xml foi estruturado corretamente. Mensagem: {}", e.getMessage());
		} catch (IOException e) {
			logger.error("N??o foi poss??vel encontrar o arquivo xml especificado ({}).", WebScannerConfig.getProperty("urlXml"));
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					logger.error("Erro ao tentar fechar a conex??o com o stream do xml. Mensagem: {}", e.getMessage());
				}
			}
		}
		return menuProducts;
	}
	
	public static Product getProduct(MenuProduct menuProduct) throws XmlConfigurationException {
		logger.info("Recuperando xml de configura????o do produto {}", menuProduct.getName());
				
		InputStream is = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			try {
				if(ApplicationData.getBuild() == Build.DESENV){
					is = XmlUtil.class.getClassLoader().getResourceAsStream("br/com/webscanner/resources/xml/" + menuProduct.getPath());
				}else{
					try {
						String environment = ApplicationData.getParam("ambiente");
						String url = menuProduct.getPath();
						if(environment != null){
							url = String.format(url, environment);
						} else{
							url = String.format(url, "DESE");
						}
						
						logger.info("URL formatada {}", url);
						logger.info("Recuperando o arquivo xml {}{}", WebScannerConfig.getSourcePath(), url);
						is = new URL(WebScannerConfig.getSourcePath() + url).openStream();
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
			} catch (Exception e) {
				logger.error("N??o foi possivel encontrar o arquivo de leitura. {}", menuProduct.getPath());
			}
			
			InputSource source = new InputSource(is);
			org.w3c.dom.Document doc = builder.parse(source);
			
			List<DocumentModel> documents = new ArrayList<DocumentModel>();
			List<DocumentGeneric> documentsGeneric = new ArrayList<DocumentGeneric>();
			NodeList productsNode = doc.getElementsByTagName("product");
			
			String produtoParametro = menuProduct.getId();
			if (ApplicationData.paramExists("subProduto")) {
				produtoParametro = ApplicationData.getParam("subProduto");
			}
			
			for (int index = 0; index < productsNode.getLength(); index++) {
				Element productElement = (Element) productsNode.item(index);
				String productId = productElement.getAttribute("id");
				
				if (productId.equals(produtoParametro)) {
					String productName = productElement.getAttribute("name");
					
					NodeList modelNode = productElement.getElementsByTagName("model");
					Element modelElement = (Element) modelNode.item(0);
					
					double scale = 0.0;
					if(!modelElement.getAttribute("scale").trim().isEmpty()){
						try {
							NumberFormat numberFormat = DecimalFormat.getPercentInstance();
							scale = numberFormat.parse(modelElement.getAttribute("scale")).doubleValue();
						} catch (ParseException e1) {
							logger.error("N??o foi poss??vel converter a escala do produto. {}", e1.getMessage());
						}
					}
					
					Type imageType = Type.TIF;
					try {
						imageType = Type.valueOf(modelElement.getAttribute("defaultImageType"));
					} catch (IllegalArgumentException e) {
						logger.warn("O image type nao existe. Definido como TIF");
					}
					
					boolean splitDuplex = Boolean.parseBoolean(modelElement.getAttribute("splitDuplex"));
					boolean exportJpg = Boolean.parseBoolean(modelElement.getAttribute("exportJpg"));
					boolean exitAfterProcessed = Boolean.parseBoolean(modelElement.getAttribute("exitAfterProcessed"));
					
					List<Scanner.DriverScanner> drivers = new ArrayList<Scanner.DriverScanner>();
					NodeList scannableNodeList = modelElement.getElementsByTagName("scannable");
					if (scannableNodeList.getLength() > 0) {
						Element scannableElement = (Element) scannableNodeList.item(0);
						if (scannableElement.hasAttribute("tipo")) {
							String[] driversPermitidos = scannableElement.getAttribute("tipo").split(",");
							for (String driver : driversPermitidos) {
								try {
									drivers.add(DriverScanner.valueOf(driver.trim()));
								} catch (IllegalArgumentException e) {
									logger.error("O tipo de scanner n??o foi encontrado");
									return null;
								}
							}
						} else {
							drivers = new ArrayList<DriverScanner>(Arrays.asList(DriverScanner.values()));
							drivers.remove(DriverScanner.MOCK);
						}
					}
						
					boolean importable = modelElement.getElementsByTagName("importable").getLength() > 0 ? true : false;
					
					
					
					boolean brightness = modelElement.getElementsByTagName("brightness").getLength() > 0 ? true : false;
					
					NodeList scannerConfigNode = modelElement.getElementsByTagName("scannerConfigurable");
										
					boolean grayScale = false;
					boolean doubleSensor = false;
					boolean blackPageConfig = false;
					
					for (int i = 0; i < scannerConfigNode.getLength(); i++) {
						Element scannerConfigElement = (Element) scannerConfigNode.item(index);
						grayScale = Boolean.parseBoolean(scannerConfigElement.getAttribute("grayScale"));
						doubleSensor = Boolean.parseBoolean(scannerConfigElement.getAttribute("doubleSensor"));
						blackPageConfig = Boolean.parseBoolean(scannerConfigElement.getAttribute("blackPageConfig"));
					}
					
					List<Extension> extensions = new ArrayList<Extension>();
					NodeList extensionList = modelElement.getElementsByTagName("extension");
					for(int i = 0; i < extensionList.getLength(); i++){
						Element extensionElement = (Element)extensionList.item(i);
						
						boolean duplex = Boolean.parseBoolean(extensionElement.getAttribute("duplex"));
						boolean bothRequired = Boolean.parseBoolean(extensionElement.getAttribute("bothRequired"));
						String type = extensionElement.getAttribute("type");
						String description = extensionElement.getAttribute("description");
						int maxSize = Integer.parseInt(extensionElement.getAttribute("maxSize"));
						int minSize = Integer.parseInt(extensionElement.getAttribute("minSize"));
						
						Extension extension = new Extension(type, description, maxSize, minSize, duplex, bothRequired);
						for(String ext : extensionElement.getAttribute("ext").split(",")){
							extension.addExtension(ext.trim().toLowerCase());
						}
						
						extensions.add(extension);
					}
					
					NodeList groupNode = productElement.getElementsByTagName("groups");
					Element groupElement = (Element)groupNode.item(0);
					
					NodeList documentsNode = groupElement.getElementsByTagName("document");
					for(int j = 0; j < documentsNode.getLength(); j++){
						Element documentElement = (Element) documentsNode.item(j);
						Group group = extractDocumentGroup(documentElement.getParentNode());
						documents.add(extractDocumentModel(documentElement, group));					
					}
					
					NodeList documentsGenericNode = groupElement.getElementsByTagName("documentGeneric");
					for (int j = 0; j < documentsGenericNode.getLength(); j++) {
						Element documentGenericElement = (Element) documentsGenericNode.item(j);
						Group group = extractDocumentGroup(documentGenericElement.getParentNode());
						documentsGeneric.add(extractDocumentGeneric(documentGenericElement, group));
					}
					
							
					NodeList exportationNode = productElement.getElementsByTagName("exportation");
					Element exportationElement = (Element)exportationNode.item(0);
					List<Exportable> exports = null;
					if(exportationElement != null){
						NodeList exportsNode = exportationElement.getElementsByTagName("export");
						exports = new ArrayList<Exportable>();
						for(int j = 0; j < exportsNode.getLength(); j++){
							Element exportElement = (Element) exportsNode.item(j);
							exports.add(extractProductExport(exportElement));
						}
					}
					
					NodeList typificationNode = productElement.getElementsByTagName("typification");
					Element typificationElement = (Element) typificationNode.item(0);
					List<Recognition> recognitions = new ArrayList<Recognition>();;
					if(typificationElement != null){
						NodeList recognitionNode = typificationElement.getElementsByTagName("recognition");
						
						for(int i = 0; i < recognitionNode.getLength(); i++){
							Element recognitionElement = (Element) recognitionNode.item(i);
							recognitions.add(extractRecognition(recognitionElement, documents));
						}
					}
					
					NodeList validationNode = productElement.getElementsByTagName("validationGroup");
					Validatable validatable = null;
					if(validationNode.getLength() > 0){
						Element validationElement = (Element) validationNode.item(0);
						String type = validationElement.getAttribute("type");
						String param = validationElement.getAttribute("param");
						String x;
						
						try {
							@SuppressWarnings("unchecked")
							Class<Validatable> clazz = (Class<Validatable>) Class.forName("br.com.webscanner.model.domain.validatable." + type + "Validation");
							if(param != null && !param.isEmpty()) {
								String [] parameters = param.split(",");
								Object [] constructorParameters = new Object[parameters.length];
								
								@SuppressWarnings("unchecked")
								Constructor<Validatable> constructor = (Constructor<Validatable>) clazz.getConstructors()[0];
								
								for(int i = 0; i < constructor.getParameterTypes().length; i++){
									constructorParameters[i] = constructor.getParameterTypes()[i].getConstructor(String.class).newInstance(parameters[i].trim());
								}
								
								validatable = constructor.newInstance(constructorParameters);
							} else {
								validatable = clazz.asSubclass(Validatable.class).newInstance();
							}
						} catch (ClassNotFoundException e) {
							logger.error("Erro ao criar inst??ncia da classe de valida????o. {}", e.getMessage());
						} catch (InstantiationException e) {
							logger.error("Erro ao criar inst??ncia da classe de valida????o. {}", e.getMessage());
						} catch (IllegalAccessException e) {
							logger.error("Erro ao criar inst??ncia da classe de valida????o. {}", e.getMessage());
						} catch (SecurityException e) {
							logger.error("Erro ao criar inst??ncia da classe de valida????o. {}", e.getMessage());
						} catch (IllegalArgumentException e) {
							logger.error("Erro ao criar inst??ncia da classe de valida????o. {}", e.getMessage());
						} catch (InvocationTargetException e) {
							logger.error("Erro ao criar inst??ncia da classe de valida????o. {}", e.getMessage());
						} catch (NoSuchMethodException e) {
							logger.error("Erro ao criar inst??ncia da classe de valida????o. {}", e.getMessage());
						}
					}
					
					NodeList preInitializationNode = productElement.getElementsByTagName("preInitialization");
					PreInitializable preInitializable = null;
					if(preInitializationNode.getLength() > 0){
						Element preInitializationElement = (Element) preInitializationNode.item(0);
						String type = preInitializationElement.getAttribute("type");
						
						try {
							@SuppressWarnings("unchecked")
							Class<PreInitializable> clazz = (Class<PreInitializable>) Class.forName("br.com.webscanner.model.domain.preinitialization." + type + "PreInitialization");
							preInitializable = clazz.asSubclass(PreInitializable.class).newInstance();
						} catch (ClassNotFoundException e) {
							logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
						} catch (InstantiationException e) {
							logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
						} catch (IllegalAccessException e) {
							logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
						} catch (SecurityException e) {
							logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
						} catch (IllegalArgumentException e) {
							logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
						}
					}
					ScannerConfig  scannerConfig = new ScannerConfig(grayScale, doubleSensor, blackPageConfig);
					
					ProductModel productModel = new ProductModel(splitDuplex, exportJpg, exitAfterProcessed, scale, documents, documentsGeneric, exports, recognitions, validatable, preInitializable, drivers, importable, scannerConfig , brightness);
					productModel.setType(imageType);
					productModel.setExtensions(extensions);
					Product product = new Product(productId, productName);
					product.setModel(productModel);
					return product;
				}
			}
		} catch (ParserConfigurationException e) {
			logger.error("Erro ao criar o documentBuilder. Mensagem: {}", e.getMessage());
		} catch (SAXException e) {
			logger.error("Erro ao realizar o parse do xml. Verificar se o xml foi estruturado corretamente. Mensagem: {}", e.getMessage());
		} catch (IOException e) {
			logger.error("N??o foi poss??vel encontrar o arquivo xml especificado ({}). Mensagem: {}", WebScannerConfig.getProperty("xml"), e.getMessage());
		} finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					logger.error("Erro ao tentar fechar a conex??o com o stream do xml. Mensagem: {}", e.getMessage());
				}
			}
		}
		return null;
	}

	private static Recognition extractRecognition(Element recognitionElement, List<DocumentModel> documentModels) throws XmlConfigurationException {
		ExtractionType extractionType = ExtractionType.NONE;
		String type = recognitionElement.getAttribute("type").trim();
		
		try {
			extractionType = ExtractionType.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			logger.error("O tipo de extra????o especificado n??o existe '{}'. {}", extractionType, e.getMessage());
		}
		
		Recognizable recognizable = null;
		NodeList documentNode = null;
		switch (extractionType) {
			case SPECIFIC:
				String className = recognitionElement.getAttribute("class").trim();
				try {
					@SuppressWarnings("unchecked")
					Class<SpecificRecognitionXmlBuilder> specificClass = (Class<SpecificRecognitionXmlBuilder>) Class.forName("br.com.webscanner.model.domain.xml." + className);
					SpecificRecognitionXmlBuilder specific = specificClass.asSubclass(SpecificRecognitionXmlBuilder.class).newInstance();
					
					recognizable = specific.build(recognitionElement);
				} catch (ClassNotFoundException e) {
					logger.error("Erro ao iniciar a classe especifica de reconhecimento. {}", e.getMessage());
					throw new XmlConfigurationException("Erro ao iniciar a classe especifica de reconhecimento.");
				} catch (InstantiationException e) {
					logger.error("Erro ao iniciar a classe especifica de reconhecimento. {}", e.getMessage());
					throw new XmlConfigurationException("Erro ao iniciar a classe especifica de reconhecimento.");
				} catch (IllegalAccessException e) {
					logger.error("Erro ao iniciar a classe especifica de reconhecimento. {}", e.getMessage());
					throw new XmlConfigurationException("Erro ao iniciar a classe especifica de reconhecimento.");
				} catch (SpecificRecognitionXmlBuilderException e) {
					logger.error("Erro ao iniciar a classe especifica de reconhecimento. {}", e.getMessage());
					throw new XmlConfigurationException("Erro ao iniciar a classe especifica de reconhecimento.");
				} 
				break;
			case CMC7:
			case BLANKPAGE:
				documentNode = recognitionElement.getElementsByTagName("document");
				double threshold = 0.95d;
				double size = 0.00d;
				try {
					threshold = DecimalFormat.getPercentInstance().parse(recognitionElement.getAttribute("threshold")).doubleValue();
					size = DecimalFormat.getInstance().parse(recognitionElement.getAttribute("size")).doubleValue();
				} catch (ParseException e1) {
					logger.warn("N??o foi encontrado a attr threshold na configura????o de pagina em branco. Setado como default 95%.");
				}
				recognizable = new BlankPageRecognitionModel(getRecognitionDocumentModel(documentNode, documentModels), threshold, size);
				break;
			case DEFAULT:
				documentNode = recognitionElement.getElementsByTagName("document");
				recognizable = new DefaultRecognitionModel(getRecognitionDocumentModel(documentNode, documentModels));
				break;
			case TEXT:
				PositionGroup positionGroup = new PositionGroup();
				NodeList positionNode = recognitionElement.getElementsByTagName("position");
				for(int j = 0; j < positionNode.getLength(); j++){
					Element positionElement = (Element) positionNode.item(j);

					try {
						int x = positionElement.getAttribute("x").isEmpty() ? 0 : Integer.parseInt(positionElement.getAttribute("x"));
						int y = positionElement.getAttribute("y").isEmpty() ? 0 : Integer.parseInt(positionElement.getAttribute("y"));
						int width = positionElement.getAttribute("width").isEmpty() ? 0 : Integer.parseInt(positionElement.getAttribute("width"));
						int height = positionElement.getAttribute("height").isEmpty() ? 0 : Integer.parseInt(positionElement.getAttribute("height"));
						Position position = new Position(x, y, width, height);

						documentNode = positionElement.getElementsByTagName("document");
						for(int k = 0; k < documentNode.getLength(); k++){
							Element documentElement = (Element) documentNode.item(k);
							long id = Long.parseLong(documentElement.getAttribute("id"));

							for(DocumentModel model : documentModels){
								if(model.getId() == id){
									DocumentRecognitionModel documentRecognitionModel = new DocumentRecognitionModel(model);
									for(String pattern : Arrays.asList(documentElement.getAttribute("pattern").split(","))){
										if(!pattern.trim().isEmpty()){
											documentRecognitionModel.addPattern(pattern.trim());
										}
									}
									position.addDocumentRecognizable(documentRecognitionModel);
									break;
								}
							}
						}
						positionGroup.addPosition(position);
					} catch (NumberFormatException e) {
						logger.error("Erro ao realizar o parse para inteiro das coordenadas do campo de extra????o do field. {}", e.getMessage());
					}
				}
				recognizable = positionGroup;
				break;
			case CODE128:
			case ITF:
			case CODE39:
			case QRCODE:
				positionGroup = new PositionGroup();
				positionNode = recognitionElement.getElementsByTagName("position");
				for(int j = 0; j < positionNode.getLength(); j++){
					Element positionElement = (Element) positionNode.item(j);
		
					try {
						int x = positionElement.getAttribute("x").isEmpty() ? 0 : Integer.parseInt(positionElement.getAttribute("x"));
						int y = positionElement.getAttribute("y").isEmpty() ? 0 : Integer.parseInt(positionElement.getAttribute("y"));
						int width = positionElement.getAttribute("width").isEmpty() ? 0 : Integer.parseInt(positionElement.getAttribute("width"));
						int height = positionElement.getAttribute("height").isEmpty() ? 0 : Integer.parseInt(positionElement.getAttribute("height"));
						Position position = new Position(x, y, width, height);
						
						documentNode = positionElement.getElementsByTagName("document");
						for(int k = 0; k < documentNode.getLength(); k++){
							Element documentElement = (Element) documentNode.item(k);
							long id = Long.parseLong(documentElement.getAttribute("id"));
							TransposeType orientation = documentElement.getAttribute("orientation").isEmpty() ? null : getTransposeType(documentElement.getAttribute("orientation").trim());
							BarCodeRecognitionModel barCodeRecognitionModel = null;
							
							for(DocumentModel model : documentModels){
								if(model.getId() == id){
									barCodeRecognitionModel = new BarCodeRecognitionModel(model, orientation);

									for(String pattern : Arrays.asList(documentElement.getAttribute("pattern").split(","))){
										if(!pattern.trim().isEmpty()){
											barCodeRecognitionModel.addPattern(pattern.trim());
										}
									}

									NodeList fieldNode = documentElement.getElementsByTagName("field");
									for(int i = 0; i < fieldNode.getLength(); i++){
										Element fieldElement = (Element) fieldNode.item(i);
										
										int fieldModelId = Integer.parseInt(fieldElement.getAttribute("id"));
										for(FieldModel fieldModel : model.getFields()){
											if(fieldModel.getId() == fieldModelId){
												ExtractableField extractableField = new ExtractableField();
												extractableField.setFieldModel(fieldModel);
												extractableField.setIndex(Integer.parseInt(fieldElement.getAttribute("index")));
												extractableField.setLength(Integer.parseInt(fieldElement.getAttribute("length")));
												barCodeRecognitionModel.addExtractableField(extractableField);
												break;
											}
										}
									}
									position.addDocumentRecognizable(barCodeRecognitionModel);
									break;
								}
							}
							
							if (barCodeRecognitionModel == null) {
								barCodeRecognitionModel = buildBarcodeRecognitionModelGeneric(position, id, orientation, documentElement);
								position.addDocumentRecognizable(barCodeRecognitionModel);
							}
						}
						positionGroup.addPosition(position);
					} catch (NumberFormatException e) {
						logger.error("Erro ao realizar o parse para inteiro das coordenadas do campo de extra????o do field. {}", e.getMessage());
					} catch (ProductConfigurationException e) {
						logger.error(e.getMessage());
					}
				}
				recognizable = positionGroup;
				break;
			default:
				break;
				
		}
		return new Recognition(extractionType, recognizable);
	}

	private static DocumentModel extractDocumentModel(Element documentElement, Group group) throws XmlConfigurationException {
		try {
			long documentId = Long.parseLong(documentElement.getAttribute("id").trim());
			String documentName = documentElement.getAttribute("name").trim();
			String documentDisplayName = documentElement.getAttribute("displayName").trim();
			String documentTableDisplay = documentElement.getAttribute("tableDisplay").trim();
			int icor = Integer.parseInt(documentElement.getAttribute("icor").trim());			
			
			DocumentModel model = new DocumentModel(documentId, documentName, documentDisplayName, documentTableDisplay, icor, group);
			
			logger.info("Iniciando a extra????o dos modelos de fields");
			NodeList fieldsNode = documentElement.getElementsByTagName("field");
			for(int k = 0; k < fieldsNode.getLength(); k++){						
				Element fieldElement = (Element) fieldsNode.item(k);
				model.addField(extractFieldModel(fieldElement));
			}
			
			NodeList extractionNode = documentElement.getElementsByTagName("extraction");
			for(int i = 0; i < extractionNode.getLength(); i++){
				Element extractionElement = (Element) extractionNode.item(i);
				ExtractionType extractionType = ExtractionType.NONE;
				String extractType = extractionElement.getAttribute("type").trim();
				
				try {
					extractionType = ExtractionType.valueOf(extractType.toUpperCase());
				} catch (IllegalArgumentException e) {
					logger.error("O tipo de extra????o especificado n??o existe '{}'. {}", extractionType, e.getMessage());
				}
				
				try {
					int x = Integer.parseInt(extractionElement.getAttribute("x").trim());
					int y = Integer.parseInt(extractionElement.getAttribute("y").trim());
					int w = Integer.parseInt(extractionElement.getAttribute("width").trim());
					int h = Integer.parseInt(extractionElement.getAttribute("height").trim());
					
					Extraction extraction = new Extraction(extractionType, x, y, w, h);

					NodeList extractableFieldNode = extractionElement.getElementsByTagName("extractableField");
					for(int k = 0; k < extractableFieldNode.getLength(); k++){
						Element extractableFieldElement = (Element) extractableFieldNode.item(k);
						int id = Integer.parseInt(extractableFieldElement.getAttribute("id").trim());
						
						for(FieldModel fieldModel : model.getFields()){
							if(fieldModel.getId() == id){
								ExtractableField extractableField = new ExtractableField();
								extractableField.setFieldModel(fieldModel);
								extractableField.setIndex(!extractableFieldElement.getAttribute("index").isEmpty() ? Integer.parseInt(extractableFieldElement.getAttribute("index")) : 0);
								extractableField.setLength(!extractableFieldElement.getAttribute("length").isEmpty() ? Integer.parseInt(extractableFieldElement.getAttribute("length")) : 0);
								extraction.addExtractableField(extractableField);
								break;
							}
						}
					}
					model.addExtraction(extraction);
				} catch (NumberFormatException e) {
					logger.info("Erro ao realizar o parse para inteiro das coordenadas do campo de extra????o do field. {}", e.getMessage());
				}
			}
			
			NodeList functionNodeList = documentElement.getElementsByTagName("function");
			for(int k = 0; k < functionNodeList.getLength(); k++){						
				Element functionElement = (Element) functionNodeList.item(k);
				
				String message = functionElement.getAttribute("message");
				String function = functionElement.getAttribute("function");
				
				model.addValidationFunction(new ValidationFunction(function, message));
			}
			Validator validator = new Validator();
			model.validate(validator);
			if (validator.hasError()) {
				logger.error(validator.getMessages().toString());
				throw new XmlConfigurationException(validator.getMessages().toString());
			}

			//TODO 05/05
			Rule rule = extractRule(documentElement);
			model.setRule(rule);
			
			return model;
		} catch (NumberFormatException e) {
			logger.error("Erro ao realizar a convers??o do Id do DocumentModel. {}", e.getMessage());
		}
		return null;
	}

	private static Exportable extractProductExport(Element exportElement) {
		String type = exportElement.getAttribute("type");		
		
		 if (type.equalsIgnoreCase("TRANSP01")) {
			String pathExport = exportElement.getAttribute("pathExport");
			String pathExportCSV = exportElement.getAttribute("pathExportCSV");
			String pathExportRomaneio = exportElement.getAttribute("pathExportRomaneio");
			return new TRANSP01Export(pathExport, pathExportCSV, pathExportRomaneio);			
		}
		 if (type.equalsIgnoreCase("TRANSP02")) {
			String pathExport = exportElement.getAttribute("pathExport");
			return new TRANSP02Export(pathExport);			
		}		
		
		return null;
	}


	/**
	 * Faz a leitura dos dados de um Field e popula um objeto imut??vel de FieldModel.
	 * @param fieldElement 
	 * @return - {@link FieldModel}
	 */
	private static FieldModel extractFieldModel(Element fieldElement) {
		List<Item> items = null;
		int fieldId = Integer.parseInt(fieldElement.getAttribute("id"));
		String fieldName = fieldElement.getAttribute("name");
		String displayName = fieldElement.getAttribute("displayName");
		String type = fieldElement.getAttribute("type");
		FieldType fieldType = FieldType.TEXT;
		
		if(type.trim().equals("combo")){
			fieldType = FieldType.COMBO;
			items = new ArrayList<Item>();
			if(!fieldElement.getAttribute("items").trim().isEmpty()){
				for (String item : fieldElement.getAttribute("items").split(";")) {
					try{
						String[] split = item.split(",");
						Item i = new Item(split[0].trim(), split[1].trim());
						items.add(i);	
					}catch (ArrayIndexOutOfBoundsException e) {
						logger.error("Erro ao criar items do combobox. {}" , item);
					}
				}
				Collections.sort(items);
			}else{
				logger.error("Ao definir um field type como 'combo' o atributo 'items' ?? obrigat??rio.");
			}
		}else if(type.trim().equals("numericLeadingZero")){
			fieldType = FieldType.NUMERIC_LEADING_ZERO;
		}else{
			try {
				fieldType = FieldType.valueOf(type.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
				logger.error("O Tipo de field especificado n??o existe '{}'. {}", type, e.getMessage());
			}
		}
		
		int fieldMaxLength = fieldElement.getAttribute("maxlength").isEmpty() ? 0 : Integer.parseInt(fieldElement.getAttribute("maxlength"));
		
		boolean hidden = Boolean.parseBoolean(fieldElement.getAttribute("hidden"));
		boolean readonly = Boolean.parseBoolean(fieldElement.getAttribute("readonly"));
		boolean required = Boolean.parseBoolean(fieldElement.getAttribute("required"));
		boolean fixedSize = Boolean.parseBoolean(fieldElement.getAttribute("fixedsize"));
		boolean greaterThanZero = Boolean.parseBoolean(fieldElement.getAttribute("greaterThanZero"));
		String validationMethod = fieldElement.getAttribute("validationMethod");
		
		String dependents = fieldElement.getAttribute("dependents");
		List<String> dependentsList = null;
		if (!dependents.isEmpty()) {
			dependentsList = Arrays.asList(dependents.split(","));
		}
		
		FieldModel fieldModel = new FieldModel(fieldId, fieldName, displayName, fieldType, fieldMaxLength, items, hidden, readonly, required, validationMethod, fixedSize, greaterThanZero, dependentsList);
		
		return fieldModel;
	}

	/**
	 * Transforma o atributo configurado no xml no enum utilizado pelo JAI para realizar a transposi????o da imagem.
	 * @param orientation - Orienta????o da imagem (90, 180 ou 270);
	 * @return {@link TransposeType} enum 
	 * @throws ProductConfigurationException
	 */
	private static TransposeType getTransposeType(String orientation) throws ProductConfigurationException {
		try {
			int angle = Integer.parseInt(orientation);
			
			switch (angle) {
				case 90:
					return TransposeDescriptor.ROTATE_90;
				case 180:
					return TransposeDescriptor.ROTATE_180;
				case 270:
					return TransposeDescriptor.ROTATE_270;
				default:
					return null;
			}
		} catch (NumberFormatException e) {
			throw new ProductConfigurationException(String.format("Erro ao recuperar o angulo de orienta????o. {}. message: {}", orientation, e.getMessage()));
		}
	}
	
	/**
	 * @param product
	 * @param agency
	 * @param scanner
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File generateXMLASRH(Product product, ProcessAgency agency, ScannerProperties scanner, String filePath, String fileName) throws IOException {
		StringBuilder xml = new StringBuilder();
		XStream xstream = new XStream(new DomDriver("UTF-8"));

		File file = new File(filePath, fileName + ".xml");

		xstream.autodetectAnnotations(true);
		xstream.omitField(ImageScanned.class, "jpg");
		xstream.omitField(Image.class, "rear");
		xstream.aliasSystemAttribute(null, "class");
		
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		xml.append("<xml>\n");
		xml.append("<app name=\"destroyer\"/>\n");
		xml.append("<lot id=\"0000000000\"/>\n");
		xml.append(xstream.toXML(agency));
		xml.append("\n");
		xml.append(xstream.toXML(scanner));
		xml.append("\n");
		xml.append(xstream.toXML(product));
		xml.append("\n</xml>");

		return FileManagement.write(xml.toString().replaceAll("<documents>", "").replaceAll("</documents>", "").getBytes("UTF-8"), file.getAbsolutePath());
	}
	
	public static void writeXstream(Object obj, File file) throws Exception{
		XStream xstream = new XStream(new DomDriver());
		String xml = xstream.toXML(obj);
		FileManagement.write(xml, file);
	}
	
	public static Object readXstream(File file) throws Exception{
		XStream xstream = new XStream(new DomDriver());
		return xstream.fromXML(file);
	}
	
	/**
	 * Cria um {@link XMLData} informando o root do xml
	 * @param root - Root do xml
	 * @return {@link XMLData}
	 */
	public static XMLData createXml(String root){
		return new XMLData(root);
	}
	
	/**
	 * Cria um {@link XMLData} informando o root do xml e o encoding do xml
	 * @param root - Root do xml
	 * @param encoding - Encoding do xml
	 * @return {@link XMLData}
	 */
	public static XMLData createXml(String root, String encoding){
		return new XMLData(root, encoding);
	}

	public static File generateXML(Product product, ProcessAgency agency, ScannerProperties scanner, String fileName, List<DocumentXmlExportModel> models) throws IOException{
		StringBuilder xml = new StringBuilder();
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		
		xstream.registerConverter(new DocumentConverter(models));
		
		String fileSeparator = System.getProperty("file.separator"); 
		
		File file = new File(System.getProperty("user.home") + fileSeparator
				+ WebScannerConfig.getProperty("pathImage") + fileSeparator 
				+ fileSeparator + fileName + ".xml");

		xstream.autodetectAnnotations(true);
		
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		xml.append("<xml>\n");
		xml.append("<app name=\"destroyer\"/>\n");
		xml.append("<lot id=\"0000000000\"/>\n");
		xml.append(xstream.toXML(agency));
		xml.append("\n");
		xml.append(xstream.toXML(scanner));
		xml.append("\n");
		xml.append(xstream.toXML(product));
		xml.append("\n</xml>");

		return FileManagement.write(xml.toString().getBytes("UTF-8"), file.getAbsolutePath());
	}
	
	/**
	 * Retorna o modelo de documento definido pelo atributo "id" no n?? <b>document</b>, filho do n?? <b>recognition</b>.
	 * @param documentNode {@link NodeList} que cont??m o n?? <b>document</b> cujo atributo ?? o "id" do modelo a ser recuperado.
	 * @param documentModels Lista de {@link DocumentModel} com os quais o "id" obtido ser?? comparado.
	 * @return {@link DocumentModel}
	 */
	private static DocumentModel getRecognitionDocumentModel(NodeList documentNode, List<DocumentModel> documentModels){
		try{
			for(int k = 0; k < documentNode.getLength(); k++){
				Element documentElement = (Element) documentNode.item(k);
				long id = Long.parseLong(documentElement.getAttribute("id"));

				for(DocumentModel model : documentModels){
					if(model.getId() == id){
						return model;
					}
				}
			}
		} catch(NumberFormatException e) {
			logger.warn("N??o foi poss??vel converter o ID do produto: {}", e.getMessage());
		}
		return null;
	}
	
	
	/**
	 * Cria um {@link DocumentGeneric} a partir das informa????es contidas em um {@link Element} do tipo <b>documentGeneric</b>
	 * @param documentGenericElement {@link Element} do tipo <b>documentGeneric</b>
	 * @return {@link DocumentGeneric}
	 */
	private static DocumentGeneric extractDocumentGeneric(Element documentGenericElement, Group group){
		logger.info("Iniciando extra????o de documento gen??rico");
		DocumentGeneric documentGeneric = null;
		
		long id;
		String name;
		String displayName;
		String tableDisplay;
		int icor;
		
		try{
			id = Long.parseLong(documentGenericElement.getAttribute("id").trim());
		} catch(NumberFormatException e){
			logger.error("Erro ao realizar parse no atributo 'id': {}", e.getMessage());
			return null;
		}
		name = documentGenericElement.getAttribute("name").trim();
		displayName = documentGenericElement.getAttribute("displayName").trim();
		tableDisplay = documentGenericElement.getAttribute("tableDisplay").trim();
		try{
			icor = Integer.parseInt(documentGenericElement.getAttribute("icor").trim());
		} catch(NumberFormatException e){
			logger.error("Erro ao realizar parse no atributo 'icor': {}", e.getMessage());
			return null;
		}
		documentGeneric = new DocumentGeneric(id, name, displayName, tableDisplay, icor, group);


		logger.info("Iniciando a extra????o de fields gen??ricos.");
		NodeList fieldsGenericNode = documentGenericElement.getElementsByTagName("fieldGeneric");
		for(int k = 0; k < fieldsGenericNode.getLength(); k++){						
			Element fieldGenericElement = (Element) fieldsGenericNode.item(k);
			documentGeneric.addField(extractFieldGeneric(fieldGenericElement));
		}
		
		NodeList functionNodeList = documentGenericElement.getElementsByTagName("function");
		for(int k = 0; k < functionNodeList.getLength(); k++){						
			Element functionElement = (Element) functionNodeList.item(k);
			
			String message = functionElement.getAttribute("message");
			String function = functionElement.getAttribute("function");
			
			documentGeneric.addValidationFunction(new ValidationFunction(function, message));
		}
		
		//TODO 05/05
		Rule rule = extractRule(documentGenericElement);
		documentGeneric.setRule(rule);
		
		logger.info("T??rmino da extra????o do documento gen??rico.");
		return documentGeneric;
	}
	
	/**
	 * Cria um {@link FieldGeneric} a partir das informa????es contidas em um {@link Element} do tipo <b>fieldGeneric</b>
	 * @param fieldGenericElement {@link Element} do tipo <b>fieldGeneric</b>
	 * @return {@link FieldGeneric}
	 */
	private static FieldGeneric extractFieldGeneric(Element fieldGenericElement){
		int id;
		try{
			id = Integer.parseInt(fieldGenericElement.getAttribute("id").trim());
		} catch(NumberFormatException e){
			logger.error("Erro ao realizar parse no atributo 'id': {}", e.getMessage());
			return null;
		}
		String name = fieldGenericElement.getAttribute("name").trim();
		String displayName = fieldGenericElement.getAttribute("displayName").trim();
		String type = fieldGenericElement.getAttribute("type").trim();
		
		List<Item> items = null;
		FieldType fieldType = FieldType.TEXT;
		if(type.trim().equals("combo")){
			fieldType = FieldType.COMBO;
			items = new ArrayList<Item>();
			if(!fieldGenericElement.getAttribute("items").trim().isEmpty()){
				for (String item : fieldGenericElement.getAttribute("items").split(";")) {
					try{
						String[] split = item.split(",");
						Item i = new Item(split[0].trim(), split[1].trim());
						items.add(i);	
					}catch (ArrayIndexOutOfBoundsException e) {
						logger.error("Erro ao criar items do combobox. {}" , item);
					}
				}
				Collections.sort(items);
			}else{
				logger.warn("N??o foi definido o atributo 'items' para o campo gen??rico {}", displayName);
			}
		}else if(type.trim().equals("numericLeadingZero")){
			fieldType = FieldType.NUMERIC_LEADING_ZERO;
		} else {
			try {
				fieldType = FieldType.valueOf(type.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
				logger.error("O Tipo de field especificado n??o existe '{}'. {}", type, e.getMessage());
			}
		}
		
		int maxLength = 0;
		try{
			maxLength = fieldGenericElement.getAttribute("maxlength").trim().isEmpty() ? 0 : Integer.parseInt(fieldGenericElement.getAttribute("maxlength").trim());
		} catch(NumberFormatException e){
			logger.error("Erro ao realizar parse no atributo 'maxLength': {}", e.getMessage());
			return null;
		}
		
		boolean hidden = Boolean.parseBoolean(fieldGenericElement.getAttribute("hidden").trim());
		boolean readonly = Boolean.parseBoolean(fieldGenericElement.getAttribute("readonly").trim());
		boolean required = Boolean.parseBoolean(fieldGenericElement.getAttribute("required").trim());
		boolean fixedSize = Boolean.parseBoolean(fieldGenericElement.getAttribute("fixedsize").trim());
		
		return new FieldGeneric(id, name, displayName, fieldType, maxLength, items, hidden, readonly, required, fixedSize);
	}
	
	private static Group extractDocumentGroup(Node node){
		Element groupElement = (Element)node;
		int id;
		String name;
		
		try{
			id = Integer.parseInt(groupElement.getAttribute("id").trim());
		} catch(NumberFormatException e){
			logger.error("Erro ao realizar parse no atributo 'id': {}", e.getMessage());
			return null;
		}
		name = groupElement.getAttribute("name").trim();
		return new Group(id, name);
	}

	
	private static BarCodeRecognitionModel buildBarcodeRecognitionModelGeneric (Position position, long documentId, TransposeType orientation, Element documentElement) {
		logger.info("Iniciando build de BarcodeRecognitionModel gen??rico para documentId {}", documentId);
		BarCodeRecognitionModel barCodeRecognitionModel = new BarCodeRecognitionModel(documentId, orientation);

		for(String pattern : Arrays.asList(documentElement.getAttribute("pattern").split(","))){
			if(!pattern.trim().isEmpty()){
				barCodeRecognitionModel.addPattern(pattern.trim());
			}
		}

		NodeList fieldNode = documentElement.getElementsByTagName("field");
		for(int i = 0; i < fieldNode.getLength(); i++){
			Element fieldElement = (Element) fieldNode.item(i);

			int fieldModelId = Integer.parseInt(fieldElement.getAttribute("id").trim());
			ExtractableField extractableField = new ExtractableField(fieldModelId);
			extractableField.setIndex(Integer.parseInt(fieldElement.getAttribute("index")));
			extractableField.setLength(Integer.parseInt(fieldElement.getAttribute("length")));
			barCodeRecognitionModel.addExtractableField(extractableField);
		}
		logger.info("T??rmino do build");
		return barCodeRecognitionModel;
	}
	
	private static Rule extractRule (Element element) {
		NodeList nodeList = element.getElementsByTagName("rule");
		
		Rule rule = null;
		if(nodeList.getLength() > 0){
			Element ruleElement = (Element) nodeList.item(0);
			String type = ruleElement.getAttribute("type");
			String models = ruleElement.getAttribute("models");
			
			try {
				@SuppressWarnings("unchecked")
				Class<Rule> clazz = (Class<Rule>) Class.forName("br.com.webscanner.model.domain.rule." + type + "Rule");
				if (models != null && !models.isEmpty()) {
					String[] modelsParameters = models.split(",");
					List<String> modelsList = new ArrayList<String>();
					for (String model : modelsParameters) {
						modelsList.add(model.trim());
					}

					Constructor<Rule> constructor = (Constructor<Rule>) clazz.getConstructor(List.class);
					rule = constructor.newInstance(modelsList);
				} else {
					Constructor<Rule> constructor = (Constructor<Rule>) clazz.getConstructor();
					rule = constructor.newInstance();
				}
			} catch (ClassNotFoundException e) {
				logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
			} catch (InstantiationException e) {
				logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
			} catch (IllegalAccessException e) {
				logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
			} catch (SecurityException e) {
				logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
			} catch (IllegalArgumentException e) {
				logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
			} catch (InvocationTargetException e) {
				logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
			} catch (NoSuchMethodException e) {
				logger.error("Erro ao criar inst??ncia da classe de pr??-inicializa????o. {}", e.getMessage());
			}
		}
		return rule;
	}
}
	