package io.github.marceltanuri.security.oauth.bff.proxy;

import javax.ws.rs.core.Response;

/**
 * Defines the contract for a generic proxy service. Implementations of this
 * interface are responsible for forwarding requests to a downstream service.
 *
 * @author Marcel Tanuri
 */
@FunctionalInterface
public interface ProxyService {

	/**
	 * Executes a proxy request using the information provided in the given
	 * {@link DefaultOAuthProxyService.ProxyRequestContext}.
	 *
	 * @param context The context containing all necessary information for the
	 *                proxy request, such as headers, HTTP method, and URI.
	 * @return A {@link Response} object representing the response from the
	 *         downstream service.
	 */
	public Response executeProxyRequest(
		DefaultOAuthProxyService.ProxyRequestContext context);

}