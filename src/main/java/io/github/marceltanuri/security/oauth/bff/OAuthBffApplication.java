package io.github.marceltanuri.security.oauth.bff;

import io.github.marceltanuri.security.oauth.bff.proxy.ProxyResource;
import io.github.marceltanuri.security.oauth.bff.token.TokenResource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;

/**
 * Main entry point for the JAX-RS application. This class is responsible for
 * configuring and bootstrapping the OAuth BFF (Backend for Frontend) services.
 *
 * @author Marcel Tanuri
 */
@ApplicationPath("/oauth-bff")
@Component(
	property = {
		"osgi.jaxrs.name=OAuthBff.Rest",
		"osgi.jaxrs.application.base=/oauth-bff"
	},
	service = Application.class
)
public class OAuthBffApplication extends Application {

	/**
	 * Returns the set of resource classes for the application. This includes
	 * the {@link ProxyResource} and {@link TokenResource} classes.
	 *
	 * @return A {@link Set} of resource classes.
	 */
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();

		classes.add(ProxyResource.class);
		classes.add(TokenResource.class);

		return classes;
	}

}