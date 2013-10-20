package org.apache.camel.dropbox;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DropboxConfigurationTest {
    private DropboxConfiguration config;

    @Before
    public void setup() throws Exception {
        this.config = DropboxConfiguration.create(DropboxConfiguration.SRC_TEST_RESOURCES, DropboxConfiguration.DEFAULT_RESOURCES);
        assertNotNull(this.config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByKey_keyNull() throws Exception {
        this.config.getByKey(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetKey_keyEmpty() throws Exception {
        this.config.getByKey("");
    }

    @Test
    public void testGetKey_notNull() throws Exception {
        assertNotNull(this.config.getByKey(DropboxConfiguration.APP_KEY));
        assertNotNull(this.config.getByKey(DropboxConfiguration.APP_SECRET));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStore_null() throws Exception {
        this.config.store(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStore_empty() throws Exception {
        this.config.store("", null);
    }

    @Test
    public void testStoreAndRemove_notNull() throws Exception {
        final String key = "token";
        final String value = "myToken";

        this.config.store(key, value);
        assertEquals(value, this.config.getByKey(key));

        this.config.remove(key);
        assertNull(null, this.config.getByKey(key));
    }
}
