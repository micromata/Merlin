package de.micromata.merlin.app.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Ensuring the user data inside request threads. For now, it's only a simple implementation (no login required).
 * Only the user's (client's) locale is used.
 */
public class UserFilter implements Filter {
    private Logger log = LoggerFactory.getLogger(UserFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        try {
            UserData userData = UserUtils.getUser();
            if (userData != null) {
                log.warn("****************************************");
                log.warn("***********                   **********");
                log.warn("*********** SECURITY WARNING! **********");
                log.warn("***********                   **********");
                log.warn("*********** Internal error:   **********");
                log.warn("*********** User already set! **********");
                log.warn("***********                   **********");
                log.warn("****************************************");
                log.warn("Don't deliver this app in dev mode due to security reasons!");
                String message = "User already given for this request. Rejecting request due to security reasons. Given user: " + userData;
                log.error(message);
                throw new IllegalArgumentException(message);
            }
            userData = UserManager.instance().getUser("dummy");
            UserUtils.setUser(userData, request.getLocale());
            if (log.isDebugEnabled()) log.debug("Request for user: " + userData);
            chain.doFilter(request, response);
        } finally {
            UserUtils.removeUser();
        }
    }

    @Override
    public void destroy() {
    }

}
