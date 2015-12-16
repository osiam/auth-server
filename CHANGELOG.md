# OSIAM auth server

## 2.5 - 2015-12-15

### Features

- Use JDBC connection pooling

    By default the pool has a size of 10 and a timeout of 30s to acquire a connection.
    These settings can be changed with the following configuration properties:

    - `org.osiam.auth-server.db.maximum-pool-size`
    - `org.osiam.auth-server.db.connection-timeout-ms`

- Support retrieving list of clients

    Use the resource endpoint `/Client` with `GET`.

- Make number of parallel connections to the auth-server configurable

    The default is 40 and can be changed with the following configuration property:

    - `org.osiam.resource-server.connector.max-connections`

- Make timeouts of connections to auth-server configurable

    By default the read timeout is set to 10000ms and the connect timeout to 5000ms.
    These settings can be changed with the following configuration properties:

    - `org.osiam.resource-server.connector.read-timeout-ms`
    - `org.osiam.resource-server.connector.connect-timeout-ms`

### Changes

- Add Flyway migration to replace method-based scopes

    The migration removes all method-based scopes from the auth-server client and adds the scope `ADMIN`.

- Increase default timeouts for connections to resource-server

    By default the read timeout is set to 10000ms and the connect timeout to 5000ms.

- Increase default maximum number of parallel connections to resource-server

    The default is 40.

- Switch to Spring Boot

- Refactor database schema

    **Note:** Some fields in table `osiam_client` have been renamed:

    - `accesstokenvalidityseconds` becomes `access_token_validity_seconds`
    - `refreshtokenvalidityseconds` becomes `refresh_token_validity_seconds`
    - `validityinseconds` becomes `validity_in_seconds`

    Update your SQL scripts, if you add OAuth 2 clients via direct database manipulation.
    It's recommended to use the RESTful endpoints under `/Client` to manage Clients.

### Fixes

- Make sure `access_token`, `refresh_token` and `token_type` are added only
  once to the returned Access Token (Fixes [#42](https://github.com/osiam/auth-server/issues/42)).

- Remove `scopes` from the Access Token (Fixes [#51](https://github.com/osiam/auth-server/issues/51)).

- Prevent NPE when `User#active` is null

- Handle duplicate client creation error on application level

    Respond with Conflict 409 when a client with a requested client id already
    exists

### Updates

- OSIAM connector4java 1.8
- MySQL JDBC driver 5.1.37
- PostgreSQL JDBC driver 9.4-1205
- OAuth2 for Spring Security 2.0.8

## 2.4

Skipped to synchronize OSIAM main version with versions of the core servers

## 2.3 - 2015-10-09

Revoked, see 2.5

## 2.2 - 2015-06-18

### Changes

- Bump connector to make use of more concurrent HTTP connections

## 2.1.2 - 2015-06-02

### Fixes

- Revert 'Change OAuth JSON error to comply to spec'

    "old style" error messages are back:
    ```json
    {
      "error_code": "...",
      "description": "..."
    }
    ```

## 2.1.1 - 2015-06-02

### Other

- Append classifier to distribution artifact

## 2.1 - 2015-06-02

### Features

- Support for new `ME` scope
- Support for new `ADMIN` scope
- Remember and check timestamp of approval per client in web session

### Changes

- Remove field `expiry` from OAuth clients
- Bump dependencies

### Fixes

- Revert change that disabled single sign-on
- Secure the '/token/revocation' endpoints
- Change OAuth JSON error to comply to spec

    Was:
    ```json
    {
      "error_code": "...",
      "description": "..."
    }
    ```

    Changed to:
    ```json
    {
      "error": "...",
      "description": "..."
    }
    ```

### Other

- Auth-server now lives in its own Git repo
- Changed artifact id from `osiam-auth-server` to `auth-server`

## 2.0 - 2015-04-29

**Breaking changes!**

This release introduces breaking changes, due to the introduction of automatic
database schema updates powered by Flyway. See the
[migration notes](docs/Migration.md#from-13x-to-20) for further details.

- [feature] Support automatic database migrations
- [feature] create JAR containing the classes of app
- [fix] replace Windows line endings with Unix ones in SQL scripts
- [change] decrease default verbosity
- [change] bump dependency versions
- [docs] move documentation from Wiki to repo
- [docs] rename file RELEASE.NOTES to CHANGELOG.md

## 1.3.2 - 2014-11-24
- release because of fixes in addon-administration

## 1.3.1 - 2014-10-27
- release because of fixes in addon-self-administration

## 1.3 - 2014-10-17
- [fix] Umlauts encoding problems
- [fix] Wrong directory name for translations

  For a detailed description and migration see:
  https://github.com/osiam/server/wiki/Migration#from-12-to-13

## 1.2 - 2014-09-30
- release because of fixes in addon-self-administration

## 1.1 - 2014-09-19
- [feature] support for mysql as database
- [feature] prevent users from login after N failed attempts
- [feature] revocation of access tokens
  It is now possible to revoke access tokens by using the following service
  endpoints:
  * auth-server/token/revocation
    For revocation of the access token sent as bearer token in the
    Authorization header
  * auth-server/token/revocation/<uuid of user>
    For revocation of all access tokens that were issued to or in behalf of a
    given user. This endpoint is protected.
- [feature] revoke all access tokens of a deactivated/deleted user
- [enhancement] Force UTF-8 encoding of requests and responses
- [enhancement] better error message on search
  When searching for resources and forgetting the surrounding double quotes for
  values, a non-understandable error message was responded. the error message
  was changed to explicitly tell that the error occurred due to missing
  double quotes.
- [enhancement] updated dependencies: Spring 4.1.0, Spring Security 3.2.5,
  Spring Metrics 3.0.2, Jackson 2.4.2, Hibernate 4.3.6, AspectJ 1.8.2,
  Joda Time 2.4, Joda Convert 1.7, Apache Commons Logging 1.2, Guava 18.0,
  Postgres JDBC Driver 9.3-1102-jdbc41
