package uk.co.revsys.user.manager.camel;

import org.apache.camel.Exchange;
import uk.co.revsys.esb.component.HttpProxyProcessor;

public class GetAccountsProcessor extends HttpProxyProcessor{
    
    public GetAccountsProcessor(String string) {
        super(string);
    }

    @Override
    public String getHttpMethod() {
        return GET;
    }

    @Override
    public String getUrlPath(Exchange exchng) {
        return "/accounts/all";
    }

}
