package io.github.marceltanuri.security.oauth.client;

/**
 * Unchecked exception thrown for errors that occur within the OAuth client,
 * such as failures during the token retrieval or validation process.
 *
 * @author Marcel Tanuri
 */
public class OAuthClientException extends RuntimeException {

	/**
	 * Constructs a new {@code OAuthClientException} with the specified detail
	 * message.
	 *
	 * @param message The detail message.
	 */
	public OAuthClientException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code OAuthClientException} with the specified detail
	 * message and cause.
	 *
	 * @param message The detail message.
	 * @param cause   The cause of the exception.
	 */
	public OAuthClientException(String message, Throwable cause) {
		super(message, cause);
	}

}