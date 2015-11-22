package org.jyougo.google.oauth2.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class SimpleCORSFilter.
 */
@WebFilter("/api/*")
public class SimpleCorsFilter implements Filter {

  /**
   * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain).
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse) res;
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Headers",
        "Origin, X-Requested-With, Content-Type, Accept");
    chain.doFilter(req, res);
  }

  /**
   * @see Filter#destroy().
   */
  @Override
  public void destroy() {
    //Implementation of abstract method.
  }

  /**
   * @see Filter#init(FilterConfig).
   */
  @Override
  public void init(FilterConfig filterConfig)
      throws ServletException {
    //Implementation of abstract method.
  }

}
