package com.webtec2;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import com.webtec2.DBCategory;
import com.webtec2.DBUser;

@Entity
@XmlRootElement
public class DBMessage extends DBIdentified {

	private DBCategory category;
	private User user;
	private String headline;
	private String content;
	private Date publishedOn;
	private boolean isOnline;

	@Temporal(TemporalType.TIMESTAMP)
	public Date getPublishedOn() {
		return publishedOn;
	}

	public void setPublishedOn(Date publishedOn) {
		this.publishedOn = publishedOn;
	}
	
	public DBUser getUser()	{
		return user;
	}
	
	public void setUser(DBUser user)	{
		this.user = user;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public DBCategory getCategory() {
		return category;
	}

	public void setCategory(DBCategory category) {
		this.category = category;
	}

	public boolean getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
}

