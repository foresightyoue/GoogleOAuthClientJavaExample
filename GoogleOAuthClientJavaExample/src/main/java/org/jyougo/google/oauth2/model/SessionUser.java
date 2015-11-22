package org.jyougo.google.oauth2.model;

import com.google.api.client.auth.oauth2.StoredCredential;

import java.io.Serializable;

public class SessionUser implements Serializable {

  private static final long serialVersionUID = 2551780956991577083L;

  //Used as id to retrieve his oauth credentials.
  private String sessionId;

  private String orcIdRecord;

  //Use cookies to remember the user
  private Boolean remembeMe = Boolean.FALSE;

  private StoredCredential storedCredential;

  /**
   * Constructor.
   * @param sessionId
   *        Unique id for the user.
   * @param remembeMe
   *        "true" to Remember the user after the browser is closed.
   */
  public SessionUser(String sessionId, String remembeMe) {
    super();
    this.sessionId = sessionId;
    if ("true".equalsIgnoreCase(remembeMe)) {
      this.remembeMe = Boolean.TRUE;
    }
  }

  /**
   * Constructor.
   * @param sessionId
   *        Unique id for the user.
   * @param remembeMe
   *        Remember of not the user after the browser is closed.
   */
  public SessionUser(String sessionId, Boolean remembeMe) {
    super();
    this.sessionId = sessionId;
    this.remembeMe = remembeMe;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public Boolean getRemembeMe() {
    return remembeMe;
  }

  public void setRemembeMe(Boolean remembeMe) {
    this.remembeMe = remembeMe;
  }

  public String getOrcIdRecord() {
    return orcIdRecord;
  }

  public void setOrcIdRecord(String orcIdRecord) {
    this.orcIdRecord = orcIdRecord;
  }

  public StoredCredential getStoredCredential() {
    return storedCredential;
  }

  public void setStoredCredential(StoredCredential storedCredential) {
    this.storedCredential = storedCredential;
  }

}
