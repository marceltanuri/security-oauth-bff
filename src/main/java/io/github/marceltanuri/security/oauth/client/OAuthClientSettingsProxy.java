package io.github.marceltanuri.security.oauth.client;

import java.lang.annotation.Annotation;

public class OAuthClientSettingsProxy implements OAuthClientSettings {

	public OAuthClientSettingsProxy(OAuthClientSettings settings) {
		_settings = settings;
	}

	/**
	 * @return The annotation type of the wrapped settings.
	 */
	@Override
	public Class<? extends Annotation> annotationType() {
		return _settings.annotationType();
	}

	/**
	 * Returns the audience from an environment variable if available, otherwise
	 * from the wrapped settings.
	 *
	 * @return The audience value.
	 */
	@Override
	public String audience() {
		String clientName = _settings.clientName();

		String normalizedClientName = _normalizeForEnvVar(clientName);

		String envValue = _getEnvValue(normalizedClientName, "AUDIENCE");

		if (envValue != null) {
			return envValue;
		}

		return _settings.audience();
	}

	/**
	 * Returns the client ID from an environment variable if available, otherwise
	 * from the wrapped settings.
	 *
	 * @return The client ID value.
	 */
	@Override
	public String clientId() {
		String clientName = _settings.clientName();

		String normalizedClientName = _normalizeForEnvVar(clientName);

		String envValue = _getEnvValue(normalizedClientName, "CLIENT_ID");

		if (envValue != null) {
			return envValue;
		}

		return _settings.clientId();
	}

	/**
	 * Returns the client name from the wrapped settings.
	 *
	 * This setting cannot be overridden by an environment variable.
	 *
	 * @return The client name value.
	 */
	@Override
	public String clientName() {
		return _settings.clientName();
	}

	/**
	 * Returns the client secret from an environment variable if available,
	 * otherwise from the wrapped settings.
	 *
	 * @return The client secret value.
	 */
	@Override
	public String clientSecret() {
		String clientName = _settings.clientName();

		String normalizedClientName = _normalizeForEnvVar(clientName);

		String envValue = _getEnvValue(normalizedClientName, "CLIENT_SECRET");

		if (envValue != null) {
			return envValue;
		}

		return _settings.clientSecret();
	}

	/**
	 * Returns the scope from an environment variable if available, otherwise from
	 * the wrapped settings.
	 *
	 * @return The scope value.
	 */
	@Override
	public String scope() {
		String clientName = _settings.clientName();

		String normalizedClientName = _normalizeForEnvVar(clientName);

		String envValue = _getEnvValue(normalizedClientName, "SCOPE");

		if (envValue != null) {
			return envValue;
		}

		return _settings.scope();
	}

	/**
	 * Returns the service base URL from an environment variable if available,
	 * otherwise from the wrapped settings.
	 *
	 * @return The service base URL value.
	 */
	@Override
	public String serviceBaseUrl() {
		String clientName = _settings.clientName();

		String normalizedClientName = _normalizeForEnvVar(clientName);

		String envValue = _getEnvValue(
			normalizedClientName, "SERVICE_BASE_URL");

		if (envValue != null) {
			return envValue;
		}

		return _settings.serviceBaseUrl();
	}

	/**
	 * Returns the token endpoint from an environment variable if available,
	 * otherwise from the wrapped settings.
	 *
	 * @return The token endpoint value.
	 */
	@Override
	public String tokenEndpoint() {
		String clientName = _settings.clientName();

		String normalizedClientName = _normalizeForEnvVar(clientName);

		String envValue = _getEnvValue(normalizedClientName, "TOKEN_ENDPOINT");

		if (envValue != null) {
			return envValue;
		}

		return _settings.tokenEndpoint();
	}

	private static String _normalizeForEnvVar(String name) {
		if (name == null) {
			return "";
		}

		return name.toUpperCase(
		).replaceAll(
			"[^A-Z0-9_]+", "_"
		);
	}

	private String _getEnvValue(String normalizedClientName, String suffix) {
		for (String prefix : _ENV_VAR_PREFIXES) {
			String envVarName =
				prefix + "_" + normalizedClientName + "_" + suffix;

			String envValue = System.getenv(envVarName);

			if (envValue != null) {
				return envValue;
			}
		}

		return null;
	}

	private static final String[] _ENV_VAR_PREFIXES = {
		_normalizeForEnvVar(
			OAuthClientSettings.class.getPackage(
			).getName()),
		"OAUTH2_CLIENTS"

	};

	private final OAuthClientSettings _settings;

}