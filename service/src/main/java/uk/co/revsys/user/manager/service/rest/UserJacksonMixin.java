package uk.co.revsys.user.manager.service.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class UserJacksonMixin {

	private String password;
	@JsonIgnore
	private String passwordSalt;

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
	
}
