package com.webtec2;

import java.io.Serializable;

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
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.webtec2.DBMessage;

@Entity
@Table(name="user", uniqueConstraints = {
	@UniqueConstraint(columnNames="id")
})
@XmlRootElement
public class DBUser extends DBIdentified implements Serializable{

	private static final long serialVersionUID = -4746333924452133573L;
	
	private String username;
	private String password;
	private String salt;
	private Date createdOn;
	private Date lastVisitedOn;
	private boolean isAdmin;
	
	public DBUser() { }
	
	public DBUser(String username, String password)
	{
		this.username = username;
		this.password = password;
		this.createdOn = new Date();
		this.lastVisitedOn = new Date();
		this.isAdmin = false;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonProperty(value="last_visited_on")
	@Column(name="last_visited_on")
	public Date getLastVisitedOn() {
		return lastVisitedOn;
	}

	public void setLastVisitedOn(Date lastVisitedOn) {
		this.lastVisitedOn = lastVisitedOn;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonProperty(value="created_on")
	@Column(name="created_on")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@JsonProperty(value="username")
	@Column(name="username")
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@JsonIgnore
	@JsonProperty(value="salt")
	@Column(name="salt")
	public String getSalt() {
		return salt;
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}

	@JsonIgnore
	@JsonProperty(value="password")
	@Column(name="password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@JsonProperty(value="is_admin")
	@Column(name="is_admin")
	public boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

}

