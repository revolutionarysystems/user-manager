package uk.co.revsys.user.manager.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class UserJacksonMixin {

	private String password;
	private String passwordSalt;

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
    
    @JsonIgnore
    public String getPasswordSalt() {
        return password;
    }

    @JsonProperty
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }
	
}
