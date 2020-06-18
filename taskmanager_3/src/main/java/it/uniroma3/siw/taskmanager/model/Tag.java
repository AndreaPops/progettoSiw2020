package it.uniroma3.siw.taskmanager.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Tag {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column
	private String name;
	private String color;
	private String description;
	@ManyToMany(mappedBy="listaTag")
	private List<Task> listaTask;	
	public Tag() {
		listaTask=new ArrayList<>();
	}

	public Tag(String nome,String colore,String descrizione) {
		this.name=nome;
		this.color=colore;
		this.description=descrizione;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Tag other = (Tag) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tag [id=");
		builder.append(id);
		builder.append(", nome=");
		builder.append(name);
		builder.append(", colore=");
		builder.append(color);
		builder.append(", descrizione=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

	public List<Task> getListaTask() {
		return listaTask;
	}

	public void setListaTask(List<Task> listaTask) {
		this.listaTask = listaTask;
	}



}
