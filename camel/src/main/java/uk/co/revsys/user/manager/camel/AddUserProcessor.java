package uk.co.revsys.user.manager.camel;

import org.apache.camel.Exchange;
import uk.co.revsys.esb.component.HttpProxyProcessor;

public class AddUserProcessor extends HttpProxyProcessor {

    private String account;

    public AddUserProcessor(String baseUrl) {
        super(baseUrl);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getHttpMethod() {
        return POST;
    }

    @Override
    public String getContentType() {
        return APPLICATION_JSON;
    }

    @Override
    public String getUrlPath(Exchange exchange) {
        return "/accounts/" + getAccount()+ "/addUser";
    }

}
