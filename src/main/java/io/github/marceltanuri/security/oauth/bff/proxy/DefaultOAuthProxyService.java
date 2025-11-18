package io.github.marceltanuri.security.oauth.bff.proxy;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import io.github.marceltanuri.security.oauth.client.OAuthClient;
import io.github.marceltanuri.security.oauth.client.OAuthServiceFactory;

import java.net.URI;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Default implementation of the {@link ProxyService}. This class orchestrates
 * the entire proxy workflow, which includes:
 * <ul>
 *     <li>Retrieving the appropriate {@link OAuthClient} configuration.</li>
 *     <li>Fetching an OAuth 2.0 access token.</li>
 *     <li>Constructing the target URI for the downstream service.</li>
 *     <li>Executing the request and forwarding the response to the original caller.</li>
 * </ul>
 * It is registered as an OSGi component and relies on an {@link OAuthServiceFactory}
 * to get client configurations.
 * 
 * @author Marcel Tanuri
 */
@Component(
    immediate = true,
    service = ProxyService.class
)
public class DefaultOAuthProxyService implements ProxyService {

	/**
	 * Executes the proxy request based on the provided context.
	 *
	 * @param context The {@link ProxyRequestContext} containing all necessary
	 *                details for the request, such as the client name, path,
	 *                and HTTP method.
	 * @return A {@link Response} object that mirrors the response from the
	 *         downstream service. If an error occurs, it returns an
	 *         appropriate error response.
	 */
	@Override
	public Response executeProxyRequest(ProxyRequestContext context) {
		OAuthClient oAuthService = _oAuthServiceFactory.getOAuthClient(
			context.getClientName());

		if (oAuthService == null) {
			String errorMessage = String.format(
				"OAuth client configuration not found for: %s",
				context.getClientName());

			return Response.status(
				Response.Status.NOT_FOUND
			).entity(
				errorMessage
			).build();
		}

		try {
			String accessToken = _getTokenOrThrow(oAuthService, context);

			URI targetURI = _buildTargetUri(oAuthService, context);

			if (_log.isDebugEnabled()) {
				String logMessage = String.format(
					"Proxying %s request to URI: %s",
					context.getMethodHandler(
					).name(),
					targetURI);

				_log.debug(logMessage);
			}

			return _executeClientCallAndMapResponse(
				targetURI, context, accessToken);
		}
		catch (RuntimeException runtimeException) {
			_log.error(
				String.format(
					"Error retrieving token for client %s",
					context.getClientName()),
				runtimeException);

			String errorMessage = String.format(
				"Error retrieving token for client %s: %s",
				context.getClientName(), runtimeException.getMessage());

			return Response.status(
				Response.Status.INTERNAL_SERVER_ERROR
			).entity(
				errorMessage
			).build();
		}
		catch (Exception exception) {
			_log	.error(
				String.format(
					"Error proxying request (%s)",
					context.getMethodHandler(
					).name()),
				exception);

			String errorMessage = String.format(
				"Error proxying request: %s", exception.getMessage());

			return Response.status(
				Response.Status.INTERNAL_SERVER_ERROR
			).entity(
				errorMessage
			).build();
		}
	}

	/**
	 * A data holder class that encapsulates all the necessary information
	 * for a proxy request. It is instantiated using a {@link Builder}.
	 */
	public static class ProxyRequestContext {

		/**
		 * Creates a new instance of the {@link Builder}.
		 *
		 * @return A new {@link Builder} instance.
		 */
		public static Builder builder() {
			return new Builder();
		}

		/**
		 * Returns the name of the client configuration to use.
		 *
		 * @return The client name.
		 */
		public String getClientName() {
			return _clientName;
		}

		/**
		 * Returns the HTTP method handler for the request.
		 *
		 * @return The {@link HttpMethodHandler}.
		 */
		public HttpMethodHandler getMethodHandler() {
			return _methodHandler;
		}

		/**
		 * Returns the request path.
		 *
		 * @return The request path.
		 */
		public String getPath() {
			return _path;
		}

		/**
		 * Returns the query string of the request.
		 *
		 * @return The query string.
		 */
		public String getQueryString() {
			return _queryString;
		}

		/**
		 * Returns the request body.
		 *
		 * @return The request body as a String.
		 */
		public String getRequestBody() {
			return _requestBody;
		}

		/**
		 * A builder class for creating instances of {@link ProxyRequestContext}.
		 */
		public static class Builder {

			/**
			 * Builds the {@link ProxyRequestContext} instance.
			 *
			 * @return A new {@link ProxyRequestContext} instance.
			 * @throws IllegalStateException if the client name or method handler
			 *                               is not set.
			 */
			public ProxyRequestContext build() {
				if ((_clientName == null) || (_methodHandler == null)) {
					throw new IllegalStateException(
						"Client name and method handler must be set");
				}

				return new ProxyRequestContext(this);
			}

			/**
			 * Sets the client name for the request context.
			 *
			 * @param clientName The name of the client configuration.
			 * @return The builder instance.
			 */
			public Builder clientName(String clientName) {
				_clientName = clientName;

				return this;
			}

			/**
			 * Sets the HTTP method handler for the request context.
			 *
			 * @param methodHandler The {@link HttpMethodHandler}.
			 * @return The builder instance.
			 */
			public Builder methodHandler(HttpMethodHandler methodHandler) {
				_methodHandler = methodHandler;

				return this;
			}

			/**
			 * Sets the path for the request context.
			 *
			 * @param path The request path.
			 * @return The builder instance.
			 */
			public Builder path(String path) {
				_path = path;

				return this;
			}

			/**
			 * Sets the query string for the request context.
			 *
			 * @param queryString The request query string.
			 * @return The builder instance.
			 */
			public Builder queryString(String queryString) {
				_queryString = queryString;

				return this;
			}

			/**
			 * Sets the request body for the request context.
			 *
			 * @param requestBody The request body.
			 * @return The builder instance.
			 */
			public Builder requestBody(String requestBody) {
				_requestBody = requestBody;

				return this;
			}

			private String _clientName;
			private HttpMethodHandler _methodHandler;
			private String _path;
			private String _queryString;
			private String _requestBody;

		}

		private ProxyRequestContext(Builder builder) {
			_clientName = builder._clientName;
			_path = builder._path;
			_methodHandler = builder._methodHandler;
			_requestBody = builder._requestBody;
			_queryString = builder._queryString;
		}

		private final String _clientName;
		private final HttpMethodHandler _methodHandler;
		private final String _path;
		private final String _queryString;
		private final String _requestBody;

	}

	/**
	 * An enum representing the supported HTTP methods for the proxy service.
	 * It provides a method to execute the corresponding JAX-RS client request.
	 */
	public enum HttpMethodHandler {

		DELETE, GET, POST, PUT;

		/**
		 * Executes the corresponding HTTP method on the JAX-RS
		 * {@link Invocation.Builder}.
		 *
		 * @param builder The JAX-RS invocation builder.
		 * @param entity  The entity to be sent with the request (for POST, PUT,
		 *                and DELETE).
		 * @return The {@link Response} from the downstream service.
		 * @throws UnsupportedOperationException if the HTTP method is not supported.
		 */
		public Response execute(Invocation.Builder builder, Entity<?> entity) {
			switch (this) {
				case DELETE:
					return builder.method("DELETE", entity);
				case GET:
					return builder.get();
				case POST:
					return builder.post(entity);
				case PUT:
					return builder.put(entity);
				default:
					throw new UnsupportedOperationException(
						"Unsupported HTTP method: " + name());
			}
		}

	}

	/**
	 * Builds the target URI for the downstream service by combining the service
	 * base URL with the request path and query string.
	 *
	 * @param oAuthService The {@link OAuthClient} configuration containing the
	 *                     base URL.
	 * @param context      The {@link ProxyRequestContext} containing the path and
	 *                     query string.
	 * @return The constructed {@link URI} for the downstream service.
	 * @throws Exception if the URI syntax is invalid.
	 */
	private URI _buildTargetUri(
			OAuthClient oAuthService, ProxyRequestContext context)
		throws Exception {

		String targetBaseUrl = oAuthService.getServiceBaseUrl();

		if (!targetBaseUrl.contains("://")) {
			targetBaseUrl = "https://" + targetBaseUrl;
		}

		String path = context.getPath();

		String targetPath;

		if (targetBaseUrl.endsWith("/") && path.startsWith("/")) {
			targetPath = path.substring(1);
		}
		else if (!targetBaseUrl.endsWith("/") && !path.startsWith("/")) {
			targetPath = "/" + path;
		}
		else {
			targetPath = path;
		}

		String targetUrl = targetBaseUrl + targetPath;

		String queryString = context.getQueryString();

		if ((queryString != null) && !queryString.isEmpty()) {
			targetUrl += "?" + queryString;
		}

		return new URI(targetUrl);
	}

	/**
	 * Executes the client call to the downstream service and maps the response
	 * to a JAX-RS {@link Response} object that can be returned to the original
	 * caller.
	 *
	 * @param targetURI   The target {@link URI} of the downstream service.
	 * @param context     The {@link ProxyRequestContext} for the request.
	 * @param accessToken The OAuth 2.0 access token.
	 * @return A {@link Response} object mirroring the downstream service's response.
	 * @throws Exception if an error occurs during the client call.
	 */
	private Response _executeClientCallAndMapResponse(
			URI targetURI, ProxyRequestContext context, String accessToken)
		throws Exception {

		Client client = null;
		Response proxyResponse = null;

		try {
			client = _clientBuilder.build();

			WebTarget target = client.target(targetURI);

			Invocation.Builder requestBuilder = target.request();

			String maskedToken = (accessToken.length() > 10) ?
				(accessToken.substring(0, 10) + "...") : accessToken;

			requestBuilder.header("Authorization", "Bearer " + accessToken);

			_log.debug("Request Headers: Authorization=Bearer " + maskedToken);

			Entity<String> entity = null;

			if ((context.getRequestBody() != null) &&
				!context.getRequestBody(
				).isEmpty()) {

				entity = Entity.entity(
					context.getRequestBody(), MediaType.APPLICATION_JSON);

				_log.debug(
					"Request Headers: Content-Type=" +
						MediaType.APPLICATION_JSON);
				_log.debug("Request Body: " + context.getRequestBody());
			}
			else {
				if ((context.getMethodHandler() == HttpMethodHandler.GET) ||
					(context.getMethodHandler() == HttpMethodHandler.DELETE)) {

					requestBuilder.header("Content-Type", null);
					_log.debug(
						"Request Headers: Content-Type=null (Forced omission " +
							"for GET/DELETE to satisfy AWS)");
				}
				else {
					_log.debug(
						"Request Headers: Content-Type=omitted (No Body)");
				}
			}

			proxyResponse = context.getMethodHandler(
			).execute(
				requestBuilder, entity
			);

			String responseEntity = proxyResponse.readEntity(String.class);

			Response.ResponseBuilder responseBuilder = Response.status(
				proxyResponse.getStatus()
			).entity(
				responseEntity
			);

			MultivaluedMap<String, Object> headers = proxyResponse.getHeaders();

			for (Map.Entry<String, List<Object>> headerEntry :
					headers.entrySet()) {

				String name = headerEntry.getKey();

				if (!name.equalsIgnoreCase("Transfer-Encoding") &&
					!name.equalsIgnoreCase("Content-Encoding")) {

					for (Object value : headerEntry.getValue()) {
						responseBuilder.header(name, value);
					}
				}
			}

			responseBuilder.type(proxyResponse.getMediaType());

			return responseBuilder.build();
		}
		finally {
			if (client != null) {
				client.close();
			}

			if (proxyResponse != null) {
				proxyResponse.close();
			}
		}
	}

	/**
	 * Retrieves the access token from the {@link OAuthClient}. If the token
	 * retrieval fails, it logs the error and re-throws the exception.
	 *
	 * @param oAuthService The {@link OAuthClient} service to use for fetching
	 *                     the token.
	 * @param context      The {@link ProxyRequestContext} for logging purposes.
	 * @return The access token as a String.
	 * @throws RuntimeException if token retrieval fails.
	 */
	private String _getTokenOrThrow(
			OAuthClient oAuthService, ProxyRequestContext context)
		throws RuntimeException {

		try {
			return oAuthService.getAccessToken();
		}
		catch (RuntimeException runtimeException) {
			_log.error(
				"Error retrieving token for client " + context.getClientName(),
				runtimeException);

			throw runtimeException;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultOAuthProxyService.class);

	@Reference
	private ClientBuilder _clientBuilder;

	@Reference
	private OAuthServiceFactory _oAuthServiceFactory;

}