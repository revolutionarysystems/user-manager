package uk.co.revsys.user.manager.model;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;

public class Account extends AbstractEntity{

	@NotNull
	private String name;
	@NotNull
	private Status status;
	private List<Application> applications;
	private Map<String, Object> attributes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
}
