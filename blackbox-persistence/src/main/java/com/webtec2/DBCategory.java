 package com.webtec2;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

@Entity
@Table(name="category", uniqueConstraints={
	@UniqueConstraint(columnNames = "id")
})
@XmlRootElement
public class DBCategory extends DBIdentified {
	private String name;
	private String description;
	private Date createdOn;

	public DBCategory() { }
	
	public DBCategory(String name, String description)
	{
		this.name = name;
		this.description = description;
		this.createdOn = new Date();
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonProperty(value="created_on")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@JsonProperty(value="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty(value="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

