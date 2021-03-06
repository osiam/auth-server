/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.auth.login;

import org.osiam.auth.oauth_client.OsiamAuthServerClientProvider;
import org.osiam.auth.token.OsiamAccessTokenProvider;
import org.osiam.client.OsiamConnector;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResourceServerConnector implements InitializingBean{

    @Value("${org.osiam.resource-server.home}")
    private String resourceServerHome;

    @Value("${org.osiam.auth-server.home}")
    private String authServerHome;

    @Value("${org.osiam.resource-server.connector.max-connections:40}")
    private int maxConnections;

    @Value("${org.osiam.resource-server.connector.read-timeout-ms:10000}")
    private int readTimeout;

    @Value("${org.osiam.resource-server.connector.connect-timeout-ms:5000}")
    private int connectTimeout;

    @Autowired
    private OsiamAccessTokenProvider osiamAccessTokenProvider;

    @Autowired
    private OsiamAuthServerClientProvider authServerClientProvider;

    @Override
    public void afterPropertiesSet() throws Exception {
        OsiamConnector.setMaxConnections(maxConnections);
        OsiamConnector.setMaxConnectionsPerRoute(maxConnections);
    }

    public User getUserByUsername(final String userName) {
        Query query = new QueryBuilder().filter("userName eq \"" + userName + "\"").build();

        SCIMSearchResult<User> result = createOsiamConnector().searchUsers(query,
                osiamAccessTokenProvider.createAccessToken());

        if (result.getTotalResults() != 1) {
            return null;
        } else {
            return result.getResources().get(0);
        }
    }

    public User getUserById(final String id) {
        return createOsiamConnector().getUser(id, osiamAccessTokenProvider.createAccessToken());
    }

    public User createUser(User user) {
        return createOsiamConnector().createUser(user, osiamAccessTokenProvider.createAccessToken());
    }

    public User updateUser(String userId, UpdateUser user) {
        return createOsiamConnector().updateUser(userId, user, osiamAccessTokenProvider.createAccessToken());
    }

    public User searchUserByUserNameAndPassword(String userName, String hashedPassword) {
        Query query = new QueryBuilder().filter("userName eq \"" + userName + "\""
                + " and password eq \"" + hashedPassword + "\"").build();

        SCIMSearchResult<User> result = createOsiamConnector().searchUsers(
                query, osiamAccessTokenProvider.createAccessToken()
        );

        if (result.getTotalResults() != 1) {
            return null;
        } else {
            return result.getResources().get(0);
        }
    }

    private OsiamConnector createOsiamConnector() {
        return new OsiamConnector.Builder()
                .setAuthServerEndpoint(authServerHome)
                .setResourceServerEndpoint(resourceServerHome)
                .setClientId(OsiamAuthServerClientProvider.AUTH_SERVER_CLIENT_ID)
                .setClientSecret(authServerClientProvider.getClientSecret())
                .withReadTimeout(readTimeout)
                .withConnectTimeout(connectTimeout)
                .build();
    }
}
