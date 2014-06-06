package org.apache.camel.dropbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class DropboxConfiguration {

    public static final String SRC_TEST_RESOURCES = "src/test/resources/";
    public static final String SRC_MAIN_RESOURCES = "src/main/resources/";
    public static final String DEFAULT_RESOURCES = "dropbox.properties";

    public static final String APP_SECRET = "app_secret";
    public static final String APP_KEY = "app_key";

    public static final String TOKEN = "token";

    private Logger logger = LoggerFactory.getLogger(DropboxConfiguration.class);

    private Properties properties;
    private String path;
    private String propertyName;

    private DropboxConfiguration(final String path, final String propertyName) {
        this.path = path;
        this.propertyName = propertyName;
        this.logger.debug("Path= " + path + ", property=" + propertyName);

        this.properties = new Properties();
        this.loadProperties(path, propertyName);
        this.logger.debug("Properties loaded.");
    }

    public static DropboxConfiguration create(final String path, final String propertyName) {
        return new DropboxConfiguration(path, propertyName);
    }

    public void store(final String key, final String value) throws FileNotFoundException, IOException {
        if (null == key || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        properties.put(key, value);
        properties.store(new FileOutputStream(path + propertyName), null);
        this.logger.debug("Key=" + key + ", value=" + value + " stored.");
    }

    public String getByKey(final String key) throws FileNotFoundException, IOException {
        if (null == key || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        return properties.getProperty(key);
    }

    public void remove(final String key) {
        this.properties.remove(key);
        this.updateProperties();
    }

    private void loadProperties(final String path, final String propertyName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path + propertyName);
            properties.load(fis);
        } catch (final FileNotFoundException e) {
            throw new IllegalStateException("Path= " + path + ", property=" + propertyName + " not found.", e);
        } catch (final IOException e) {
            throw new IllegalStateException("Path= " + path + ", property=" + propertyName + " not found.", e);
        } finally {
            try {
                fis.close();
            } catch (final IOException e) {
                logger.warn("File Input Stream could not be closed.", e);
            }
        }
    }

    private void updateProperties() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path + propertyName);
            this.properties.store(fos, null);
        } catch (final FileNotFoundException e) {
            throw new IllegalStateException("Path= " + path + ", property=" + propertyName + " not found.", e);
        } catch (final IOException e) {
            throw new IllegalStateException("Path= " + path + ", property=" + propertyName + " not found.", e);
        } finally {
            try {
                fos.close();
            } catch (final IOException e) {
                logger.warn("File Output Stream could not be closed.", e);
            }
        }
    }
}
