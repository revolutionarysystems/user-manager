package uk.co.revsys.user.manager.service.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class UserJacksonMixin {

	@JsonIgnore
	private String password;
	@JsonIgnore
	private String passwordSalt;
	
}
