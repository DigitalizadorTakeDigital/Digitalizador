/**
 * 
 */
package br.com.webscanner.model.domain.image;

import static br.com.webscanner.util.WebscannerUtil.generateSHA1;

import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.TransposeType;

import br.com.gcc.api.scanner.domain.model.TestId;
import br.com.webscanner.exception.CarregarArquivoException;
import br.com.webscanner.exception.CopiarArquivoException;
import br.com.webscanner.exception.FecharStreamException;
import br.com.webscanner.exception.InvalidImageSHA1Exception;
import br.com.webscanner.exception.Sha1Exception;
import br.com.webscanner.util.FileManagement;
import br.com.webscanner.util.ImageUtil;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDirectory;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import com.thoughtworks.xstream.annotations.XStreamOmitField;


/**
 * Representa uma imagem;
 * @author Jonathan Camara
 *
 */
public class ImageInfo {
	@XStreamOmitField
	private long id;
	@XStreamOmitField
	private File file;
	@XStreamOmitField
	private String sha1;
	@XStreamOmitField
	private List<? extends TestId> testsId;
	
	public ImageInfo(long id, File file) {
		this.id = id;
		this.file = file;
		this.sha1 = generateSHA1(file);
		this.testsId = new ArrayList<TestId>();
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
		this.sha1 = generateSHA1(file);
	}
	public String getSha1() {
		return sha1;
	}
	public List<? extends TestId> getTestsId() {
		return testsId;
	}
	public void setTestsId(List<? extends TestId> testsId) {
		this.testsId = testsId;
	}
	
	void rotacionar(TransposeType rotacao) throws Sha1Exception, InvalidImageSHA1Exception, CopiarArquivoException, CarregarArquivoException, FecharStreamException {
		if (sha1.equals(generateSHA1(file))) {
			rotacionar(file, rotacao);
			sha1 = generateSHA1(file);
		} else {
			throw new InvalidImageSHA1Exception("A imagem foi modificada fora da ferramenta");
		}
	}

	private void rotacionar(File file, TransposeType rotacao) throws CopiarArquivoException, CarregarArquivoException, FecharStreamException {
		String extension = FileManagement.getFileExtension(file).toLowerCase();
		
		FileOutputStream out = null;
		FileSeekableStream stream = null;
		File fileTemp =null;
		
		fileTemp = new File(FileManagement.TEMP_DIR, "T" + System.currentTimeMillis() + "." + extension);
		try {
			try {
				//copia o arquivo principal para um arquivo temporario
				FileManagement.copy(file, fileTemp);
			} catch (IOException e) {
				throw new CopiarArquivoException(file, fileTemp, e);
			}

			//apaga o arquivo principal
			file.delete();
			

			try {
				stream = new FileSeekableStream(fileTemp);
			} catch (IOException e) {
				throw new CarregarArquivoException("Erro ao carregar o arquivo de imagem via FileSeekableStream", e);
			}
			
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(ImageUtil.decodeOldCompression(stream));
			pb.add(rotacao);

			RenderedOp image = null;
			RenderedOp transposedImage = null;
			try {
				transposedImage = JAI.create("transpose", pb);
				out = new FileOutputStream(file);
				if (extension.equals("tiff") || extension.equals("tif")) {
					image  = JAI.create("stream", stream);
					TIFFDirectory tiffDirectory = (TIFFDirectory) image.getProperty("tiff_directory");
					TIFFField[] fields = tiffDirectory.getFields();
					TIFFEncodeParam encodeParam = new TIFFEncodeParam();
					int compression = tiffDirectory.getField(259).getAsInt(0);
					encodeParam.setCompression(compression);
					
					encodeParam.setExtraFields(fields);
					JAI.create("encode", transposedImage, out, "TIFF", encodeParam);
				} else {
					JAI.create("encode", transposedImage, out, "JPEG");
				}
			} catch (FileNotFoundException e) {
				throw new CarregarArquivoException("O arquivo de saida nao foi encontrado", e); 
			} finally {
				if(image != null){
					image.dispose();
				}
				
				if(transposedImage != null){
					transposedImage.dispose();
				}
				
				JAI.getDefaultInstance().getTileCache().flush();
				Runtime.getRuntime().gc();
			}
		} finally {
			fileTemp.delete();

			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					throw new FecharStreamException("Erro ao fechar o stream de saida da imagem", e);
				}
			
			if(stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					throw new FecharStreamException("Erro ao fechar o stream de saida da imagem", e);
				}
			
		}
	}
}