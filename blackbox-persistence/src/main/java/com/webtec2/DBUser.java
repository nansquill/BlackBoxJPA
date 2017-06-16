package com.webtec2;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Entity
@XmlRootElement
public class DBUser extends DBIdentified {

	private String username;
	private String password;
	private Date createdOn;
	private Date lastVisitedOn;
	private String group;
	private boolean isAdmin;

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastVisitedOn() {
		return lastVisitedOn;
	}

	public void setLastVisitedOn(Date lastVisitedOn) {
		this.lastVisitedOn = lastVisitedOn;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getGroup() {
		return group;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}

