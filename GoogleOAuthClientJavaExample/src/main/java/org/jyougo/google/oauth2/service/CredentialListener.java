package org.jyougo.google.oauth2.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow.CredentialCreatedListener;

/**
 * CredentialCreatedListener invoked every time a user logs into OrcId OAuth. This class manages a
 * map to register the returned OrcId from the authentication service to the AccessToken stored in
 * the user credential.
 * @author JYouGo
 */
public class CredentialListener implements CredentialCreatedListener {

  private final Logger logger = LogManager.getLogger(CredentialListener.class);

  /**
   * Map to store the OrcId returned in the authentication header for the accesstoken.
   */
  private Map<String, String> orcIdsMap = new HashMap<>();

  @Override
  public void onCredentialCreated(Credential credential, TokenResponse tokenResponse)
      throws IOException {
    logger.debug("Entering onCredentialCreated()");
    String orcId = (String) tokenResponse.getUnknownKeys().get("orcid");
    String name = (String) tokenResponse.getUnknownKeys().get("name");
    String accessToken = credential.getAccessToken();
    orcIdsMap.putIfAbsent(accessToken, orcId);
    logger.debug("AccessToken {} Mapped to OrcID {} for {}. \nExiting onCredentialCreated()",
        accessToken, orcId, name);
  }

  /**
   * Returns the OrcId related to the accessToken. The mapping is discarded after the return.
   * @param accessToken
   *        User access token for authentication
   * @return OrcIdRecord of the user
   */
  public String pullOrcId(String accessToken) {
    logger.debug("Entering pullOrcId({})", accessToken);
    String record = orcIdsMap.get(accessToken);
    if (record != null) {
      logger.debug("OrcID {} mapping removed for AccessToken {}", record, accessToken);
      orcIdsMap.remove(accessToken);
    }
    logger.debug("Exiting pullOrcId()");
    return record;
  }

}
