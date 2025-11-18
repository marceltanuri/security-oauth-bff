package io.github.marceltanuri.security.oauth.client;

import io.github.marceltanuri.security.commons.oauth.token.api.TokenService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

/**
 * An OSGi factory component responsible for creating and managing configured
 * instances of the {@link OAuthClient} service. Each instance is configured
 * through the OSGi Configuration Admin service using the properties defined in
 * {@link OAuthClientSettings}. This manager delegates all client operations to an
 * internal {@link OAuthClientDefaultServiceImpl} instance.
 *
 * @author Marcel Tanuri
 */
@Component(
	configurationPid = "io.github.marceltanuri.security.oauth.client.OAuthClientSettings",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "clientName={clientName}", service = OAuthClient.class
)
@Designate(factory = true, ocd = OAuthClientSettings.class)
public class OAuthClientManager implements OAuthClient {

	/**
	 * Activates the component, creating and initializing a new instance of the
	 * default OAuth client implementation ({@link OAuthClientDefaultServiceImpl})
	 * with the provided settings.
	 *
	 * @param settings The {@link OAuthClientSettings} configuration for this
	 *                 client instance.
	 */
	@Activate
	public void activate(OAuthClientSettings settings) {
		_service = new OAuthClientDefaultServiceImpl(
			new OAuthClientSettingsProxy(settings), _tokenService);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAccessToken() {
		return _service.getAccessToken();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAudience() {
		return _service.getAudience();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getClientId() {
		return _service.getClientId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getClientName() {
		return _service.getClientName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getScope() {
		return _service.getScope();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getServiceBaseUrl() {
		return _service.getServiceBaseUrl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTokenEndpoint() {
		return _service.getTokenEndpoint();
	}

	private OAuthClient _service;

	@Reference
	private TokenService _tokenService;

}