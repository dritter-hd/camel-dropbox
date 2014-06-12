package org.apache.camel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.DropboxOperation.DropboxOperations;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;
import com.dropbox.core.DbxEntry.File;

public class DropboxOperationImpl {
    private static final Map<DropboxOperations, DropboxOperation> operations = new HashMap<DropboxOperations, DropboxOperation>();

    private DropboxEndpoint endpoint;
    private DbxClient client;

    private DropboxOperationImpl(final DropboxEndpoint endpoint, final DbxClient dbxClient) {
        this.endpoint = endpoint;
        this.client = dbxClient;

        operations.put(DropboxOperations.PRODUCER_ADD, getProducerAdd());
        operations.put(DropboxOperations.PRODUCER_GET, getProducerGet());

        operations.put(DropboxOperations.CONSUMER_GET, getConsumerGet());
        operations.put(DropboxOperations.CONSUMER_SEARCH, getConsumerSearch());
    }

    public static DropboxOperationImpl create(final DropboxEndpoint endpoint, final DbxClient dbxClient) {
        return new DropboxOperationImpl(endpoint, dbxClient);
    }

    public DropboxOperation getOperation(final DropboxOperations name) {
        return operations.get(name);
    }

    private DropboxOperation getConsumerGet() {
        return new DropboxOperation() {
            @Override
            public void execute(final Exchange exchange) throws DbxException, IOException {
                final String path = endpoint.getPath();
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                client.getFile(path, null, baos);

                final String[] pathArray = path.split("/");
                exchange.getIn().setHeader(Exchange.FILE_NAME, pathArray[pathArray.length - 1]);
                exchange.getIn().setBody(baos);
            }
        };
    }

    private DropboxOperation getConsumerSearch() {
        return new DropboxOperation() {
            @Override
            public void execute(final Exchange exchange) throws DbxException {
                final List<DbxEntry> listOfFilesFolders = client.searchFileAndFolderNames(endpoint.getPath(), endpoint.getQuery());
                exchange.getIn().setHeader("searchResult", "List<DbxEntry>");
                exchange.getIn().setBody(listOfFilesFolders);
            }
        };
    }

    private DropboxOperation getProducerAdd() {
        return new DropboxOperation() {
            @Override
            public void execute(final Exchange exchange) throws DbxException, IOException {
                final ByteArrayOutputStream baos = exchange.getIn().getBody(ByteArrayOutputStream.class);
                final byte[] byteArray = baos.toByteArray();
                final ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
                final String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
                exchange.getIn().setBody(upload(byteArray.length, bais, fileName));
            }
        };
    }

    private DropboxOperation getProducerGet() {
        return new DropboxOperation() {

            @Override
            public void execute(final Exchange exchange) throws DbxException, IOException {
                final String searchHeader = exchange.getIn().getHeader("searchResult", String.class);

                if (null == searchHeader) {
                    throw new IllegalArgumentException();
                }

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
