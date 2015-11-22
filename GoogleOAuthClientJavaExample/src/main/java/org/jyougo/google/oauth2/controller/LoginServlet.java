package org.jyougo.google.oauth2.controller;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;

import org.jyougo.google.oauth2.service.DataClaimingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;


/**
 * Servlet implementation of AbstractAuthorizationCodeServlet
 * Responsible for the user login thru OrcId OAuth.
 * 
 * @author JYouGo
 *
 */
@WebServlet(DataClaimingService.URL_LOGIN)
public class LoginServlet extends AbstractAuthorizationCodeServlet {

  private static final long serialVersionUID = 8833967981024864211L;
  
  @Autowired
  private transient DataClaimingService service;

  @Override
  protected String getRedirectUri(HttpServletRequest req)
      throws ServletException, IOException {
    return service.getCallBackUrl(req);
  }

  @Override
  protected String getUserId(HttpServletRequest request)
      throws ServletException, IOException {
    return service.retrieveUserSession(request).getSessionId();
  }

  @Override
  protected AuthorizationCodeFlow initializeFlow()
      throws ServletException, IOException {
    return service.createAuthorizationFlow(false);
  }

  @Override
  public void init(final ServletConfig config)
      throws ServletException {
    super.init(config);
    WebApplicationContext springContext = WebApplicationContextUtils
        .getRequiredWebApplicationContext(config.getServletContext());
    final AutowireCapableBeanFactory beanFactory = springContext.getAutowireCapableBeanFactory();
    beanFactory.autowireBean(this);
  }

}
