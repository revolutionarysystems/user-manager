package uk.co.revsys.user.manager.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import uk.co.revsys.user.manager.model.User;

public class UserClient extends EntityClientImpl<RawUserClient, User> {

    public UserClient(UserManagerConnector connector, ObjectMapper objectMapper) {
        super(new RawUserClient(connector), objectMapper, User.class);
    }
    
    public User getMe() throws IOException{
        return objectMapper.readValue(rawClient.getMe(), entityClass);
    }
    
    public void changePassword(String id, String password) throws IOException{
        rawClient.changePassword(id, password);
    }

}
