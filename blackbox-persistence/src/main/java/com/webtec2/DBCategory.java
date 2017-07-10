package com.webtec2;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="category", uniqueConstraints={
	@UniqueConstraint(columnNames = "name")
})
@XmlRootElement
public class DBCategory {
	private String name;

	public DBCategory(String name) {
		this.name = name;
	}

	public DBCategory() {
	}

	@Id
	@Column(name = "name")
	@JsonProperty(value="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}

