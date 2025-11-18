package io.github.marceltanuri.security.oauth.bff.proxy;

import io.github.marceltanuri.security.oauth.bff.proxy.DefaultOAuthProxyService.HttpMethodHandler;
import io.github.marceltanuri.security.oauth.bff.proxy.DefaultOAuthProxyService.ProxyRequestContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * JAX-RS resource class that exposes endpoints for the proxy service.
 * This class defines the API paths for proxying requests to downstream
 * services, handling different HTTP methods (GET, POST, PUT, DELETE).
 * It extracts path parameters, the request body, and query strings to
 * construct a {@link ProxyRequestContext} and then delegates the execution
 * to the {@link ProxyService}.
 *
 * @author Marcel Tanuri
 */
@Component(
	immediate = true,
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=OAuthBff.Rest)",
		"osgi.jaxrs.resource=true"
	},
	service = ProxyResource.class
)
public class ProxyResource {

	/**
	 * Handles HTTP DELETE requests for the proxy.
	 *
	 * @param clientName The name of the client configuration to use, extracted
	 *                   from the path.
	 * @param path       The downstream service path to proxy the request to.
	 * @param uriInfo    The {@link UriInfo} context, used to extract the query
	 *                   string.
	 * @return A {@link Response} from the downstream service.
	 */
	@DELETE
	@Path(_PROXY_PATH)
	@Produces(MediaType.WILDCARD)
	public Response proxyDeleteRequest(
		@PathParam("clientName") String clientName,
		@PathParam("path") String path, @Context UriInfo uriInfo) {

		ProxyRequestContext context = ProxyRequestContext.builder(
		).clientName(
			clientName
		).path(
			path
		).methodHandler(
			HttpMethodHandler.DELETE
		).queryString(
			_getQueryString(uriInfo)
		).build();

		return _executeProxyRequest(context);
	}

	/**
	 * Handles HTTP GET requests for the proxy.
	 *
	 * @param clientName The name of the client configuration to use, extracted
	 *                   from the path.
	 * @param path       The downstream service path to proxy the request to.
	 * @param uriInfo    The {@link UriInfo} context, used to extract the query
	 *                   string.
	 * @return A {@link Response} from the downstream service.
	 */
	@GET
	@Path(_PROXY_PATH)
	@Produces(MediaType.WILDCARD)
	public Response proxyGetRequest(
		@PathParam("clientName") String clientName,
		@PathParam("path") String path, @Context UriInfo uriInfo) {

		ProxyRequestContext context = ProxyRequestContext.builder(
		).clientName(
			clientName
		).path(
			path
		).methodHandler(
			HttpMethodHandler.GET
		).queryString(
			_getQueryString(uriInfo)
		).build();

		return _executeProxyRequest(context);
	}

	/**
	 * Handles HTTP POST requests for the proxy.
	 *
	 * @param clientName  The name of the client configuration to use, extracted
	 *                    from the path.
	 * @param path        The downstream service path to proxy the request to.
	 * @param requestBody The body of the POST request.
	 * @param uriInfo     The {@link UriInfo} context, used to extract the query
	 *                    string.
	 * @return A {@link Response} from the downstream service.
	 */
	@Consumes(MediaType.WILDCARD)
	@POST
	@Path(_PROXY_PATH)
	@Produces(MediaType.WILDCARD)
	public Response proxyPostRequest(
		@PathParam("clientName") String clientName,
		@PathParam("path") String path, String requestBody,
		@Context UriInfo uriInfo) {

		ProxyRequestContext context = ProxyRequestContext.builder(
		).clientName(
			clientName
		).path(
			path
		).methodHandler(
			HttpMethodHandler.POST
		).requestBody(
			requestBody
		).queryString(
			_getQueryString(uriInfo)
		).build();

		return _executeProxyRequest(context);
	}

	/**
	 * Handles HTTP PUT requests for the proxy.
	 *
	 * @param clientName  The name of the client configuration to use, extracted
	 *                    from the path.
	 * @param path        The downstream service path to proxy the request to.
	 * @param requestBody The body of the PUT request.
	 * @param uriInfo     The {@link UriInfo} context, used to extract the query
	 *                    string.
	 * @return A {@link Response} from the downstream service.
	 */
	@Consumes(MediaType.WILDCARD)
	@Path(_PROXY_PATH)
	@Produces(MediaType.WILDCARD)
	@PUT
	public Response proxyPutRequest(
		@PathParam("clientName") String clientName,
		@PathParam("path") String path, String requestBody,
		@Context UriInfo uriInfo) {

		ProxyRequestContext context = ProxyRequestContext.builder(
		).clientName(
			clientName
		).path(
			path
		).methodHandler(
			HttpMethodHandler.PUT
		).requestBody(
			requestBody
		).queryString(
			_getQueryString(uriInfo)
		).build();

		return _executeProxyRequest(context);
	}

	/**
	 * Executes the generic proxy request by delegating it to the injected
	 * {@link ProxyService}.
	 *
	 * @param context The {@link ProxyRequestContext} containing all request details.
	 * @return The {@link Response} returned by the proxy service.
	 */
	private Response _executeProxyRequest(ProxyRequestContext context) {
		return _proxyService.executeProxyRequest(context);
	}

	/**
	 * Extracts the query string from the {@link UriInfo}.
	 *
	 * @param uriInfo The JAX-RS {@link UriInfo} context.
	 * @return The query string from the request URI, or {@code null} if not present.
	 */
	private String _getQueryString(UriInfo uriInfo) {
		return uriInfo.getRequestUri(
		).getQuery();
	}

	private static final String _PROXY_PATH = "/{clientName}/proxy/{path: .*}";

	@Reference
	private ProxyService _proxyService;

}