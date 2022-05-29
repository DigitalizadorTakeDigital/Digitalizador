package br.com.webscanner.model.domain;

import java.util.ArrayList;
import java.util.List;

public class Extension {
	private String type;
	private String description;
	private List<String> exts;
	private long maxSize;
	private long minSize;
	private boolean duplex;
	private boolean bothRequired;

	public Extension(String type, String description, long maxSize, long minSize, boolean duplex, boolean bothRequired) {
		super();
		this.type = type;
		this.description = description;
		this.maxSize = maxSize;
		this.minSize = minSize;
		this.duplex = duplex;
		this.bothRequired = bothRequired;
		this.exts = new ArrayList<String>();
	}
	
	public String getType() {
		return type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<String> getExts() {
		return exts;
	}

	public boolean addExtension(String ext){
		return this.exts.add(ext);
	}
	
	public long getMaxSize() {
		return maxSize;
	}
	
	public long getMinSize() {
		return minSize;
	}

	public boolean isDuplex() {
		return duplex;
	}
	
	public boolean isBothRequired() {
		return bothRequired;
	}
	
	@Override
	public String toString() {
		return this.description;
	}
}