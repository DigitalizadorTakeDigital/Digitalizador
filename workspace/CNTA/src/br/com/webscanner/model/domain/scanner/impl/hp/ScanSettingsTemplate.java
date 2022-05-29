package br.com.webscanner.model.domain.scanner.impl.hp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ScanSettingsTemplate")
public class ScanSettingsTemplate implements Cloneable {

	private Tag labelA;
	private Tag labelB;
	private Tag type;
	private Tag color;
	private Tag resolution;
	private Tag duplex;
	private Tag autoCrop;
	private Tag autoDeskew;
	private Tag detectBlank;
	private Tag source;
	private Tag mediaSize;
	private Tag multiPage;
	private Tag jobDescription;
	private Tag pageContent;
	private Tag qualityMode;
	private Tag sharpness;
	private Tag darkness;
	private Tag contrast;
	private Tag backgrdRemove;
	private Tag orientation;
	private Tag hideDeleteButton;
	private Tag fileName;
	
	@XmlElement(name="LabelA")
	public Tag getLabelA() {
		return labelA;
	}
	public void setLabelA(Tag labelA) {
		this.labelA = labelA;
	}
	@XmlElement(name="LabelB")
	public Tag getLabelB() {
		return labelB;
	}
	public void setLabelB(Tag labelB) {
		this.labelB = labelB;
	}
	@XmlElement(name="Type")
	public Tag getType() {
		return type;
	}
	public void setType(Tag type) {
		this.type = type;
	}
	@XmlElement(name="Color")
	public Tag getColor() {
		return color;
	}
	public void setColor(Tag color) {
		this.color = color;
	}
	@XmlElement(name="Resolution")
	public Tag getResolution() {
		return resolution;
	}
	public void setResolution(Tag resolution) {
		this.resolution = resolution;
	}
	@XmlElement(name="Duplex")
	public Tag getDuplex() {
		return duplex;
	}
	public void setDuplex(Tag duplex) {
		this.duplex = duplex;
	}
	@XmlElement(name="AutoCrop")
	public Tag getAutoCrop() {
		return autoCrop;
	}
	public void setAutoCrop(Tag autoCrop) {
		this.autoCrop = autoCrop;
	}
	@XmlElement(name="AutoDeskew")
	public Tag getAutoDeskew() {
		return autoDeskew;
	}
	public void setAutoDeskew(Tag autoDeskew) {
		this.autoDeskew = autoDeskew;
	}
	@XmlElement(name="DetectBlank")
	public Tag getDetectBlank() {
		return detectBlank;
	}
	public void setDetectBlank(Tag detectBlank) {
		this.detectBlank = detectBlank;
	}
	@XmlElement(name="Source")
	public Tag getSource() {
		return source;
	}
	public void setSource(Tag source) {
		this.source = source;
	}
	@XmlElement(name="MediaSize")
	public Tag getMediaSize() {
		return mediaSize;
	}
	public void setMediaSize(Tag mediaSize) {
		this.mediaSize = mediaSize;
	}
	@XmlElement(name="MultiPage")
	public Tag getMultiPage() {
		return multiPage;
	}
	public void setMultiPage(Tag multiPage) {
		this.multiPage = multiPage;
	}
	@XmlElement(name="JobDescription")
	public Tag getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(Tag jobDescription) {
		this.jobDescription = jobDescription;
	}
	@XmlElement(name="PageContent")
	public Tag getPageContent() {
		return pageContent;
	}
	public void setPageContent(Tag pageContent) {
		this.pageContent = pageContent;
	}
	@XmlElement(name="QualityMode")
	public Tag getQualityMode() {
		return qualityMode;
	}
	public void setQualityMode(Tag qualityMode) {
		this.qualityMode = qualityMode;
	}
	@XmlElement(name="Sharpness")
	public Tag getSharpness() {
		return sharpness;
	}
	public void setSharpness(Tag sharpness) {
		this.sharpness = sharpness;
	}
	@XmlElement(name="Darkness")
	public Tag getDarkness() {
		return darkness;
	}
	public void setDarkness(Tag darkness) {
		this.darkness = darkness;
	}
	@XmlElement(name="Contrast")
	public Tag getContrast() {
		return contrast;
	}
	public void setContrast(Tag contrast) {
		this.contrast = contrast;
	}
	@XmlElement(name="BackgrdRemove")
	public Tag getBackgrdRemove() {
		return backgrdRemove;
	}
	public void setBackgrdRemove(Tag backgrdRemove) {
		this.backgrdRemove = backgrdRemove;
	}
	@XmlElement(name="Orientation")
	public Tag getOrientation() {
		return orientation;
	}
	public void setOrientation(Tag orientation) {
		this.orientation = orientation;
	}
	@XmlElement(name="HideDeleteButton")
	public Tag getHideDeleteButton() {
		return hideDeleteButton;
	}
	public void setHideDeleteButton(Tag hideDeleteButton) {
		this.hideDeleteButton = hideDeleteButton;
	}
	@XmlElement(name="FileName")
	public Tag getFileName() {
		return fileName;
	}
	public void setFileName(Tag fileName) {
		this.fileName = fileName;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		ScanSettingsTemplate template = new ScanSettingsTemplate();
		template.labelA = (Tag) this.labelA.clone();
		template.labelB = (Tag) this.labelB.clone();
		template.type = (Tag) this.type.clone();
		template.color = (Tag) this.color.clone();
		template.resolution = (Tag) this.resolution.clone();
		template.duplex = (Tag) this.duplex.clone();
		template.autoCrop = (Tag) this.autoCrop.clone();
		template.autoDeskew = (Tag) this.autoDeskew.clone();
		template.detectBlank = (Tag) this.detectBlank.clone();
		template.source = (Tag) this.source.clone();
		template.mediaSize = (Tag) this.mediaSize.clone();
		template.multiPage = (Tag) this.multiPage.clone();
		template.jobDescription = (Tag) this.jobDescription.clone();
		template.pageContent = (Tag) this.pageContent.clone();
		template.qualityMode = (Tag) this.qualityMode.clone();
		template.sharpness = (Tag) this.sharpness.clone();
		template.darkness = (Tag) this.darkness.clone();
		template.contrast = (Tag) this.contrast.clone();
		template.backgrdRemove = (Tag) this.backgrdRemove.clone();
		template.orientation = (Tag) this.orientation.clone();
		template.hideDeleteButton = (Tag) this.hideDeleteButton.clone();
		template.fileName = (Tag) this.fileName.clone();
		
		return template;
	}
}
