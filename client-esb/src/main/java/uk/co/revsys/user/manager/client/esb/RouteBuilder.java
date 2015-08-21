package uk.co.revsys.user.manager.client.esb;

import org.apache.camel.Exchange;
import uk.co.revsys.user.manager.client.UserManagerClient;

public class RouteBuilder extends org.apache.camel.builder.RouteBuilder {

    private UserManagerClient userManagerClient;

    public RouteBuilder(UserManagerClient userManagerClient) {
        this.userManagerClient = userManagerClient;
    }

    @Override
    public void configure() throws Exception {
        from("direct-vm://esb.event.usermanager.getMe").process(new UserManagerProcessor(userManagerClient) {

            @Override
            public String doProcess(Exchange exchange) throws Exception {
                return userManagerClient.users().raw().getMe();
            }
        });
        from("direct-vm://esb.event.usermanager.getAccounts").process(new UserManagerProcessor(userManagerClient) {

            @Override
            public String doProcess(Exchange exchange) throws Exception {
                return userManagerClient.accounts().raw().findAll();
            }
        });
        from("direct-vm://esb.event.usermanager.getAccountUsers").process(new UserManagerProcessor(userManagerClient) {

            @Override
            public String doProcess(Exchange exchange) throws Exception {
                return userManagerClient.accounts().raw().getUsers(exchange.getIn().getHeader("account", String.class));
            }
        });
        from("direct-vm://esb.event.usermanager.getUser").process(new UserManagerProcessor(userManagerClient) {

            @Override
            public String doProcess(Exchange exchange) throws Exception {
                return userManagerClient.users().raw().findById(exchange.getIn().getHeader("id", String.class));
            }
        });
        from("direct-vm://esb.event.usermanager.deleteUser").process(new UserManagerProcessor(userManagerClient) {

            @Override
            public String doProcess(Exchange exchange) throws Exception {
                userManagerClient.users().raw().delete(exchange.getIn().getHeader("id", String.class));
                return null;
            }
        });
        from("direct-vm://esb.event.usermanager.changePassword").process(new UserManagerProcessor(userManagerClient) {

            @Override
            public String doProcess(Exchange exchange) throws Exception {
                userManagerClient.users().raw().changePassword(exchange.getIn().getHeader("id", String.class), exchange.getIn().getHeader("password", String.class));
                return null;
            }
        });        
    }

}
