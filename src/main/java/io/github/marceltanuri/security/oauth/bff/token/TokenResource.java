package io.github.marceltanuri.security.oauth.bff.token;

import io.github.marceltanuri.security.oauth.client.OAuthClient;
import io.github.marceltanuri.security.oauth.client.OAuthServiceFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


/**
 * JAX-RS resource class that exposes endpoints for retrieving OAuth 2.0 access
 * tokens and checking the service's health. It uses an
 * {@link OAuthServiceFactory} to obtain the correct {@link OAuthClient}
 * configuration and fetch the token.
 *
 * @author Marcel Tanuri
 */
@Component(
	immediate = true,
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=OAuthBff.Rest)",
		"osgi.jaxrs.resource=true"
	},
	service = TokenResource.class
)
public class TokenResource {

	/**
	 * Retrieves an OAuth 2.0 access token for a specified client.
	 *
	 * @param clientName The name of the client configuration to use, passed as a
	 *                   path parameter.
	 * @return A {@link Response} containing the access token as plain text with a
	 *         200 (OK) status. If the client configuration is not found, it
	 *         returns a 404 (Not Found) response. If an error occurs during
	 *         token retrieval, it returns a 500 (Internal Server Error)
	 *         response with an error message.
	 */
	@GET
	@Path("/{clientName}/token")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAccessToken(@PathParam("clientName") String clientName) {
		OAuthClient oAuthService = _oAuthServiceFactory.getOAuthClient(
			clientName);

		if (oAuthService == null) {
			return Response.status(
				Response.Status.NOT_FOUND
			).entity(
				"OAuth client configuration not found for: " + clientName
			).build();
		}

		try {
			return Response.ok(
				oAuthService.getAccessToken()
			).build();
		}
		catch (RuntimeException runtimeException) {
			_log.error(runtimeException);
			return Response.status(
				Response.Status.INTERNAL_SERVER_ERROR
			).entity(
				"Error retrieving token for client " + clientName + ": " +
					runtimeException.getMessage()
			).build();
		}
	}

	/**
	 * A simple health check endpoint to verify if the service is running.
	 *
	 * @return A {@link Response} with the string "Ready" and a 200 (OK) status.
	 */
	@GET
	@Path("/ready")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getReady() {
		return Response.ok(
			"Ready"
		).build();
	}

	@Reference
	private OAuthServiceFactory _oAuthServiceFactory;

	private final static Log _log = LogFactoryUtil.getLog(
		TokenResource.class);

}