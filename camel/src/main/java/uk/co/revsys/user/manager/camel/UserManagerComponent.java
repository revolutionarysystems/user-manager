package uk.co.revsys.user.manager.camel;

import uk.co.revsys.esb.component.HttpProxyComponent;
import java.util.Map;

import org.apache.camel.Processor;

public class UserManagerComponent extends HttpProxyComponent{

    @Override
    protected void populateMappings(Map<String, Class<? extends Processor>> mappings) {
        mappings.put("getUser", GetUserProcessor.class);
        mappings.put("createAccount", CreateAccountProcessor.class);
        mappings.put("activateAccount", ActivateAccountProcessor.class);
        mappings.put("disableAccount", DisableAccountProcessor.class);
        mappings.put("updateAccount", UpdateAccountProcessor.class);
        mappings.put("updateUser", UpdateUserProcessor.class);
        mappings.put("changePassword", ChangePasswordProcessor.class);
    }
    
}
