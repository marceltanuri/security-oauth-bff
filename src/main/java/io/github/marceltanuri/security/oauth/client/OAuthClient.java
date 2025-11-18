package io.github.marceltanuri.security.oauth.client;

/**
 *
 * @author Marcel Tanuri
 */
public interface OAuthClient {

	/**
	 *
	 * @return A valid access token as a String.
	 */
	public String getAccessToken();

	/**
	 * Retrieves the audience for which the access token is intended.
	 *
	 * @return The audience of the OAuth client as a String.
	 */
	public String getAudience();

	/**
	 * Retrieves the client identifier.
	 *
	 * @return The client ID of the OAuth client as a String.
	 */
	public String getClientId();

	/**
	 * Retrieves the client's descriptive name.
	 *
	 * @return The client name of the OAuth client as a String.
	 */
	public String getClientName();

	/**
	 * Retrieves the scope of the access request.
	 *
	 * @return The scope of the OAuth client as a String.
	 */
	public String getScope();

	/**
	 * Retrieves the base URL of the service.
	 *
	 * @return The base URL of the service as a String.
	 */
	public String getServiceBaseUrl();

	/**
	 * Retrieves the token endpoint of the OAuth service.
	 *
	_ @return The token endpoint of the OAuth service as a String.
	 */
	public String getTokenEndpoint();

}