package uk.co.revsys.user.manager.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormAuthenticationFilter extends AuthenticatingFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormAuthenticationFilter.class);

	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
		return new UsernamePasswordToken(request.getParameter("username"), request.getParameter("password"));
	}

	@Override
	protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
		return request.getParameter("username")!=null && request.getParameter("password")!=null;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		if (isLoginRequest(request, response)) {
			return executeLogin(request, response);
		} else {
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
	}
}
