package org.apache.camel.dropbox.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.dropbox.component.DropboxConsumerOperation.DropboxOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxEntry.File;
import com.dropbox.core.DbxEntry.WithChildren;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;
import com.dropbox.core.util.Maybe;

public class DropboxOperationImpl {
    private Logger logger = LoggerFactory.getLogger(DropboxOperationImpl.class);

    private final Map<DropboxOperations, DropboxConsumerOperation> consumerOperation = new HashMap<DropboxOperations, DropboxConsumerOperation>();
    private final Map<DropboxOperations, DropboxProducerOperation> producerOperation = new HashMap<DropboxOperations, DropboxProducerOperation>();

    private DropboxEndpoint endpoint;
    private DbxClient client;

    private DropboxOperationImpl(final DropboxEndpoint endpoint, final DbxClient dbxClient) {
        this.endpoint = endpoint;
        this.client = dbxClient;

        producerOperation.put(DropboxOperations.PRODUCER_ADD, getProducerAdd());
        producerOperation.put(DropboxOperations.PRODUCER_GET, getProducerGet());

        consumerOperation.put(DropboxOperations.CONSUMER_GET, getConsumerGet());
        consumerOperation.put(DropboxOperations.CONSUMER_GET_FOLDER, getConsumerGetFolder());
        consumerOperation.put(DropboxOperations.CONSUMER_SEARCH, getConsumerSearch());
    }

    public static DropboxOperationImpl create(final DropboxEndpoint endpoint, final DbxClient dbxClient) {
        return new DropboxOperationImpl(endpoint, dbxClient);
    }

    public DropboxConsumerOperation getConsumerOperation(final DropboxOperations name) {
        return consumerOperation.get(name);
    }

    public DropboxProducerOperation getProducerOperation(final DropboxOperations name) {
        return producerOperation.get(name);
    }

    private DropboxConsumerOperation getConsumerGet() {
        return new DropboxConsumerOperation() {
            private String prevFolderHash = "1D";

            @Override
            public List<Exchange> execute() throws DbxException, IOException {
                final List<Exchange> exchanges = new ArrayList<Exchange>();

                final String path = endpoint.getPath();
                final Maybe<WithChildren> meta = client.getMetadataWithChildrenIfChanged(path, handleNull(prevFolderHash));
                if (meta.isNothing()) {
                    return Collections.<Exchange> emptyList();
                }
                prevFolderHash = meta.getJust().hash;
                
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                client.getFile(path, null, baos);

                final Exchange exchange = endpoint.createExchange();
                final String[] pathArray = path.split("/");
                exchange.getIn().setHeader(Exchange.FILE_NAME, pathArray[pathArray.length - 1]);
                exchange.getIn().setBody(baos);

                exchanges.add(exchange);
                return exchanges;
            }

            private String handleNull(final String prevFolderHash) {
                return prevFolderHash == null ? "1D" : prevFolderHash;
            }
        };
    }

    private DropboxConsumerOperation getConsumerGetFolder() {
        return new DropboxConsumerOperation() {
            private String prevFolderHash = "1D";

            @Override
            public List<Exchange> execute() throws DbxException, IOException {
                final List<Exchange> exchanges = new ArrayList<Exchange>();

                final String path = endpoint.getPath();

                final Maybe<WithChildren> meta = client.getMetadataWithChildrenIfChanged(path, prevFolderHash);

                if (meta.isNothing()) {
                    return Collections.<Exchange> emptyList();
                }
                prevFolderHash = meta.getJust().hash;
                final List<DbxEntry> children = meta.getJust().children;

                for (final DbxEntry dbxEntry : children) {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    client.getFile(dbxEntry.path, null, baos);

                    final Exchange exchange = endpoint.createExchange();
                    exchange.getIn().setHeader(Exchange.FILE_NAME, dbxEntry.name);
                    exchange.getIn().setBody(baos);

                    exchanges.add(exchange);
                }
                return exchanges;
            }
        };
    }

    private DropboxConsumerOperation getConsumerSearch() {
        return new DropboxConsumerOperation() {
            @Override
            public List<Exchange> execute() throws DbxException {
                final List<Exchange> exchanges = new ArrayList<Exchange>();

                final Exchange exchange = endpoint.createExchange();
                final List<DbxEntry> listOfFilesFolders = client.searchFileAndFolderNames(endpoint.getPath(), endpoint.getQuery());
                exchange.getIn().setHeader("searchResult", "List<DbxEntry>");
                exchange.getIn().setBody(listOfFilesFolders);

                exchanges.add(exchange);
                return exchanges;
            }
        };
    }

    private DropboxProducerOperation getProducerAdd() {
        return new DropboxProducerOperation() {

            @Override
            public void execute(Exchange exchange) throws DbxException, IOException {
                final String rawBody = exchange.getIn().getBody(String.class);

                if (rawBody != null) {
                    final String body = rawBody;
                    final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes());
                    exchange.getIn().setBody(upload(body.getBytes().length, bais, "fileFromString.txt"));
                } else {
                    final ByteArrayOutputStream baos = exchange.getIn().getBody(ByteArrayOutputStream.class);
                    final byte[] byteArray = baos.toByteArray();
                    final ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
                    final String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
                    exchange.getIn().setBody(upload(byteArray.length, bais, fileName));
                }
            }
        };
    }

    private DropboxProducerOperation getProducerGet() {
        return new DropboxProducerOperation() {
            @Override
            public void execute(final Exchange exchange) throws DbxException, IOException {
                final List<DbxEntry> body = (List<DbxEntry>) exchange.getIn().getBody();
                final DbxEntry dbxEntry = body.get(0);
                endpoint.setPath(dbxEntry.path);
                final String path = dbxEntry.path;

                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                client.getFile(path, null, baos);

                final String[] pathArray = path.split("/");
                exchange.getIn().setHeader(Exchange.FILE_NAME, pathArray[pathArray.length - 1]);
                exchange.getIn().setBody(baos);
            }
        };
    }

    public File upload(final long length, final InputStream is, final String fileName) throws DbxException, IOException {
        final String path = this.endpoint.getPath();
        return client.uploadFile(path + "/" + fileName, DbxWriteMode.add(), length, is);
    }
}
