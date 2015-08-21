package uk.co.revsys.user.manager.client.esb;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import uk.co.revsys.user.manager.client.UserManagerClient;

public abstract class UserManagerProcessor implements Processor {

    private UserManagerClient userManagerClient;

    public UserManagerProcessor(UserManagerClient userManagerClient) {
        this.userManagerClient = userManagerClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        String cookie = message.getHeader("Cookie", String.class);
        if(cookie!=null && cookie.indexOf("JSESSIONID=") > -1){
            cookie = cookie + ";";
            String sessionId = cookie.substring(cookie.indexOf("JSESSIONID=") + "JSESSIONID=".length());
            sessionId = sessionId.substring(0, sessionId.indexOf(";"));
            userManagerClient.setAuthenticationToken(sessionId);
        }
        String result = doProcess(exchange);
        exchange.getIn().setHeader("Content-Type", "application/json");
        if(result==null){
            result = "";
        }
        exchange.getIn().setBody(result);
    }
    
    public abstract String doProcess(Exchange exchange) throws Exception;

}
