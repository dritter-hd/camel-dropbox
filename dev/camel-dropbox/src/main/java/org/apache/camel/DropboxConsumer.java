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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.camel.dropbox.DropboxApp;
import org.apache.camel.dropbox.DropboxAppConfiguration;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;

/**
 * The dropbox.com consumer.
 */
public class DropboxConsumer extends ScheduledPollConsumer {
    private Logger logger = LoggerFactory.getLogger(DropboxConsumer.class);

    private final DropboxEndpoint endpoint;

    private DbxClient client;

    public DropboxConsumer(final DropboxEndpoint endpoint, final Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;

        this.setupDropboxClient();
    }

    private void setupDropboxClient() {
        final DropboxAppConfiguration appConfig = new DropboxAppConfiguration(this.endpoint.getAppKey(), this.endpoint.getAppSecret(),
                this.endpoint.getAccessToken());
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

        if ("get".equals(this.endpoint.getMethod())) {
            final String path = this.endpoint.getPath();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            client.getFile(path, null, baos);

            final String[] pathArray = path.split("/");
            exchange.getIn().setHeader(Exchange.FILE_NAME, pathArray[pathArray.length - 1]);
            exchange.getIn().setBody(baos);
        } else if ("search".equals(this.endpoint.getMethod())) {
            final List<DbxEntry> listOfFilesFolders = client.searchFileAndFolderNames(this.endpoint.getPath(), this.endpoint.getQuery());
            exchange.getIn().setHeader("searchResult", "List<DbxEntry>");
            exchange.getIn().setBody(listOfFilesFolders);
        } else {
            throw new UnsupportedOperationException("Method " + this.endpoint.getMethod() + " unknown.");
        }

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
