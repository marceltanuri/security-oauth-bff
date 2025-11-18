# -security-oauth-bff

## Installation

You need to install the following JARs in your OSGi container:

-   [security-oauth-bff-osgi-bundle](https.repo1.maven.org/maven2/io/github/marceltanuri/security-oauth-bff-osgi-bundle/)
-   [security-oauth-commons-osgi-bundle](https.repo1.maven.org/maven2/io/github/marceltanuri/security-oauth-commons-osgi-bundle/)

## Configuration

### Configure an OAuth2 Client

Create a `.config` file in your OSGi container's configuration directory (e.g., `etc/` or `load/`).

The filename should be `io.github.marceltanuri.security.oauth.client.OAuthClientSettings-<client-name>.config`.

**Configuration Example (`io.github.marceltanuri.security.oauth.client.OAuthClientSettings-myapi.config`):**

```properties
# Unique name for this client configuration. This is the only required property in this file.
clientName="myapi"
```

**Security Note:** For enhanced security, it is strongly recommended to configure all sensitive values using environment variables instead of placing them directly in the configuration file.

The application will automatically look for environment variables to override the settings from the `.config` file.

### Environment Variable Overrides

If an environment variable is set, its value will be used, overriding any value present in the `.config` file. You can leave the properties empty or omit them from the file when using an environment variable.

The environment variable names are constructed using the following pattern:

`[PREFIX]_[NORMALIZED_CLIENT_NAME]_[PROPERTY_NAME]`

Where:
- `[PREFIX]` is one of the supported prefixes.
- `[NORMALIZED_CLIENT_NAME]` is the `clientName` from your configuration file, converted to uppercase, with any non-alphanumeric characters replaced by underscores.
- `[PROPERTY_NAME]` is the name of the configuration property in uppercase.

#### Configurable Properties

The following properties can be configured via environment variables:

| Property Name | Environment Variable Suffix | Description |
| ------------------ | --------------------------- | ------------------------------------------------ |
| `audience` | `AUDIENCE` | The audience for the OAuth2 token. |
| `clientId` | `CLIENT_ID` | The client ID for the OAuth2 application. |
| `clientSecret` | `CLIENT_SECRET` | The client secret for the OAuth2 application. |
| `scope` | `SCOPE` | The scope(s) to request for the OAuth2 token. |
| `serviceBaseUrl` | `SERVICE_BASE_URL` | The base URL of the service provider. |
| `tokenEndpoint` | `TOKEN_ENDPOINT` | The token endpoint URL for the OAuth2 provider. |

#### Environment Variable Prefixes

The following prefixes are supported for backward compatibility:

- `IO_GITHUB_MARCELTANURI_SECURITY_OAUTH_CLIENT`
- `OAUTH2_CLIENTS`

#### Example

For a client with `clientName="myapi"`, the environment variable for the `tokenEndpoint` should the following:

- `IO_GITHUB_MARCELTANURI_SECURITY_OAUTH_CLIENT_MYAPI_TOKEN_ENDPOINT`

## Liferay Configuration

To allow the proxy resource to be accessed, you need to add the following to your Liferay access policy:

```
io.github.marceltanuri.security.oauth.bff.proxy.ProxyResource#*
```