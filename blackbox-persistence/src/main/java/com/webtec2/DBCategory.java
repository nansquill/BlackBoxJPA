package com.webtec2;

import java.util.*;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

@Entity
@Table(name="category", uniqueConstraints={
	@UniqueConstraint(columnNames = "name")
})
@XmlRootElement
public class DBCategory{
	private String name;

	public DBCategory() { }
	
	public DBCategory(String name)
	{
		this.name = name;
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

