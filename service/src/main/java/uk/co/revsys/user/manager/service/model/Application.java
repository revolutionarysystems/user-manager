package uk.co.revsys.user.manager.service.model;

import java.util.Map;
import javax.validation.constraints.NotNull;

public class Application extends AbstractEntity{

	@NotNull
	private String name;
	private Map<String, Object> attributes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
}
