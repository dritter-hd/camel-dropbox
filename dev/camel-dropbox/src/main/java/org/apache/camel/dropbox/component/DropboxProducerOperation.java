package org.apache.camel.dropbox.component;

import java.io.IOException;

import org.apache.camel.Exchange;

import com.dropbox.core.DbxException;

public interface DropboxProducerOperation {
    public enum DropboxOperations {
        PRODUCER_GET, PRODUCER_ADD;
    }
    
    void execute(Exchange exchange) throws DbxException, IOException;
}
