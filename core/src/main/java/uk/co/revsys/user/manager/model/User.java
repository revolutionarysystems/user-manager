package uk.co.revsys.user.manager.model;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;

public class User extends AbstractEntity{

	@NotNull
	private String name;
	@NotNull
	private String username;
	@NotNull
	private String password;
	@NotNull
	private Status status;
	private Map<String, Object> attributes;
	@NotNull
	private String accountId;
	private List<String> roles;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setPassword(String password) {
		this.password = password;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
}
