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

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import org.apache.camel.DropboxOperationImpl.DropboxOperations;
import org.apache.camel.dropbox.DropboxApp;
import org.apache.camel.dropbox.DropboxAppConfiguration;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The dropbox.com consumer.
 */
public class DropboxConsumer extends ScheduledPollConsumer {
    private Logger logger = LoggerFactory.getLogger(DropboxConsumer.class);

    private final DropboxEndpoint endpoint;

    private DbxClient client;

    private DropboxOperationImpl operation;

    public DropboxConsumer(final DropboxEndpoint endpoint, final Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;

        this.setupDropboxClient();
        
        this.operation = DropboxOperationImpl.create(this.endpoint, this.client);
    }

    private void setupDropboxClient() {
        final DropboxAppConfiguration appConfig = new DropboxAppConfiguration(this.endpoint.getAppKey(), this.endpoint.getAppSecret(),
                this.endpoint.getAccessToken(), this.endpoint.getProxyHost(), this.endpoint.getProxyPort());
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

    @Override
    protected int poll() throws Exception {
        final Exchange exchange = endpoint.createExchange();

        final String method = this.endpoint.getMethod();
        final DropboxOperations operationName = DropboxOperations.valueOf(("consumer" + "_" + method).toUpperCase());
        
        logger.debug("get operation for " + operationName);
        final DropboxOperation producerOperation = operation.getOperation(operationName);
        if (null == producerOperation) {
            throw new UnsupportedOperationException("Producer operation " + method + " not supported.");
        }
        producerOperation.execute(exchange);
        
        try {
            // send message to next processor in the route
            getProcessor().process(exchange);
            return 1; // number of messages polled
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }
}
