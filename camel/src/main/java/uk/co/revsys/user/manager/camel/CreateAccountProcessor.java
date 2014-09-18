package uk.co.revsys.user.manager.camel;

import uk.co.revsys.esb.component.HttpProxyProcessor;

public class CreateAccountProcessor extends HttpProxyProcessor{
    
    public CreateAccountProcessor(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public String getContentType() {
        return APPLICATION_JSON;
    }

    @Override
    public String getHttpMethod() {
        return POST;
    }

    @Override
    public String getUrlPath() {
        return "/accounts";
    }

}
