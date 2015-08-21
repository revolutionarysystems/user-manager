package uk.co.revsys.user.manager.client;

import java.io.IOException;

public class RawAccountClient extends RawEntityClientImpl{

    public RawAccountClient(UserManagerConnector connector) {
        super(connector, "accounts");
    }
    
    public String getUsers(String account) throws IOException{
        return get(account + "/users");
    }

}
