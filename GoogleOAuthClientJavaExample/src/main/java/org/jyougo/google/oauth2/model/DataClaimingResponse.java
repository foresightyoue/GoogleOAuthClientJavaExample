package org.jyougo.google.oauth2.model;

import java.io.Serializable;

public class DataClaimingResponse implements Serializable {

  private static final long serialVersionUID = -6635718540722183171L;

  private Boolean isUserLoggedIn;
  private String loginUrl;
  private String logoutUrl;
  private String orcIdRecord;

  public Boolean getIsUserLoggedIn() {
    return isUserLoggedIn;
  }

  public void setIsUserLoggedIn(Boolean isUserLoggedIn) {
    this.isUserLoggedIn = isUserLoggedIn;
  }

  public String getLoginUrl() {
    return loginUrl;
  }

  public void setLoginUrl(String loginUrl) {
    this.loginUrl = loginUrl;
  }

  public String getOrcIdRecord() {
    return orcIdRecord;
  }

  public void setOrcIdRecord(String orcIdRecord) {
    this.orcIdRecord = orcIdRecord;
  }

  public String getLogoutUrl() {
    return logoutUrl;
  }

  public void setLogoutUrl(String logoutUrl) {
    this.logoutUrl = logoutUrl;
  }

}
