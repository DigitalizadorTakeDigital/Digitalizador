package br.com.webscanner.model.domain.xml;

import java.util.ArrayList;
import java.util.List;

import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.webscanner.exception.SpecificRecognitionXmlBuilderException;
import br.com.webscanner.model.domain.typification.Recognizable;
import br.com.webscanner.model.domain.typification.specific.transp01.TRANSP01Position;
import br.com.webscanner.model.domain.typification.specific.transp01.TRANSP01SpecificRecognition;

public class TRANSP01SpecificRacognitionXmlBuilder implements SpecificRecognitionXmlBuilder{

	private static Logger logger = LogManager.getLogger(TRANSP01SpecificRacognitionXmlBuilder.class.getName());
	
	@Override
	public Recognizable build(Element element) throws SpecificRecognitionXmlBuilderException {
		logger.info("Iniciando interpretação especifica do reconhecimento do produto TRANSP01");
		NodeList combinedNodeList = element.getElementsByTagName("combined");

		Element combinedElement = (Element) combinedNodeList.item(0);

		NodeList listaPosicoes = combinedElement.getElementsByTagName("position");

		List<TRANSP01Position> posicoes = new ArrayList<TRANSP01Position>();
		for (int i = 0; i < listaPosicoes.getLength(); i++) {
			TRANSP01Position posicao = buildINVESTFPPosition((Element) listaPosicoes.item(i));
			posicoes.add(posicao);
		}
		
		String idDocumento = ((Element) combinedElement.getElementsByTagName("match").item(0)).getAttribute("document");
		TRANSP01SpecificRecognition reconhecimento = new TRANSP01SpecificRecognition(Integer.valueOf(idDocumento), posicoes);
		
		return reconhecimento;
	}

	private TRANSP01Position buildINVESTFPPosition(Element position) throws SpecificRecognitionXmlBuilderException {
	
//		ApplicationData.getParam("ambiente");
		int x = Integer.parseInt(position.getAttribute("x").trim());
		int y = Integer.parseInt(position.getAttribute("y").trim());
		int width = Integer.parseInt(position.getAttribute("width").trim());
		int height = Integer.parseInt(position.getAttribute("height").trim());
		String name = position.getAttribute("name").trim();
		
		TRANSP01Position posicao = new TRANSP01Position(x, y, width, height, name);
		return posicao;
	}

	@SuppressWarnings("unused")
	private TransposeType getTransposeType(String orientation) throws SpecificRecognitionXmlBuilderException {
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
			logger.error("Erro ao recuperar o angulo de orientação do documento. {}", e);
			throw new SpecificRecognitionXmlBuilderException("Erro ao recuperar o angulo de orientação do documento.");
		}
	}
}
