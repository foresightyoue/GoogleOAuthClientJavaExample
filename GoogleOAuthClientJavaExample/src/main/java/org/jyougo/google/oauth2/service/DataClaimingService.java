package org.jyougo.google.oauth2.service;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jyougo.google.oauth2.model.SessionUser;

/**
 * Interface that provides Http Request/Response services to manage the user authentication.
 * @author JYouGo
 */
public interface DataClaimingService {

  public static final String URL_LOGIN = "/dataclaiming/login";
  public static final String URL_CALLBACKLOGIN = "/dataclaiming/logincallback";
  public static final String URL_LOGOUT = "/dataclaiming/logout";

  public String getCallBackUrl(HttpServletRequest request);

  public String getLoginUrl(HttpServletRequest request);

  public String getLogoutUrl(HttpServletRequest request);

  /**
   * After successful authentication, let the user log in the application.
   * @param req
   *        HttpServletRequest
   * @param resp
   *        HttpServletResponse
   * @param credential
   *        Credential
   */
  public void
      registerUserLogin(HttpServletRequest req, HttpServletResponse resp, Credential credential);

  /**
   * Let the user log out the application and delete "remember-me" cookie.
   * @param req
   *        HttpServletRequest
   * @param resp
   *        HttpServletResponse
   * @param credential
   *        Credential
   */
  public void
      registerUserLogout(HttpServletRequest req, HttpServletResponse resp);

  /**
   * Create if necessary and return the current user from session, with the unique identification
   * and remember me parameters filled.
   * @param request
   *        HttpServletRequest for the user
   * @return SessionUser
   */
  public SessionUser retrieveUserSession(HttpServletRequest request);

  /**
   * Creates an authorization flow for google OAuth.
   * @param addCredentialCreateListener
   *        true to add a Credential Create Listener to the flow.
   * @return AuthorizationCodeFlow
   * @throws IOException
   *         exception
   */
  public AuthorizationCodeFlow createAuthorizationFlow(boolean addCredentialCreateListener)
      throws IOException;

  /**
   * Check if the user have signed with ORCID.
   * @param sessionUser
   *        SessionUser of current user
   * @return true if user exists in HTTP session
   */
  public Boolean isUserLoggedIn(SessionUser sessionUser);

  /**
   * Fetches all Works from ORCID and retrieve the ORCID record. The Uses must have already logged
   * in with ORCID.
   * @param request
   *        HttpServletRequest
   * @return OrcIdRecord in String
   * @throws Exception
   *         exception
   */
  public String loadOrcIdRecord(HttpServletRequest request)
      throws Exception;

}
