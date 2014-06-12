package org.apache.camel;

import java.io.IOException;

import com.dropbox.core.DbxException;

public interface DropboxOperation {
    public enum DropboxOperations {
        CONSUMER_GET, CONSUMER_SEARCH, PRODUCER_GET, PRODUCER_ADD;
    }
    
    void execute(Exchange exchange) throws DbxException, IOException;
}
