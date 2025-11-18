package io.github.marceltanuri.security.oauth.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * An OSGi component that acts as a centralized factory for accessing configured
 * {@link OAuthClient} services. This factory tracks all {@link OAuthClient}
 * instances registered in the OSGi service registry and makes them available
 * for retrieval by their unique client name.
 *
 * @author Marcel Tanuri
 */
@Component(service = OAuthServiceFactory.class)
public class OAuthServiceFactory {

	/**
	 * Retrieves a registered {@link OAuthClient} service by its client name.
	 *
	 * @param clientName The unique name of the client to retrieve.
	 * @return The {@link OAuthClient} instance, or {@code null} if no client is
	 *         found with the specified name.
	 */
	public OAuthClient getOAuthClient(String clientName) {
		return _clients.get(clientName);
	}

	/**
	 * A dynamic OSGi lifecycle method called when a new {@link OAuthClient}
	 * service is registered. It adds the service to the internal map, using the
	 * "clientName" property as the key.
	 *
	 * @param service    The {@link OAuthClient} service being registered.
	 * @param properties The service's registration properties.
	 */
	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, target = "(clientName=*)"
	)
	protected void addOAuthService(
		OAuthClient service, Map<String, Object> properties) {

		String clientName = (String)properties.get("clientName");

		_clients.put(clientName, service);
	}

	/**
	 * A dynamic OSGi lifecycle method called when an {@link OAuthClient} service
	 * is unregistered. It removes the service from the internal map.
	 *
	 * @param service    The {@link OAuthClient} service being unregistered.
	 * @param properties The service's registration properties.
	 */
	protected void removeOAuthService(
		OAuthClient service, Map<String, Object> properties) {

		String clientName = (String)properties.get("clientName");

		_clients.remove(clientName, service);
	}

	private final Map<String, OAuthClient> _clients = new ConcurrentHashMap<>();

}