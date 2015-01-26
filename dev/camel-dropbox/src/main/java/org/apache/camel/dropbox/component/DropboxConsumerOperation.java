package org.apache.camel.dropbox.component;

import java.io.IOException;
import java.util.List;

import org.apache.camel.Exchange;

import com.dropbox.core.DbxException;

public interface DropboxConsumerOperation {
    public enum DropboxOperations {
        CONSUMER_GET_FOLDER, CONSUMER_GET, CONSUMER_SEARCH, PRODUCER_GET, PRODUCER_ADD;
    }
    
    List<Exchange> execute() throws DbxException, IOException;
}
