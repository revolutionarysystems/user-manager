package uk.co.revsys.user.manager.model;

import javax.validation.constraints.NotNull;

public class Permission extends AbstractEntity{
	
	@NotNull
	private String name;
	@NotNull
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
