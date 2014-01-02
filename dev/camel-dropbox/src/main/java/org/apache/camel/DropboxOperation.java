package org.apache.camel;

import java.io.IOException;

import com.dropbox.core.DbxException;

public interface DropboxOperation {
    void execute(Exchange exchange) throws DbxException, IOException;
}
