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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.camel.dropbox.DropboxApp;
import org.apache.camel.dropbox.DropboxAppConfiguration;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxEntry.File;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

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

        this.setupDropboxClient();
    }

    public void process(final Exchange exchange) throws Exception {
        if ("add".equals(this.endpoint.getMethod())) {
            final ByteArrayOutputStream baos = exchange.getIn().getBody(ByteArrayOutputStream.class);
            final byte[] byteArray = baos.toByteArray();
            final ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            final String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
            exchange.getIn().setBody(this.upload(byteArray.length, bais, fileName));
        } else if ("get".equals(this.endpoint.getMethod())) {
            final String searchHeader = exchange.getIn().getHeader("searchResult", String.class);
            
            if (null == searchHeader) {
                throw new IllegalArgumentException();
            }
            
            final List<DbxEntry> body = (List<DbxEntry>) exchange.getIn().getBody();
            final DbxEntry dbxEntry = body.get(0);
            this.endpoint.setPath(dbxEntry.path);
            final String path = dbxEntry.path;
            
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            client.getFile(path, null, baos);

            final String[] pathArray = path.split("/");
            exchange.getIn().setHeader(Exchange.FILE_NAME, pathArray[pathArray.length - 1]);
            exchange.getIn().setBody(baos);
        } else {
            throw new UnsupportedOperationException("Method " + this.endpoint.getMethod() + " unknown.");
        }
    }

    private void setupDropboxClient() {
        final DropboxAppConfiguration appConfig = new DropboxAppConfiguration(this.endpoint.getAppKey(), this.endpoint.getAppSecret(),
                this.endpoint.getAccessToken());
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

    public File upload(final long length, final InputStream is, final String fileName) throws DbxException, IOException {
        final String path = this.endpoint.getPath();
        return client.uploadFile(path + "/" + fileName, DbxWriteMode.add(), length, is);
    }
}
