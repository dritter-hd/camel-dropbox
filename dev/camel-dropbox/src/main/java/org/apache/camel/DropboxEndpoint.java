/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.camel.dropbox.DropboxApp;
import org.apache.camel.dropbox.DropboxAppConfiguration;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;

/**
 * Represents a www.dropbox.com endpoint.
 */
public class DropboxEndpoint extends DefaultEndpoint {
    private DropboxComponent component;

    private transient Logger logger = LoggerFactory.getLogger(DropboxEndpoint.class);

    @UriParam
    private String path;
    @UriParam
    private String appKey;
    @UriParam
    private String appSecret;
    @UriParam
    private String accessToken;

    @UriParam
    private String method;
    @UriParam
    private String query;

    @UriParam
    private ProxyConfiguration proxyConfig = new ProxyConfiguration();
    
    private DbxClient client;


    public DropboxEndpoint() {
    }

    public DropboxEndpoint(final String endpointUri, final DropboxComponent component) throws URISyntaxException {
        super(endpointUri, component);
        this.component = component;
    }

    public DropboxEndpoint(final String endpointUri) {
        super(endpointUri);
    }

    public DropboxEndpoint(final String uri, final String remaining, final DropboxComponent dropboxComponent) throws URISyntaxException {
        this(uri, dropboxComponent);
        this.setMethod(remaining);
    }

    public Producer createProducer() throws Exception {
        return new DropboxProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new DropboxConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    protected DbxClient getDropboxClient() {
        if (null != client) {
            final DropboxAppConfiguration appConfig = new DropboxAppConfiguration(this.getAppKey(), this.getAppSecret(),
                    this.getAccessToken(), this.getProxyHost(), this.getProxyPort());
            final DropboxApp app = DropboxApp.create(appConfig);
            try {
                client = app.connect();
                logger.debug("Client connected: " + client.getAccountInfo().displayName);
            } catch (final FileNotFoundException e) {
                throw new IllegalArgumentException(e);
            } catch (final IOException e) {
                throw new IllegalArgumentException(e);
            } catch (final DbxException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return client;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getProxyPort() {
        return proxyConfig.getProxyPort();
    }

    public void setProxyPort(int proxyPort) {
        proxyConfig.setProxyPort(proxyPort);
    }

    public String getProxyHost() {
        return proxyConfig.getProxyHost();
    }

    public void setProxyHost(String proxyHost) {
        proxyConfig.setProxyHost(proxyHost);
    }
}
