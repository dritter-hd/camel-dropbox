package org.apache.camel.dropbox.utils;

import org.apache.camel.dropbox.component.TestUtil;
import org.apache.camel.dropbox.utils.DropboxConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DropboxConfigurationTest {
    private DropboxConfiguration config;

    @Before
    public void setup() throws Exception {
        this.config = DropboxConfiguration.create(TestUtil.TEST_DATA_FOLDER, DropboxConfiguration.DEFAULT_RESOURCES);
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
        assertNotNull("Property file dropbox.properties has no " + DropboxConfiguration.APP_KEY + ".",
                this.config.getByKey(DropboxConfiguration.APP_KEY));
        assertNotNull("Property file dropbox.properties has no " + DropboxConfiguration.APP_SECRET + ".",
                this.config.getByKey(DropboxConfiguration.APP_SECRET));
        assertNotNull("Property file dropbox.properties has no " + DropboxConfiguration.TOKEN + ".",
                this.config.getByKey(DropboxConfiguration.TOKEN));
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
        final String key = "dummy";
        final String value = "myToken";

        this.config.store(key, value);
        assertEquals(value, this.config.getByKey(key));

        this.config.remove(key);
        assertNull(null, this.config.getByKey(key));
    }
}
