package uk.co.revsys.user.manager.client;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

public class PasswordGenerator {

    public String generate(){
        return RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(13) + 8);
    }
    
}
