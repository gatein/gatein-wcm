package org.gatein.wcm.ui.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;

/**
 * Filter to redirect pretty url to /wcm servlet
 */
@WebFilter("/wcm/*")
public class WcmFilter implements Filter {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.ui.filter");

    @Override
    public void destroy() {
        log.info("destroy");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        if (req instanceof HttpServletRequest) {
            String url = ((HttpServletRequest)req).getRequestURL().toString();
            String context = url.substring(url.indexOf("/gatein-wcm-ui") + "gatein-wcm-ui".length() + 1);
            if (context.startsWith("/wcm")) {
                req.setAttribute("url", context);
                req.getRequestDispatcher("/wcm").forward(req, resp);
            }
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig cfg) throws ServletException {
        log.info("init()");
    }
}
