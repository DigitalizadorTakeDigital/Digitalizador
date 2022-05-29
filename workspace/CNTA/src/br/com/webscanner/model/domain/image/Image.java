/**
 * 
 */
package br.com.webscanner.model.domain.image;

import com.thoughtworks.xstream.annotations.XStreamConverter;
import br.com.webscanner.model.domain.converter.ImageConverter;

/**
 * Representa uma imagem tiff.
 * @author Jonathan Camara
 *
 */
public class Image {
	@XStreamConverter(ImageConverter.class)
	private ImageInfo front;
	@XStreamConverter(ImageConverter.class)
	private ImageInfo rear;
	
	public Image(ImageInfo front, ImageInfo rear){
		this.front = front;
		this.rear = rear;
	}

	public ImageInfo getFront() {
		return front;
	}
	public ImageInfo getRear() {
		return rear;
	}
}