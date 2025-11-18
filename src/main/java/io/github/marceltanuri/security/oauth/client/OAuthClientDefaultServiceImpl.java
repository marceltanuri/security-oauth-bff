package io.github.marceltanuri.security.oauth.client;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import io.github.marceltanuri.security.commons.oauth.token.api.TokenService;
import io.github.marceltanuri.security.commons.oauth.token.api.TokenServiceException;


/**
 * Default implementation of the {@link OAuthClient} interface. This class is
 * responsible for obtaining an OAuth 2.0 access token by delegating the request
 * to a {@link TokenService}. It is initialized with a set of
 * {@link OAuthClientSettings}.
 */
public class OAuthClientDefaultServiceImpl implements OAuthClient {

	/**
	 * Constructs a new {@code OAuthClientDefaultServiceImpl} with the specified
	 * settings and token service.
	 *
	 * @param settings     The {@link OAuthClientSettings} containing the
	 *                     configuration for this client.
	 * @param tokenService The {@link TokenService} used to fetch the access token.
	 */
	public OAuthClientDefaultServiceImpl(
		OAuthClientSettings settings, TokenService tokenService) {

		_settings = settings;

		_clientCredentialsSettings =
			TokenService.ClientCredentialsSettings.builder(
			).clientId(
				_settings.clientId()
			).clientSecret(
				_settings.clientSecret()
			).tokenEndpoint(
				_settings.tokenEndpoint()
			).scope(
				_settings.scope()
			).audience(
				_settings.audience()
			).build();

		_tokenService = tokenService;
	}

	/**
	 * Retrieves a valid OAuth 2.0 access token using the configured
	 * {@link TokenService}. If the token fetch fails, it logs an error and
	 * throws a {@link RuntimeException}.
	 *
	 * @return A valid access token as a String.
	 * @throws RuntimeException if the underlying {@link TokenService} fails to
	 *                          retrieve a token.
	 */
	@Override
	public String getAccessToken() {
		try {
			return _tokenService.getAccessToken(_clientCredentialsSettings);
		}
		catch (TokenServiceException tokenServiceException) {
			_log.error("Error fetching access token", tokenServiceException);
			_log.error(
				"Failed to obtain OAuth access token. " +
					_clientCredentialsSettings.toString());
			tokenServiceException.printStackTrace();
			System.out.println(tokenServiceException.getMessage());
			throw new RuntimeException(
				"failed to obtain OAuth access token", tokenServiceException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAudience() {
		return _settings.audience();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getClientId() {
		return _settings.clientId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getClientName() {
		return _settings.clientName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getScope() {
		return _settings.scope();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getServiceBaseUrl() {
		return _settings.serviceBaseUrl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTokenEndpoint() {
		return _settings.tokenEndpoint();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuthClientDefaultServiceImpl.class);

	private TokenService.ClientCredentialsSettings _clientCredentialsSettings;
	private OAuthClientSettings _settings;
	private TokenService _tokenService;

}