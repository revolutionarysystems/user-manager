package uk.co.revsys.user.manager.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AccountJacksonMixin {

    private String verificationCode;
    
    @JsonIgnore
    public String getVerificationCode() {
        return verificationCode;
    }

    @JsonProperty
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
