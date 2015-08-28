package uk.co.revsys.user.manager.camel;

import uk.co.revsys.esb.component.HttpProxyComponent;
import java.util.Map;

import org.apache.camel.Processor;

public class UserManagerComponent extends HttpProxyComponent{

    @Override
    protected void populateMappings(Map<String, Class<? extends Processor>> mappings) {
        mappings.put("getUser", GetUserProcessor.class);
        mappings.put("getAccount", GetAccountProcessor.class);
        mappings.put("getAccounts", GetAccountsProcessor.class);
        mappings.put("getAccountUsers", GetAccountUsersProcessor.class);
        mappings.put("createAccount", CreateAccountProcessor.class);
        mappings.put("activateAccount", ActivateAccountProcessor.class);
        mappings.put("disableAccount", DisableAccountProcessor.class);
        mappings.put("updateAccount", UpdateAccountProcessor.class);
        mappings.put("updateUser", UpdateUserProcessor.class);
        mappings.put("changePassword", ChangePasswordProcessor.class);
        mappings.put("addUser", AddUserProcessor.class);
        mappings.put("addRoleToAccount", AddRoleToAccountProcessor.class);
        mappings.put("addRoleToUser", AddRoleToUserProcessor.class);
        mappings.put("removeRoleFromAccount", RemoveRoleFromAccountProcessor.class);
        mappings.put("enableAccount", EnableAccountProcessor.class);
        mappings.put("verifyUser", VerifyUserProcessor.class);
    }
    
}
