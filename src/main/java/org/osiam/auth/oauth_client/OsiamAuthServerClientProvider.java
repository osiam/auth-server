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

package org.osiam.auth.oauth_client;

import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * ClientProvider which created the auth server client on startup
 *
 */
@Service
public class OsiamAuthServerClientProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsiamAuthServerClientProvider.class.getName());

    public static final String AUTH_SERVER_CLIENT_ID = "auth-server";
    public static final int CLIENT_VALIDITY = 10;

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private ClientRepository clientRepository;

    @Value("${org.osiam.auth-server.home}")
    private String authServerHome;

    private String authServerClientSecret;

    @PostConstruct
    private void createAuthServerClient() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(txManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                if (!clientRepository.existsById(AUTH_SERVER_CLIENT_ID)) {
                    LOGGER.info("No auth server client found, so it will be created.");
                    int validity = CLIENT_VALIDITY;

                    ClientEntity clientEntity = new ClientEntity();
                    Set<String> scopes = new HashSet<>();
                    scopes.add(Scope.ADMIN.toString());

                    Set<String> grants = new HashSet<>();
                    grants.add(GrantType.CLIENT_CREDENTIALS.toString());

                    clientEntity.setClientId(AUTH_SERVER_CLIENT_ID);
                    clientEntity.setRefreshTokenValiditySeconds(validity);
                    clientEntity.setAccessTokenValiditySeconds(validity);
                    clientEntity.setRedirectUri(authServerHome);
                    clientEntity.setScope(scopes);
                    clientEntity.setImplicit(true);
                    clientEntity.setValidityInSeconds(validity);
                    clientEntity.setGrants(grants);

                    clientEntity = clientRepository.save(clientEntity);
                    authServerClientSecret = clientEntity.getClientSecret();
                }
            }
        });
    }

    public String getClientSecret() {
        return authServerClientSecret;
    }
}
