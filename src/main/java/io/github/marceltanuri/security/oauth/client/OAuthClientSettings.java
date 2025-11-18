package io.github.marceltanuri.security.oauth.client;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Marcel Tanuri
 */
@ObjectClassDefinition(name = "OAuth Client Settings")
public @interface OAuthClientSettings {

	/**
	 * The unique name of the OAuth client configuration.
	 * This name is used to identify and retrieve the correct client service.
	 */
	@AttributeDefinition(
		description = "The unique name of the OAuth client configuration.",
		name = "Client Name"
	)
	String clientName();

	/**
	 * The token endpoint URL of the OAuth server.
	 */
	@AttributeDefinition(
		description = "The token endpoint URL of the OAuth server.",
		name = "Token Endpoint"
	)
	String tokenEndpoint();

	/**
	 * The client ID for the OAuth client.
	 */
	@AttributeDefinition(
		description = "The client ID for the OAuth client.", name = "Client ID"
	)
	String clientId();

	/**
	 * The client secret for the OAuth client.
	 */
	@AttributeDefinition(
		description = "The client secret for the OAuth client.",
		name = "Client Secret"
	)
	String clientSecret();

	/**
	 * The audience for the OAuth client.
	 */
	@AttributeDefinition(
		description = "The audience for the OAuth client.", name = "Audience"
	)
	String audience() default "";

	/**
	 * The scope for the OAuth client.
	 */
	@AttributeDefinition(
		description = "The scope for the OAuth client.", name = "Scope"
	)
	String scope() default "";

	/**
	 * The base URL for the service to be called by this OAuth client.
	 */
	@AttributeDefinition(
		description = "The base URL for the service to be called by this OAuth client.",
		name = "Service Base URL"
	)
	String serviceBaseUrl() default "";

}