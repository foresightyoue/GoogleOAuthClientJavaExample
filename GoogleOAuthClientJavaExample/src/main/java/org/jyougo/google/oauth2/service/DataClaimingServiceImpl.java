package org.jyougo.google.oauth2.service;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jyougo.google.oauth2.model.SessionUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Class for Http Request/Cookies management and to implement user authentication at OrcId based on
 * Google OAuth.
 * @author JYouGo
 */
public class DataClaimingServiceImpl implements DataClaimingService {

  private static final Logger LOGGER = LogManager.getLogger(DataClaimingServiceImpl.class);

  private static final String COOKIE_NAME = "OrcIdUser";
  private static final String USERID_SESSION = "ORCIDUSERID";
  private static final String PARAM_REMEMBER_ME = "remind";

  private String serverName; //server name:port for url redirection
  private String accessTokenUri;
  private JsonFactory jsonFactory;
  private HttpTransport httpTransport;

  //Client App id
  private String clientId;
  private String clientSecret;

  private String userAuthorizationUri;
  private Collection<String> scopes;

  private CredentialListener credentialListener = new CredentialListener();

  /**
   * @see DataClaimingService#registerUserLogin (HttpServletRequest, HttpServletResponse,
   *      Credential).
   */
  @Override
  public void
      registerUserLogin(HttpServletRequest req, HttpServletResponse resp, Credential credential) {
    LOGGER.debug("Entering registerUserLogin()");
    registerCookie(req, resp);
    registerOrcIdDetails(req, credential);
    LOGGER.debug("Exiting registerUserLogin()");
  }

  /**
   * @see DataClaimingService#registerUserLogout(HttpServletRequest, HttpServletResponse,
   *      Credential) .
   */
  @Override
  public void
      registerUserLogout(HttpServletRequest req, HttpServletResponse resp) {
    LOGGER.debug("Entering registerUserLogout()");
    HttpSession session = req.getSession();
    SessionUser sessionUser = (SessionUser) session.getAttribute(USERID_SESSION);
    if (sessionUser != null) {
      //Remove "remember-me" cookie
      sessionUser.setRemembeMe(Boolean.FALSE);
      registerCookie(req, resp);
    }
    //Remove user from session
    session.invalidate();
    LOGGER.debug("Exiting registerUserLogout()");
  }

  /**
   * Adds the OrcId number and Credential to the Session User.
   * @param req
   *        HttpServletRequest
   * @param credential
   *        Credential
   */
  private void registerOrcIdDetails(HttpServletRequest req, Credential credential) {
    LOGGER.debug("Entering  registerOrcId()");
    String accessToken = credential.getAccessToken();
    String record = credentialListener.pullOrcId(accessToken);
    retrieveUserSession(req).setOrcIdRecord(record);
    retrieveUserSession(req).setStoredCredential(new StoredCredential(credential));
    LOGGER.debug("Exiting registerOrcId()");
  }

  /**
   * Manages the cookie to remember the user or not. If the user wants to be remembered, add the
   * cookie; if not, removes it.
   * @param req
   *        Must contain the SessionUser with the parameter set to remember the user or not
   * @param resp
   *        HttpServletResponse with added cookie information.
   */
  private void registerCookie(HttpServletRequest req, HttpServletResponse resp) {
    LOGGER.debug("Entering  registerCookie()");
    HttpSession session = req.getSession();
    SessionUser sessionUser = (SessionUser) session.getAttribute(USERID_SESSION);
    Cookie cookie = retrieveUserIdCookie(req);

    //Check if user wants to be remembered next time that access the application...
    if (sessionUser != null && sessionUser.getRemembeMe()) {
      if (cookie == null) {
        //creates a cookie to remember the user
        cookie = new Cookie(COOKIE_NAME, retrieveUserSession(req).getSessionId());
        cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
        resp.addCookie(cookie);
        LOGGER.debug("Cookie added to HttpResponse.");
      }
    } else { //The user doesnt want to be remembered 
      if (cookie != null) { //Removes the cookie if it exists.
        cookie.setValue(null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        LOGGER.debug("Cookie removed from HttpResponse.");
      }
    }
    LOGGER.debug("Exiting  registerCookie()");
  }

  /**
   * @see DataClaimingService#isUserLoggedIn(HttpSession) .
   */
  @Override
  public Boolean isUserLoggedIn(SessionUser sessionUser) {
    LOGGER.debug("Entering isUserLoggedIn()");
    //The user is considered signed in if we know his orcid.
    if (sessionUser != null && sessionUser.getOrcIdRecord() != null
        && sessionUser.getOrcIdRecord() != null) {
      LOGGER.debug("Exiting isUserLoggedIn() : TRUE");
      return Boolean.TRUE;
    }
    LOGGER.debug("Exiting isUserLoggedIn() : FALSE");
    return Boolean.FALSE;
  }

  /**
   * @see DataClaimingService#loadOrcIdRecord(HttpServletRequest) .
   */
  @Override
  public String loadOrcIdRecord(HttpServletRequest request)
      throws Exception {
    LOGGER.debug("Entering loadOrcIdRecord()");
    String record = null;
    SessionUser sessionUser = retrieveUserSession(request);
    if (isUserLoggedIn(sessionUser)) {
      record = sessionUser.getOrcIdRecord();
    }
    LOGGER.debug("Exiting loadOrcIdRecord()");
    return record;
  }

  @Override
  public String getLogoutUrl(HttpServletRequest request) {
    return createUrl(request.getContextPath(), URL_LOGOUT);
  }

  @Override
  public String getLoginUrl(HttpServletRequest request) {
    return createUrl(request.getContextPath(), URL_LOGIN);
  }

  @Override
  public String getCallBackUrl(HttpServletRequest request) {
    return createUrl(request.getContextPath(), URL_CALLBACKLOGIN);
  }

  /**
   * Creates a url string for the server:port configured at the "serverName" variable.
   * @param appContext
   *        Application context
   * @param path
   *        Url Path
   * @return Complete Url
   */
  private String createUrl(String appContext, String path) {
    LOGGER.debug("Entering createUrl({},{})", appContext, path);
    GenericUrl url = new GenericUrl(serverName);
    url.setRawPath(appContext + path);
    String result = url.build();
    LOGGER.debug("Exiting createUrl(): {}", result);
    return result;
  }

  /**
   * Retrive the cookie that stores the user id.
   * @param request
   *        HttpServletRequest
   * @return Unique user id or null if not found in the cookies.
   */
  private Cookie retrieveUserIdCookie(HttpServletRequest request) {
    LOGGER.debug("Entering retrieveUserIdCookie()");
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        Cookie cookie = cookies[i];
        if (COOKIE_NAME.equals(cookie.getName())) {
          LOGGER.debug("Exiting retrieveUserIdCookie(): {}", cookie.toString());
          return cookie;
        }
      }
    }
    LOGGER.debug("Exiting retrieveUserIdCookie(): null");
    return null;
  }

  /**
   * @see DataClaimingService#retrieveUserId(HttpServletRequest) .
   */
  @Override
  public SessionUser retrieveUserSession(HttpServletRequest request) {
    LOGGER.debug("Entering retrieveUserSession()");
    HttpSession session = request.getSession();

    //Checks if is user already in http session...
    if (session.getAttribute(USERID_SESSION) != null) {
      LOGGER.debug("user ID found in Http Session");
      return (SessionUser) session.getAttribute(USERID_SESSION);
    } else {
      String userId = null;
      //...checks if is returning user with id saved in cookie...
      Cookie cookie = retrieveUserIdCookie(request);
      if (cookie != null) {
        userId = cookie.getValue();
        LOGGER.debug("user ID {} found in Cookie", userId);
        //TODO load the saved credential/orcid of user to make the remember me work
      }

      //If user not in session or cookie, creates a new unique user id
      if (userId == null) {
        userId = UUID.randomUUID().toString();
        LOGGER.debug("user ID {} created", userId);
      }

      String rememberMe = request.getParameter(PARAM_REMEMBER_ME);
      LOGGER.debug("Parameter 'Remember me': {}", rememberMe);
      SessionUser sessionUser = new SessionUser(userId, rememberMe);
      session.setAttribute(USERID_SESSION, sessionUser);//Add user info to http session

      LOGGER.debug("Exiting retrieveUserSession()");
      return sessionUser;
    }
  }

  /**
   * @see DataClaimingService#createAuthorizationFlow() .
   */
  @Override
  public AuthorizationCodeFlow createAuthorizationFlow(boolean addCredentialCreateListener)
      throws IOException {
    LOGGER.debug("Entering createAuthorizationFlow({})", addCredentialCreateListener);
    AuthorizationCodeFlow.Builder flow = new AuthorizationCodeFlow.Builder(
        BearerToken.authorizationHeaderAccessMethod(),
        httpTransport,
        jsonFactory, new GenericUrl(accessTokenUri),
        new BasicAuthentication(clientId, clientSecret), clientId,
        userAuthorizationUri)
            .setScopes(scopes)
            .setCredentialDataStore(
                //TODO user DB to store
                StoredCredential.getDefaultDataStore(new MemoryDataStoreFactory()));

    //Adds a Listener to be invoked every time a new credential 
    //is created (every time a user makes login at OrcId OAuth)
    if (addCredentialCreateListener) {
      LOGGER.debug("adding CredentialCreateListener");
      flow.setCredentialCreatedListener(credentialListener);
    }

    LOGGER.debug("Exiting createAuthorizationFlow()");
    return flow.build();
  }

  /**
   * Converst an storedCredential into a Credential.
   * @param storedCredential
   *        must contain accesstoken
   * @return Credential
   */
  public Credential buildCredential(StoredCredential storedCredential) {
    Credential credential = new GoogleCredential.Builder()
        .setClientSecrets(clientId, clientSecret)
        .setTransport(httpTransport)
        .setJsonFactory(jsonFactory).build();

    credential.setRefreshToken(storedCredential.getRefreshToken());
    credential.setAccessToken(storedCredential.getAccessToken());
    credential.setExpirationTimeMilliseconds(storedCredential.getExpirationTimeMilliseconds());

    return credential;
  }

  /**
   * Convert the scopes string (separated by ';') into a collection of strings.
   * @param oauthScopes
   *        String of scopes separated by ';'
   */
  public void setScopes(String oauthScopes) {
    if (oauthScopes != null) {
      this.scopes = Arrays.asList(oauthScopes.split(";"));
    } else {
      this.scopes = null;
    }
  }

  public String getAccessTokenUri() {
    return accessTokenUri;
  }

  public void setAccessTokenUri(String accessTokenUri) {
    this.accessTokenUri = accessTokenUri;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getUserAuthorizationUri() {
    return userAuthorizationUri;
  }

  public void setUserAuthorizationUri(String userAuthorizationUri) {
    this.userAuthorizationUri = userAuthorizationUri;
  }

  public String getServerName() {
    return serverName;
  }

  public void setServerName(String serverName) {
    this.serverName = serverName;
  }

  public JsonFactory getJsonFactory() {
    return jsonFactory;
  }

  public void setJsonFactory(JsonFactory jsonFactory) {
    this.jsonFactory = jsonFactory;
  }

  public HttpTransport getHttpTransport() {
    return httpTransport;
  }

  public void setHttpTransport(HttpTransport httpTransport) {
    this.httpTransport = httpTransport;
  }
  
}
