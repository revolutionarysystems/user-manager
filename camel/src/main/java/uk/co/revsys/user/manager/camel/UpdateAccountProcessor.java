package uk.co.revsys.user.manager.camel;

import uk.co.revsys.esb.component.HttpProxyProcessor;

public class UpdateAccountProcessor extends UpdateEntityProcessor{
    
    public UpdateAccountProcessor(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public String getEntityType() {
        return "accounts";
    }

    

}
