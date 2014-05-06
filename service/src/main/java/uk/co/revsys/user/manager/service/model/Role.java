package uk.co.revsys.user.manager.service.model;

import java.util.List;
import javax.validation.constraints.NotNull;

public class Role extends AbstractEntity{

	@NotNull
	private String name;
	@NotNull
	private String description;
	private List<String> permissions;

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

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
}
