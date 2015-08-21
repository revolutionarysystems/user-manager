package uk.co.revsys.user.manager.client;

import java.io.IOException;

public class RawUserClient extends RawEntityClientImpl{

    public RawUserClient(UserManagerConnector connector) {
        super(connector, "users");
    }
    
    public String getMe() throws IOException{
        return get("me");
    }
    
    public void changePassword(String id, String password) throws IOException{
        post(id + "/changePassword", "application/x-www-form-urlencoded", "password=" + password);
    }

}
