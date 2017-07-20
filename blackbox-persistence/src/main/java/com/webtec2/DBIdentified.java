package com.webtec2;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@MappedSuperclass
public class DBIdentified {

	private long id;

	@Id
	@GeneratedValue
	@JsonProperty(value="id")
	@Column(name="id")
	public long getId() {
		return this.id;
	}

	public void setId(final long id) {
		this.id = id;
	}

}
