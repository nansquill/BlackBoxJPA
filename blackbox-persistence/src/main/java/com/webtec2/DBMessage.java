package com.webtec2;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.webtec2.DBCategory;
import com.webtec2.DBUser;

@Entity
@Table(name="message", uniqueConstraints={
	@UniqueConstraint(columnNames = "id")
})
@XmlRootElement
public class DBMessage extends DBIdentified {

	private DBCategory category;
	private DBUser user;
	private String headline;
	private String content;
	private Date publishedOn;
	
	public DBMessage() { }

	public DBMessage(DBUser user, DBCategory category, String headline, String content)
	{
		this.user = user;
		this.headline = headline;
		this.content = content;
		this.category = category;
		this.publishedOn = new Date();
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonProperty(value="published_on")
	@Column(name="published_on")
	public Date getPublishedOn() {
		return publishedOn;
	}

	public void setPublishedOn(Date publishedOn) {
		this.publishedOn = publishedOn;
	}
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user")
	@JsonProperty(value="user")
	public DBUser getUser()	{
		return user;
	}
	
	public void setUser(DBUser user)	{
		this.user = user;
	}

	@JsonProperty(value="headline")
	@Column(name="headline")
	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	@JsonProperty(value="content")
	@Column(name="content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="category")
	@JsonProperty(value="category")
	public DBCategory getCategory() {
		return category;
	}

	public void setCategory(DBCategory category) {
		this.category = category;
	}
}

