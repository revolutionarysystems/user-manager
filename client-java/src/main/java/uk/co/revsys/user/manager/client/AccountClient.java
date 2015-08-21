package uk.co.revsys.user.manager.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.util.List;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.User;

public class AccountClient extends EntityClientImpl<RawAccountClient, Account> {

    public AccountClient(UserManagerConnector connector, ObjectMapper objectMapper) {
        super(new RawAccountClient(connector), objectMapper, Account.class);
    }

    public List<User> getUsers(String account) throws IOException {
        return objectMapper.readValue(rawClient.getUsers(account), TypeFactory.defaultInstance().constructCollectionType(List.class, User.class));
    }

}
