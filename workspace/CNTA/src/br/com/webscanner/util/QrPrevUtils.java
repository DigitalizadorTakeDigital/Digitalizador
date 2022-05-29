package br.com.webscanner.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.InputStream;
import javax.imageio.ImageIO;

import br.com.webscanner.model.domain.typification.Recognizable;
//import br.com.webscanner.model.domain.validatable.PREV03Validation;
import br.com.webscanner.view.MainDesenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.List;


public class QrPrevUtils {
	
	static boolean docsSA;
	public static int pgQtdPrev;
	private static Logger logger = LogManager.getLogger(QrPrevUtils.class.getName());
	

	public static HashMap <String,String> QrHash (String [] qrArray){
		
		
			 
		HashMap< String , String > mHash = new HashMap<String, String>();
		mHash.put("CENTRO_CUSTO", qrArray[1]);
		mHash.put("PROT_ATDMT","P" + qrArray[2]); //COLOCAR P NA FRENTE
		mHash.put("COD_DOCTO", qrArray[3]);
		mHash.put("QTD_PAG", qrArray[4]);
		mHash.put("ORQUESTRADORCONTEUDO", qrArray[5]);
		mHash.put("classeDocumental", qrArray[6]);
		mHash.put("mimeType", qrArray[7]);
		mHash.put("COD_PRODT_CORP", qrArray[8]);
		mHash.put("NRO_PPSTA", qrArray[9]);
		mHash.put("DATA_GERAC_PPSTA","D" + qrArray[10]);//COLOCAR D NA FRENTE
		mHash.put("DATA_ASS_PPSTA","D" + qrArray[11]);// COLOCAR D NA FRENTE
		mHash.put("CSUCUR_SEGDR", qrArray[12]);
		mHash.put("CPF", qrArray[13]);
		mHash.put("CNPJ", qrArray[14]);
		mHash.put("COD_AG", qrArray[15]);
		mHash.put("COD_TPO_PPSTA", qrArray[16]);
		mHash.put("COD_PCERO_NEGOC", qrArray[17]);
		mHash.put("CPRODT_SMB", qrArray[18]);
		mHash.put("nomeDocumento", qrArray[19]);
		mHash.put("tituloDocumento", qrArray[20]);
		mHash.put("MCRPRC", qrArray[21]);
		mHash.put("NEGOC", qrArray[22]);
		mHash.put("PROCS", qrArray[23]);
		mHash.put("IDTFD_EXCUC_PROCS", qrArray[24]);
		mHash.put("cabecalhoAutenticacao", qrArray[25]);
		mHash.put("identificadorLogado",qrArray[26]);
		mHash.put("identificadorSistema",qrArray[27]);
		mHash.put("carimboTempo",qrArray[28]);
		mHash.put("tokenAssinatura",qrArray[29]);
		mHash.put("usuario",qrArray[30]);
		mHash.put("codigoComposicao",qrArray[31]);
		mHash.put("opcaoGeracaoProtocolo",qrArray[32]);
		mHash.put("numeroProtocolo","P" + qrArray[33]); //COLOCAR P NA FRENTE
		mHash.put("tipoPessoa",qrArray[34]); 
		mHash.put("cpfCnpj",qrArray[35]); 
		mHash.put("dataAtendimento",qrArray[36]); 
		mHash.put("horaAtendimento",qrArray[37]); 		
		mHash.put("codigoTipoDocumento",qrArray[38]);
		mHash.put("nomeDoc",qrArray[39]);
		if(qrArray.length>= 41)
		mHash.put("Posicao Assinaturas",qrArray[40]);
		else
		mHash.put("Posicao Assinaturas","null");
			
		//caso não tenha assinatura gerar null
		//colocar nome do arquivo final
		return mHash;

	}
	
	public static int verificaTipoDocumento(String docType) {
		int index =  -1;
	
		
		if (docType.equals("APORTE")) {		
			index = 0;
		
		}
		if (docType.equals("PROPOSTA")) {		
			index = 1;
			
		}if (docType.equals("CONTRATO")) {		
			index = 2;
			
		}if (docType.equals("CADASTRO_EMPRESA")) {		
			index = 3;
			
		}if (docType.equals("CADASTRO_REPRESENTANTES")) {		
			index = 4;
			
		}if (docType.equals("SEM ASSINATURA")) {		
			index = 5;
		}
	  return index;
	}
	
	public static int qtdPaginas(String [] split, String ocrExtract) {
		String paginas = split[4];
		String [] pgArray = paginas.split("/");		
		int	pgQtd = Integer.parseInt(pgArray [1]);
		int pgNumb = Integer.parseInt(pgArray [0]);
		
		
		if(pgQtd == pgNumb) {	
		pgQtdPrev += pgQtd;
		}		
		return pgQtd;
	}
	
	public static String setDocTipe(String [] split, String ocrExtract) {
		String docType = split[20];
		return docType;
	}



	public static String validaAssinaturas(String [] QrSplit  ,  BufferedImage bufferedImage , String docType) {
		docsSA = false;
		String xy[] = QrSplit[40].split(";");
			 
			 		 
		     for (String xY : xy) {
									
				String [] xyPos = xY.split(",");
				int posX = Integer.parseInt(xyPos[0]);
				int posY = Integer.parseInt(xyPos[1]);
				BufferedImage subImageAssinatura = ImageUtil.getSubImage(bufferedImage, posX, posY, 300, 30);
//				 try {
//					ImageIO.write(subImageAssinatura, "png" , new File("C:\\teste\\imageassin.png"));
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				
               double blankRate = ImageUtil.getColorRate(subImageAssinatura, Color.WHITE);
				
				if(blankRate >= 1.0){
					docsSA = true;				
					
					    docType="SEM ASSINATURA";
						

			     return docType;
				}
				
			}		
		     return docType;
		}	
	


	
	

	

	
}

	

