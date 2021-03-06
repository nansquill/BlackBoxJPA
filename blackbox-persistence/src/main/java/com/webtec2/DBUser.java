package com.webtec2;

import java.util.*;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="user", uniqueConstraints={
	@UniqueConstraint(columnNames = "username")
})
@XmlRootElement
public class DBUser {
	
	private String username;
	private String password;
	
	public DBUser(String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	public DBUser() {
	}
	
	@Id
	@Column(name = "username")
	@JsonProperty(value="username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		if(this.username == null)
			this.username = username;
	}
	
	@Column(name = "password")
	@JsonProperty(value="password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if(this.password == null)
			this.password = password;
	}
}

