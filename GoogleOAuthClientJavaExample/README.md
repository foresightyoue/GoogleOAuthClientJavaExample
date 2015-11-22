Example to show how to use Google OAuth Client Library for Java

It is an easy-to-use Java library for the OAuth 1.0a and OAuth 2.0 authorization standards. (https://github.com/google/google-oauth-java-client)

The Google OAuth Client Library for Java is designed to work with any OAuth service on the web, not just with Google APIs. It is built on the Google HTTP Client Library for Java.

It is an open-source library, and contributions are welcome.

More references:
https://developers.google.com/api-client-library/java/google-oauth-java-client/

How to try this example:

1. Download the source code
2. Register a client application at ORCID Sandbox, and you'll get an client ID and the corresponding secret.
3. Put the client ID and secret to file /src/main/resources/raw.application.properties
4. Run mvn install to generate the war file, GoogleOAuthClientJavaExample.war
5. Run Tomcat with the war file
6. Start the authorization process via the following URL:
http://localhost:8080/GoogleOAuthClientJavaExample/dataclaiming/login 