package uk.co.revsys.user.manager.filter;

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;

public class RolesAuthorisationFilter extends RolesAuthorizationFilter{

    private final String role;
    
    public RolesAuthorisationFilter(String role) {
        this.role = role;
    }

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        mappedValue = new String[]{role};
        return super.isAccessAllowed(request, response, mappedValue);
    }

}
