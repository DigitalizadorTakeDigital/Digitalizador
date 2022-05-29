package br.com.webscanner.model.domain;

/**
 * Representa um produto no menu de documentos. 
 * Contém o caminho onde o arquivo xml do produto está armazenado.
 * @author Jonathan Camara
 *
 */
public class MenuProduct implements Comparable<MenuProduct>{
	private String id;
	private String name;
	private String path;
	
	public MenuProduct(String id){
		this.id = id;
	}
	
	public MenuProduct(String id, String name, String path) {
		this.id = id;
		this.name = name;
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuProduct other = (MenuProduct) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(MenuProduct o) {
		return this.id.compareTo(o.getId());
	}
}