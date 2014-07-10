package uk.co.revsys.user.manager.model;

import java.io.Serializable;

public abstract class AbstractEntity implements Serializable{
	
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
