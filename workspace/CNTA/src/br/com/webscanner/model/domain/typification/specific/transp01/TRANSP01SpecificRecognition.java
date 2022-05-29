package br.com.webscanner.model.domain.typification.specific.transp01;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.OCRException;
import br.com.webscanner.model.domain.Document;
import br.com.webscanner.model.domain.DocumentModel;
import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.extraction.ExtractionType;
import br.com.webscanner.model.domain.typification.specific.SpecificRecognizable;
import br.com.webscanner.model.services.OCRService;
import br.com.webscanner.util.ImageUtil;

public class TRANSP01SpecificRecognition implements SpecificRecognizable{
	private static Logger logger = LogManager.getLogger(TRANSP01SpecificRecognition.class.getName());

	private int idDocumento;
	private DocumentModel documento;
	private List<TRANSP01Position> posicoes;
		
	public TRANSP01SpecificRecognition(int idDocumento, List<TRANSP01Position> posicoes) {
		this.idDocumento = idDocumento;
		this.posicoes = posicoes;
	}

	@Override
	public boolean recognize(Document document) {
		
		BufferedImage bufferedImage = null;
		File arquivoImagem = null;
		
		try {
			arquivoImagem = document.getContent().getMainFile();
			bufferedImage = ImageUtil.getFileImage(arquivoImagem);
		} catch (IOException e) {
			logger.error("Não foi possível carregar o arquivo de imagem do documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());
			return false;
		}

//		List<ADQU03Position> _listaPosicoes = getOCRCode2of5(arquivoImagem, this.posicoes);

//		if(_listaPosicoes == null){
//			
//			return false;
//		}
		Map<String, String> ocrPorCampo = new LinkedHashMap<String, String>();
//		for (ADQU03Position posicao : _listaPosicoes) {
		for (TRANSP01Position posicao : this.posicoes) {
//			String ocr = posicao.getValor();
			String ocr2 = getOCRCode128(bufferedImage, posicao);
			if (ocr2 != null && !ocr2.isEmpty()) {

				ocrPorCampo.put(posicao.getName(), ocr2);
			}
		}

		if (ocrPorCampo.isEmpty()) {
			return false;
		} else {
			document.setDocumentModel(documento);

			for (Entry<String, String> entry : ocrPorCampo.entrySet()) {
				Field campo = document.getFieldByName(entry.getKey());
				campo.setValue(entry.getValue());
			}
			return true;
		}
	}
	
	private String getOCRCode128(BufferedImage bufferedImage, TRANSP01Position position) {
		String code = null;
		try {
			BufferedImage subImage = ImageUtil.getSubImage(bufferedImage, position.getX(), position.getY(), position.getWidth(), position.getHeight());
			code = OCRService.doOCR(subImage, ExtractionType.CODE128);
		} catch (OCRException e) {

			logger.warn("Não foi possível realizar OCR no documento. Documento não pôde ser tipificado. Mensagem: {}", e.getMessage());

			e.printStackTrace();
		}
		return code;
	}


	
	public int getIdDocumento() {
		return idDocumento;
	}

	public DocumentModel getDocumento() {
		return documento;
	}

	public void setDocumento(DocumentModel documento) {
		this.documento = documento;
	}

	public List<TRANSP01Position> getPosicoes() {
		return posicoes;
	}
}
