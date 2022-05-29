package br.com.webscanner.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.RasterFormatException;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteOrder;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.TransposeType;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.webscanner.exception.FileAlreadyExistsException;
import br.com.webscanner.infra.WebScannerConfig;
import br.com.webscanner.model.domain.BITMAPINFOHEADER;
import br.com.webscanner.model.domain.image.ImageInfo;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.imageioimpl.plugins.tiff.TIFFIFD;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageMetadata;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDirectory;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;

/**
 * Classe Util respons�vel por recuperar as imagens.
 * 
 * @author Jonathan Camara
 */
public class ImageUtil {
	private static Logger logger = LogManager.getLogger(ImageUtil.class
			.getName());
	private static int count;

	private ImageUtil() {
	}

	static {
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");
		JAI.disableDefaultTileCache();
		JAI.getDefaultInstance().getTileCache().memoryControl();
		JAI.getDefaultInstance().getTileCache()
				.setMemoryCapacity(16 * 1024 * 1024L);
	}

	/**
	 * Retorna uma imagem dentro da aplicação
	 * 
	 * @param image
	 *            - Nome da imagem
	 * @return {@link ImageIcon}
	 */
	public static ImageIcon getImage(String image) {
		Image ima = Toolkit.getDefaultToolkit().createImage(
				ImageUtil.class.getClassLoader().getResource(
						"br/com/webscanner/resources/images/" + image));
		return new ImageIcon(ima);
	}

	/**
	 * Converte BITMAPINFOHEADER de 1 bit para um Image
	 * 
	 * @param bmih
	 *            - BITMAPINFOHEADER
	 * @return {@image Image}
	 */
	public static Image xferDIB1toImage(BITMAPINFOHEADER bmih) {
		int width = bmih.biWidth;
		int height = bmih.biHeight;
		if (height < 0)
			height = -height;

		int pixels[] = new int[width * height];
		int numColors;
		if (bmih.biClrUsed > 0)
			numColors = bmih.biClrUsed;
		else
			numColors = (1 << bmih.biBitCount);

		int rb = width / 8;
		if ((width % 8) != 0)
			rb++;

		int rem = rb % 4;

		int padBytes = rem == 0 ? 0 : 4 - rem;

		int rowBytes = rb + padBytes;

		byte bitmap[] = bmih.getPointer().getByteArray(
				bmih.size() + numColors * 4, height * rowBytes);
		int palette[] = bmih.getPointer().getIntArray(bmih.size(), numColors);
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				int index = rowBytes * row + col / 8;
				int mask = 1 << (7 - (col & 7));
				int pixel = 0xff000000 | palette[(bitmap[index] & mask) == mask ? 1
						: 0];
				pixels[width * (height - row - 1) + col] = pixel;
			}
		}
		MemoryImageSource mis = new MemoryImageSource(width, height, pixels, 0,
				width);
		return Toolkit.getDefaultToolkit().createImage(mis);
	}

	/**
	 * Converte BITMAPINFOHEADER de 24 bit para um Image
	 * 
	 * @param bmih
	 *            - BITMAPINFOHEADER
	 * @return {@image Image}
	 */
	public static Image xferDIB24toImage(BITMAPINFOHEADER bmih) {
		int width = bmih.biWidth;
		int height = bmih.biHeight;
		if (height < 0)
			height = -height;
		int pixels[] = new int[width * height];

		int padBytes = (3 * width) % 4;

		int rowBytes = 3 * width + padBytes;
		byte bitmap[] = bmih.getPointer().getByteArray(bmih.size(),
				height * rowBytes);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				int index = rowBytes * row + col * 3;

				int pixel = 0xff000000 | (bitmap[index + 2] & 0xff) << 16
						| (bitmap[index + 1] & 0xff) << 8
						| (bitmap[index] & 0xff);

				pixels[width * (height - row - 1) + col] = pixel;
			}
		}
		MemoryImageSource mis = new MemoryImageSource(width, height, pixels, 0,
				width);
		return Toolkit.getDefaultToolkit().createImage(mis);
	}

	/**
	 * Converte BITMAPINFOHEADER de 8 bit para um Image
	 * 
	 * @param bmih
	 *            - BITMAPINFOHEADER
	 * @return {@image Image}
	 */
	public static Image xferDIB8toImage(BITMAPINFOHEADER bmih) {
		int width = bmih.biWidth;
		int height = bmih.biHeight; // height < 0 if bitmap is top-down
		if (height < 0)
			height = -height;
		int pixels[] = new int[width * height];
		int numColors;
		if (bmih.biClrUsed > 0)
			numColors = bmih.biClrUsed;
		else
			numColors = (1 << bmih.biBitCount);
		int padBytes = (4 - width % 4) % 4; // Each pixel occupies 1 byte
		int rowBytes = width + padBytes;
		byte bitmap[] = bmih.getPointer().getByteArray(
				bmih.size() + numColors * 4, height * rowBytes);
		int palette[] = bmih.getPointer().getIntArray(bmih.size(), numColors);
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				byte bitVal = bitmap[rowBytes * row + col];
				int pixel = 0xff000000 | palette[bitVal & 0xff];
				pixels[width * (height - row - 1) + col] = pixel;
			}
		}
		MemoryImageSource mis = new MemoryImageSource(width, height, pixels, 0,
				width);
		return Toolkit.getDefaultToolkit().createImage(mis);
	}

	public static br.com.webscanner.model.domain.image.ImageInfo saveTiff(
			Image img, String side) throws NoSuchAlgorithmException,
			IOException {
		long id = System.currentTimeMillis();
		StringBuilder imageName = new StringBuilder(side).append(id)
				.append(StringEncryptor.removerAcentos(System.getenv("computername"))).append(count++)
				.append(".tif");

		logger.info("Salvando imagem como tiff. {}", imageName.toString());

		File file = new File(System.getProperty("user.home")
				+ System.getProperty("file.separator")
				+ WebScannerConfig.getProperty("pathImage"),
				imageName.toString());
		file.getParentFile().mkdirs();

		saveAsTIFF(toBufferedImage(img, BufferedImage.TYPE_BYTE_BINARY), file);

		br.com.webscanner.model.domain.image.ImageInfo image = new br.com.webscanner.model.domain.image.ImageInfo(
				id, file);

		logger.info("Imagem salva com sucesso");
		return image;
	}
	
	public static br.com.webscanner.model.domain.image.ImageInfo saveTiffStream(
			URL url, String side) throws NoSuchAlgorithmException,
			IOException {
		long id = System.currentTimeMillis();
		StringBuilder imageName = new StringBuilder(side).append(id)
				.append(StringEncryptor.removerAcentos(System.getenv("computername"))).append(count++)
				.append(".tif");

		logger.info("Salvando imagem como tiff. {}", imageName.toString());

		File file = new File(System.getProperty("user.home")
				+ System.getProperty("file.separator")
				+ WebScannerConfig.getProperty("pathImage"),
				imageName.toString());
		file.getParentFile().mkdirs();

		//saveAsTIFF(toBufferedImage(img, BufferedImage.TYPE_BYTE_BINARY), file);
		
		//BufferedImage buffedImage = getFileImage();
		
		BufferedImage buffedImage = getFileImage(new File(url.getFile()),true);
		
		saveAsTIFF(buffedImage, file);

		br.com.webscanner.model.domain.image.ImageInfo image = new br.com.webscanner.model.domain.image.ImageInfo(
				id, file);

		logger.info("Imagem salva com sucesso");
		return image;
	}

	/**
	 * Salva uma imagem no formato jpeg
	 * 
	 * @param img
	 *            - Imagem que será salva como jpg
	 * @param id
	 *            - Id da imagem que será utilizado como identificador da
	 *            imagem.
	 * @param side
	 *            - Lado da imagem (F | V)
	 * @return
	 * @throws FileNotFoundException
	 */
	public static br.com.webscanner.model.domain.image.ImageInfo saveJpg(
			Image img, String side) {
		long id = System.currentTimeMillis();
		StringBuilder imageName = new StringBuilder(side).append(id)
				.append(StringEncryptor.removerAcentos(System.getenv("computername"))).append(count++)
				.append(".jpg");

		logger.info("Salvando imagem como jpg. {}", imageName.toString());

		File file = new File(System.getProperty("user.home")
				+ System.getProperty("file.separator")
				+ WebScannerConfig.getProperty("pathImage"),
				imageName.toString());

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			RenderedImage ri = toBufferedImage(img, BufferedImage.TYPE_INT_RGB);
			JAI.create("encode", ri, outputStream, "JPEG", null);

			br.com.webscanner.model.domain.image.ImageInfo image = new br.com.webscanner.model.domain.image.ImageInfo(
					id, file);
			return image;
		} catch (FileNotFoundException e) {
			logger.error("Arquivo não encontrado '{}'", file.getAbsolutePath());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar o outputStream");
				}
			}
		}
		return null;
	}

	/**
	 * Retorna um {@link BufferedImage} a partir de uma {@link Image} e o tipo
	 * da imagem
	 * 
	 * @param src
	 *            - Imagem source
	 * @param type
	 *            - Tipo da imagem. Consultar as constantes da classe
	 *            {@link BufferedImage} Ex.: BufferedImage.TYPE_BYTE_BINARY,
	 *            BufferedImage.TYPE_INT_RGB.
	 * @return
	 */
	private static BufferedImage toBufferedImage(Image src, int type) {
		int w = src.getWidth(null);
		int h = src.getHeight(null);

		// TODO: método em testes no save tiff.
		BufferedImage dest;
		// if(type == BufferedImage.TYPE_BYTE_BINARY){
		// byte[] map = new byte[] {(byte)(255),(byte)(0)};
		// IndexColorModel colorModel = new IndexColorModel(1, 2, map, map,
		// map);
		// dest = new BufferedImage(w, h, type, colorModel);
		// }else{
		// dest = new BufferedImage(w, h, type);
		// }

		dest = new BufferedImage(w, h, type);

		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(src, 0, 0, null);
		g2.dispose();

		return dest;
	}

	public static BufferedImage toBufferedImage(RenderedImage img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		ColorModel cm = img.getColorModel();
		int width = img.getWidth();
		int height = img.getHeight();
		WritableRaster raster = cm
				.createCompatibleWritableRaster(width, height);
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();

		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		String[] keys = img.getPropertyNames();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				properties.put(keys[i], img.getProperty(keys[i]));
			}
		}

		BufferedImage result = new BufferedImage(cm, raster,
				isAlphaPremultiplied, properties);
		img.copyData(raster);
		return result;
	}

	/**
	 * Retorna uma imagem a partir de um arquivo.
	 * 
	 * @param imageFile
	 *            - Arquivo de imagem.
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage getFileImage(File imageFile) throws IOException {
		FileSeekableStream stream = null;
		RenderedOp op = null;
		BufferedImage image = null;

		if (imageFile != null && !imageFile.exists()) {
			logger.error("O arquivo não foi encontrado {}", imageFile.getPath());
			throw new FileNotFoundException();
		}

		try {
			
			stream = new FileSeekableStream(imageFile);

			if (imageFile.getAbsolutePath().endsWith(".jpg")) {
				return toBufferedImage(decodeOldCompression(stream));
			} else {
				op = JAI.create("stream", stream);
				image = op.getAsBufferedImage();
			}
		} catch (Exception e) {
			logger.error("Erro ao recuperar a imagem do stream.{}",
					e.getMessage());
		} finally {
			if (stream != null) {
				stream.close();
			}

			if (op != null) {
				op.dispose();
			}

			JAI.getDefaultInstance().getTileCache().flush();
			Runtime.getRuntime().gc();
		}
		return image;
	}

	public static BufferedImage getFileImage(File imageFile, boolean imageio)
			throws IOException {
		return ImageIO.read(imageFile);
	}

	/**
	 * Retorna uma imagem a partir de uma URL.
	 * 
	 * @param url
	 *            - Endereço da imagem que será carregada
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static BufferedImage getFileImage(URL url) throws IOException,
			IllegalArgumentException {
		BufferedImage image = null;
		RenderedOp op = null;
		try {
			op = JAI.create("url", url);
			image = op.getAsBufferedImage();
		} finally {
			if (op != null) {
				op.dispose();
			}
			
			JAI.getDefaultInstance().getTileCache().flush();
			Runtime.getRuntime().gc();
		}
		
		
		return image;
	}

	public static BufferedImage getSubImage(BufferedImage bufferedImage, int x,
			int y, int w, int h) {

		try {
			bufferedImage = bufferedImage.getSubimage(x, y, w, h);
			return bufferedImage;
		} catch (RasterFormatException e) {
			logger.warn("Erro ao obter área de imagem para reconhecimento. A área definida não está contida na imagem (verificar dimensões da imagem). Mensagem de erro: "
					+ e.getMessage());
			return null;
		} catch (IllegalArgumentException e) {
			logger.warn("Erro ao recuperar arquivo de imagem. Mensagem de erro: "
					+ e.getMessage());
			return null;
		}
	}

	/**
	 * Rotaciona a Imagem
	 * 
	 * @param rotation
	 *            - ROTATE_90 rotaciona para a direita, ROTATE_270 rotaciona
	 *            para a esquerda
	 */
	private static void rotate(File file, TransposeType rotation) {
		String nomeOriginal = file.getAbsolutePath();
		String extension = FileManagement.getFileExtension(file).toLowerCase();
		FileOutputStream out = null;
		FileSeekableStream stream = null;
		File fileTemp = null;

		fileTemp = new File(System.getProperty("user.home")
				+ System.getProperty("file.separator")
				+ WebScannerConfig.getProperty("pathImage"), "T"
				+ System.currentTimeMillis() + "." + extension);
		try {
			// copia o arquivo principal para um arquivo temporario
			FileManagement.copy(file, fileTemp);

			// apaga o arquivo principal
			file.delete();

			stream = new FileSeekableStream(fileTemp);

			ParameterBlock pb = new ParameterBlock();
			pb.addSource(decodeOldCompression(stream));
			pb.add(rotation);

			RenderedOp image = null;
			RenderedOp transposedImage = null;
			try {
				transposedImage = JAI.create("transpose", pb);
				out = new FileOutputStream(new File(nomeOriginal));
				if (extension.equals("tiff") || extension.equals("tif")) {
					image = JAI.create("stream", stream);
					TIFFDirectory tiffDirectory = (TIFFDirectory) image
							.getProperty("tiff_directory");
					TIFFField[] fields = tiffDirectory.getFields();
					TIFFEncodeParam encodeParam = new TIFFEncodeParam();
					encodeParam
							.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);

					encodeParam.setExtraFields(fields);
					JAI.create("encode", transposedImage, out, "TIFF",
							encodeParam);
				} else {
					JAI.create("encode", transposedImage, out, "JPEG");
				}
			} finally {
				if (image != null) {
					image.dispose();
				}

				if (transposedImage != null) {
					transposedImage.dispose();
				}

				JAI.getDefaultInstance().getTileCache().flush();
				Runtime.getRuntime().gc();
			}
		} catch (IOException e2) {
			logger.error("Erro ao ler a imagem: {}", file.getAbsolutePath());
		} catch (Exception e) {
			logger.error("Erro ao rotacionar a imagem. {}", e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar o arquivo: {}",
							file.getAbsolutePath());
				}

			}

			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar o stream");
				}
			}

			fileTemp.delete();
		}
	}

	/**
	 * Rotaciona a Imagem
	 * 
	 * @param rotation
	 *            - ROTATE_90 rotaciona para a direita, ROTATE_270 rotaciona
	 *            para a esquerda
	 * @throws IOException
	 */
	private static BufferedImage rotateImage(File file, TransposeType rotation)
			throws IOException {
		FileSeekableStream stream = null;

		try {
			stream = new FileSeekableStream(file);

			ParameterBlock pb = new ParameterBlock();
			pb.addSource(decodeOldCompression(stream));
			pb.add(rotation);

			try {
				return JAI.create("transpose", pb).getAsBufferedImage();
			} finally {
				JAI.getDefaultInstance().getTileCache().flush();
				Runtime.getRuntime().gc();
			}
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar o stream");
				}
			}
		}
	}

	/**
	 * Rotaciona a Imagem
	 * 
	 * @param image
	 * @param rotation
	 *            - ROTATE_90 rotaciona para a direita, ROTATE_270 rotaciona
	 *            para a esquerda
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage rotateImage(BufferedImage image,
			TransposeType rotation) throws IOException {
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image);
		pb.add(rotation);

		try {
			return JAI.create("transpose", pb).getAsBufferedImage();
		} finally {
			JAI.getDefaultInstance().getTileCache().flush();
			Runtime.getRuntime().gc();
		}
	}

	/**
	 * Decodifica imagens JPG com compressão antiga (geradas pelo driver CAPI). <br>
	 * Se a imagem possuir compressão antiga, decodifica-a de maneira específica
	 * e a retorna. Senão, carrega a imagem de maneira genérica e a retorna.
	 * 
	 * @param stream
	 *            - {@link SeekableStream} da imagem
	 * @return {@link RenderedImage}
	 */
	public static RenderedImage decodeOldCompression(SeekableStream stream) {
		int TAG_COMPRESSION = 259;
		int TAG_JPEG_INTERCHANGE_FORMAT = 513;

		int COMP_JPEG_OLD = 6;
		// int COMP_JPEG_TTN2 = 7;
		// SeekableStream stream = new ByteArraySeekableStream(imageData);

		try {
			TIFFDirectory tdir = new TIFFDirectory(stream, 0);
			int compression = tdir.getField(TAG_COMPRESSION).getAsInt(0);

			// Decoder name
			String decoder2use = "tiff";
			if (compression == COMP_JPEG_OLD) {
				// Special handling for old/unsupported JPEG-in-TIFF format:
				// {@link:
				// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4929147 }
				stream.seek(tdir.getField(TAG_JPEG_INTERCHANGE_FORMAT)
						.getAsLong(0));
				decoder2use = "jpeg";
			}

			// Decode image
			ImageDecoder dec = ImageCodec.createImageDecoder(decoder2use,
					stream, null);
			RenderedImage img = dec.decodeAsRenderedImage();
			return img;
		} catch (Exception e) {
			// TODO: Teste
			// Exceção lançada quando o JPG não é old compression. Dessa forma
			// está funcionando, porém é necessário melhorar
			// método ou verificar quanto à padronirzar as imagens JPGs geradas
			// (TWAIN e CAPI).
			logger.warn("A imagem não era old compression. {}", e.getMessage());
			return JAI.create("stream", stream).getAsBufferedImage();
		}
	}

	private static void saveAsTIFF(RenderedImage image, File file) {
		ImageWriter imageWriter = (ImageWriter) ImageIO
				.getImageWritersByMIMEType("image/tiff").next();
		ImageWriteParam param = imageWriter.getDefaultWriteParam();
		if (param.canWriteCompressed()) {
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionType("CCITT T.6");
			param.setCompressionQuality(1f);
		}
		if (param.canWriteTiles()) {
			param.setTilingMode(ImageWriteParam.MODE_DISABLED);
		}

		ImageOutputStream ios = null;
		try {
			OutputStream outputStream = new FileOutputStream(file);
			ios = ImageIO.createImageOutputStream(outputStream);
			ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			imageWriter.setOutput(ios);
			imageWriter.prepareWriteSequence(null);
			IIOMetadata metadata = getMetadata(imageWriter, image, param);
			IIOImage iioImage = new IIOImage(image, null, metadata);
			imageWriter.writeToSequence(iioImage, param);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if (ios != null) {
				try {
					ios.flush();
					ios.close();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
				}
			}
			if (imageWriter != null) {
				try {
					imageWriter.endWriteSequence();
					imageWriter.reset();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
				}
			}
		}
	}

	public static void saveAsMultipageTIFF(List<File> images, File file) {
		ImageWriter imageWriter = (ImageWriter) ImageIO
				.getImageWritersByMIMEType("image/tiff").next();
		ImageWriteParam param = imageWriter.getDefaultWriteParam();
		if (param.canWriteCompressed()) {
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionType("CCITT T.6");
			param.setCompressionQuality(0.7f);
		}
		if (param.canWriteTiles()) {
			param.setTilingMode(ImageWriteParam.MODE_DISABLED);
		}

		ImageOutputStream ios = null;
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			ios = ImageIO.createImageOutputStream(outputStream);
			ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			imageWriter.setOutput(ios);
			imageWriter.prepareWriteSequence(null);
			for (File f : images) {
				RenderedImage renderedImage = getFileImage(f);
				IIOMetadata metadata = getMetadata(imageWriter, renderedImage,
						param);
				IIOImage iioImage = new IIOImage(renderedImage, null, metadata);
				imageWriter.writeToSequence(iioImage, param);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ios != null) {
				try {
					ios.flush();
					ios.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (imageWriter != null) {
				try {
					imageWriter.endWriteSequence();
					imageWriter.reset();
					imageWriter.dispose();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.info(e.getMessage());
				}
			}
		}
	}

	private static IIOMetadata getMetadata(ImageWriter imageWriter,
			RenderedImage image, ImageWriteParam param) {
		TIFFImageMetadata tiffMetadata = (TIFFImageMetadata) getIIOMetadata(
				image, imageWriter, param);
		TIFFIFD rootIFD = tiffMetadata.getRootIFD();
		BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();

		// PhotometricIntearpretation
		rootIFD.addTIFFField(new com.sun.media.imageio.plugins.tiff.TIFFField(
				base.getTag(BaselineTIFFTagSet.TAG_PHOTOMETRIC_INTERPRETATION),
				0));

		// Compression
		rootIFD.addTIFFField(new com.sun.media.imageio.plugins.tiff.TIFFField(
				base.getTag(BaselineTIFFTagSet.TAG_COMPRESSION), 4));

		// ResolutionUnit
		// rootIFD.addTIFFField(new
		// com.sun.media.imageio.plugins.tiff.TIFFField(base.getTag(BaselineTIFFTagSet.TAG_RESOLUTION_UNIT),
		// 2));

		com.sun.media.imageio.plugins.tiff.TIFFField fieldXRes = new com.sun.media.imageio.plugins.tiff.TIFFField(
				base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION),
				TIFFTag.TIFF_RATIONAL, 1, new long[][] { { 300, 1 } });
		rootIFD.addTIFFField(fieldXRes);

		com.sun.media.imageio.plugins.tiff.TIFFField fieldYRes = new com.sun.media.imageio.plugins.tiff.TIFFField(
				base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION),
				TIFFTag.TIFF_RATIONAL, 1, new long[][] { { 300, 1 } });
		rootIFD.addTIFFField(fieldYRes);

		// XResolution
		// rootIFD.addTIFFField(new
		// com.sun.media.imageio.plugins.tiff.TIFFField(base.getTag(282), 200));

		// YResolution
		// rootIFD.addTIFFField(new
		// com.sun.media.imageio.plugins.tiff.TIFFField(base.getTag(283), 200));

		// BitsPerSample
		rootIFD.addTIFFField(new com.sun.media.imageio.plugins.tiff.TIFFField(
				base.getTag(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE), 1));

		// RowsPerStrip
		rootIFD.addTIFFField(new com.sun.media.imageio.plugins.tiff.TIFFField(
				base.getTag(BaselineTIFFTagSet.TAG_ROWS_PER_STRIP), image
						.getHeight()));

		// FillOrder
		rootIFD.addTIFFField(new com.sun.media.imageio.plugins.tiff.TIFFField(
				base.getTag(BaselineTIFFTagSet.TAG_FILL_ORDER), 1));

		return tiffMetadata;
	}

	private static IIOMetadata getIIOMetadata(RenderedImage image,
			ImageWriter imageWriter, ImageWriteParam param) {
		ImageTypeSpecifier spec = ImageTypeSpecifier
				.createFromRenderedImage(image);
		IIOMetadata metadata = imageWriter.getDefaultImageMetadata(spec, param);
		return metadata;
	}

	/**
	 * Retorna o rate de preenchimento da imagem pela cor indicada.
	 * 
	 * @param image
	 *            {@link BufferedImage} Imagem a ser verificada.
	 * @param color
	 *            {@link Color} Cor a qual deseja-se verificar o preenchimento
	 *            da imagem.
	 * @return Rate de preenchimento da imagem pela cor indicada ou -1 se a
	 *         imagem for nula.
	 */
	public static double getColorRate(BufferedImage image, Color color) {
		if (image != null) {
			double count = 0;
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					Color pixel = new Color(image.getRGB(x, y));
					if (pixel.equals(color)) {
						count++;
						continue;
					}
				}
			}
			double area = image.getWidth() * image.getHeight();
			return count / area;
		}
		return -1.0;
	}

	/**
	 * Salva uma image em formato JPEG, no caminho de exportação padrão
	 * (/UserHome/Webscanner), com resolução de 100 DPI e 8 bits.
	 * 
	 * @param image
	 *            {@link Image} o qual deseja-se salvar
	 * @param id
	 *            id que irá compor o nome do arquivo de imagem
	 * @param side
	 *            define o lado do documento (F | V) e também irá compor o nome
	 *            da imagem
	 * @return {@link ImageInfo}
	 */
	public static ImageInfo saveJpeg(Image image, String side) {

		int dpi = 300;
		float jpgQuality = 0.5f;
		long id = System.currentTimeMillis();
		StringBuilder imageName = new StringBuilder(side).append(id)
				.append(StringEncryptor.removerAcentos(System.getenv("computername"))).append(count++)
				.append(".jpg");

		logger.info("Salvando imagem como jpg. {}", imageName.toString());

		File file = new File(System.getProperty("user.home")
				+ System.getProperty("file.separator")
				+ WebScannerConfig.getProperty("pathImage"),
				imageName.toString());
		BufferedImage bufferedImage = toBufferedImage(image,
				BufferedImage.TYPE_BYTE_GRAY);

		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			JPEGImageEncoder jpegImageEncoder = JPEGCodec
					.createJPEGEncoder(fileOutputStream);
			JPEGEncodeParam jpegEncodeParam = jpegImageEncoder
					.getDefaultJPEGEncodeParam(bufferedImage);
			jpegEncodeParam
					.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
			jpegEncodeParam.setXDensity(dpi);
			jpegEncodeParam.setYDensity(dpi);
			jpegEncodeParam.setQuality(jpgQuality, true);
			jpegImageEncoder.encode(bufferedImage, jpegEncodeParam);
			logger.info("Imagem salva com sucesso");

			ImageInfo imageInfo = new ImageInfo(id, file);
			return imageInfo;
		} catch (FileNotFoundException e) {
			logger.error(
					"Não foi possível escrever o arquivo de imagem. Erro: {}",
					e.getMessage());
		} catch (ImageFormatException e) {
			logger.error("Erro devido ao formato da imagem: {}", e.getMessage());
		} catch (IOException e) {
			logger.error(
					"Não foi possivel escrever o arquivo de imagem. Erro: {}",
					e.getMessage());
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.flush();
					fileOutputStream.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar stream: {}", e.getMessage());
				}
			}
		}
		return null;
	}

	/**
	 * Verifica se a imagem Tiff está no padrão de digitalizacao
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isTiffStandard(File file) {
		FileSeekableStream stream = null;
		try {
			stream = new FileSeekableStream(file);
			TIFFDirectory tiffDirectory = new TIFFDirectory(stream, 0);

			if (tiffDirectory.getField(BaselineTIFFTagSet.TAG_COMPRESSION)
					.getAsInt(0) != BaselineTIFFTagSet.COMPRESSION_CCITT_T_6) {
				logger.info("Imagem não compactada no padrão COMPRESSION_CCITT_T_6");
				return false;
			} else if (tiffDirectory.getField(
					BaselineTIFFTagSet.TAG_X_RESOLUTION).getAsRational(0)[0] != 300) {
				logger.info("Imagem não possui 300 dpi");
				return false;
			} else if (tiffDirectory.getField(
					BaselineTIFFTagSet.TAG_Y_RESOLUTION).getAsRational(0)[0] != 300) {
				logger.info("Imagem não possui 300 dpi");
				return false;
			} else if (tiffDirectory.getField(
					BaselineTIFFTagSet.TAG_PHOTOMETRIC_INTERPRETATION)
					.getAsInt(0) != BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO) {
				logger.info("Imagem fora do padrao PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO");
				return false;
				// } else
				// if(tiffDirectory.getField(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE).getAsInt(0)
				// != 1){
				// logger.info("Imagem não possui 1 bit");
				// return false;
				// } else
				// if(tiffDirectory.getField(BaselineTIFFTagSet.TAG_RESOLUTION_UNIT).getAsInt(0)
				// != 2){
				// logger.info("Unidade de resolução não é polegada");
				// return false;
			}
		} catch (IOException e) {
			logger.error(
					"Não foi possível criar o stream de leitura da imagem. {}",
					e.getMessage());
			return false;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error("Falha ao fechar o stream. {}", e.getMessage());
				}
			}
		}

		return true;
	}

	/**
	 * Retorna uma imagem dentro da aplicação
	 * 
	 * @param image
	 *            - Nome da imagem
	 * @return {@link ImageIcon}
	 * @throws IOException
	 */
	public static BufferedImage getImageAsBuffered(String image)
			throws IOException {
		return ImageIO.read(ImageUtil.class.getClassLoader().getResource(
				"br/com/webscanner/resources/images/" + image));
	}

	/**
	 * Escreve o texto em uma imagem. Utilizado na imagem do documento
	 * importado.
	 * 
	 * @param image
	 * @param text
	 */
	public static void writeString(BufferedImage image, String text) {
		Graphics graphics = image.getGraphics();
		graphics.setFont(new Font("Tahoma", Font.BOLD, 60));
		graphics.setColor(Color.white);
		graphics.drawString(text, 50, 175);
		graphics.dispose();

	}

	/**
	 * Verifica se um arquivo de imagem TIFF contém mais de uma imagem.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isTiffMultipage(File file) {
		FileSeekableStream seekableStream = null;
		try {
			seekableStream = new FileSeekableStream(file);
			ImageDecoder decoder = ImageCodec.createImageDecoder("tiff",
					seekableStream, null);
			return decoder.getNumPages() > 1;
		} catch (IOException e) {
			logger.error(
					"Não foi possível criar o stream de leitura da imagem. {}",
					e.getMessage());
			return false;
		} finally {
			if (seekableStream != null) {
				try {
					seekableStream.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar stream: {}", e.getMessage());
				}
			}
		}
	}

	public static void converterTiffUnicoMultipage(String caminhoTiffUnico, String caminhoFinal)
			throws IOException {

		FileSeekableStream ss = new FileSeekableStream(caminhoTiffUnico);

		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", ss, null);

		int count = dec.getNumPages();

		TIFFEncodeParam param = new TIFFEncodeParam();

		param.setCompression(TIFFEncodeParam.COMPRESSION_NONE);

		param.setLittleEndian(false); // Intel

		System.out.println("This TIF has " + count + " image(s)");

		Date tempoInicial = new Date();
		
		Date tempoInicialImagem = null;
		Date tempoFinalImagem = null;
		RenderedImage page = null;
		
		File f  = null;
		ParameterBlock pb  = null;
		RenderedOp r  = null;
		
		final String SAVING = "Saving ";
		final String EXTENSAO = ".tif";
		final String MIMETYPE = "tiff";
		final String FILESTORE = "filestore";
		
		for (int i = 0; i < count; i++) {

			tempoInicialImagem = new Date();
			
			page = dec.decodeAsRenderedImage(i);

			//File f = new File("d:/single_" + i + ".tif");
			
			f = new File(caminhoFinal + i + EXTENSAO);
			
			System.out.println(SAVING + f.getCanonicalPath());

			pb = new ParameterBlock();

			pb.addSource(page);

			pb.add(f.toString());

			pb.add(MIMETYPE);

			pb.add(param);

			r = JAI.create(FILESTORE, pb);

			r.dispose();
			
			tempoFinalImagem= new Date();
			
			System.out.println(tempoFinalImagem.getSeconds() - tempoInicialImagem.getSeconds());
			
		}
		Date tempoFinal  = new Date();
		System.out.println("Tempo Inicial: " + tempoInicial);
		System.out.println("Tempo Final: " + tempoFinal);
		
	}
	


	public static ImageInfo renomear(File arquivoAntigo, String side) throws SecurityException, IOException, FileAlreadyExistsException {
		
		long id = System.currentTimeMillis();
		StringBuilder imageName = new StringBuilder(side).append(id)
				.append(StringEncryptor.removerAcentos(System.getenv("computername"))).append(count++)
				.append(".jpg");

		logger.info("Salvando imagem como tiff. {}", imageName.toString());

		File file = new File(System.getProperty("user.home")
				+ System.getProperty("file.separator")
				+ WebScannerConfig.getProperty("pathImage"),
				imageName.toString());
		file.getParentFile().mkdirs();

		//saveAsTIFF(toBufferedImage(img, BufferedImage.TYPE_BYTE_BINARY), file);
		//arquivoAntigo.
		// RENOMEA O ARQUIVO
		///arquivoAntigo.renameTo(file);
		Files.move(arquivoAntigo.toPath(), file.toPath());
		
		br.com.webscanner.model.domain.image.ImageInfo imageRetorno = new br.com.webscanner.model.domain.image.ImageInfo(
				id, file);

		logger.info("Imagem salva com sucesso");
		
		return imageRetorno;
		
	}

}
