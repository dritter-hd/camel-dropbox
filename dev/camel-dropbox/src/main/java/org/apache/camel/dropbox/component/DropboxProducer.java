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
package org.apache.camel.dropbox.component;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import org.apache.camel.Exchange;
import org.apache.camel.dropbox.component.DropboxConsumerOperation.DropboxOperations;
import org.apache.camel.dropbox.utils.DropboxApp;
import org.apache.camel.dropbox.utils.DropboxAppConfiguration;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The www.dropbox.com producer.
 */
public class DropboxProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(DropboxProducer.class);
    private DropboxEndpoint endpoint;
    private DbxClient client;

    public DropboxProducer(DropboxEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;

    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        this.setupDropboxClient();
    }

    public void process(final Exchange exchange) throws Exception {
        final String method = this.endpoint.getMethod();

        final DropboxOperationImpl operation = DropboxOperationImpl.create(this.endpoint, this.client);
        final DropboxProducerOperation producerOperation = operation.getProducerOperation(DropboxOperations
                .valueOf(("producer" + "_" + method).toUpperCase()));
        if (null == producerOperation) {
            throw new UnsupportedOperationException("Producer operation " + method + " not supported.");
        }
        producerOperation.execute(exchange);
    }

    private void setupDropboxClient() {
        final DropboxAppConfiguration appConfig = new DropboxAppConfiguration(this.endpoint.getAppKey(), this.endpoint.getAppSecret(),
                this.endpoint.getAccessToken(), this.endpoint.getProxyHost(), this.endpoint.getProxyPort());
        final DropboxApp app = DropboxApp.create(appConfig);
        try {
            client = app.connect();
            LOG.debug("Client connected: " + client.getAccountInfo().displayName);
        } catch (final FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (final IOException e) {
            throw new IllegalArgumentException(e);
        } catch (final DbxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
