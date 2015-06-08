package org.apache.camel.dropbox.registry;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dropbox.component.TestUtil;
import org.apache.camel.dropbox.utils.DropboxConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

public class CamelRegistrySupportTest extends CamelRegistrySupport {

    private String appKey;
    private String appSecret;
    private String accessToken;
    
    @Override
    protected CamelContext createCamelContext() throws Exception {
        final CamelContext context = super.createCamelContext();
        CamelRegistrySupport.bindCamelRegistryToContext(context);
        return context;
    }
    
    @Ignore
    @Test
    public void testDropbox() throws Exception {
        final MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();

        assertDataSourceRegistryBound();
    }

    private void assertDataSourceRegistryBound() {
        final DataSource registry = context.getRegistry().lookupByNameAndType("datasource", DataSource.class);
        Assert.assertNotNull(registry);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        this.setupConfiguration();
        Assume.assumeNotNull(this.accessToken);

        return new RouteBuilder() {
            public void configure() {
                from(
                        "dropbox://get?path=" + "/Public/ioio.txt" + "&appKey=" + appKey + "&appSecret=" + appSecret + "&accessToken="
                                + accessToken).to(
                        "dropbox://add?path=" + "/Public/SubPublic" + "&appKey=" + appKey + "&appSecret=" + appSecret + "&accessToken="
                                + accessToken).to("mock:result");
            }
        };
    }

    private void setupConfiguration() throws FileNotFoundException, IOException {
        final DropboxConfiguration configuration = DropboxConfiguration.create(TestUtil.TEST_DATA_FOLDER,
                DropboxConfiguration.DEFAULT_RESOURCES);
        appKey = configuration.getByKey(DropboxConfiguration.APP_KEY);
        appSecret = configuration.getByKey(DropboxConfiguration.APP_SECRET);
        accessToken = configuration.getByKey(DropboxConfiguration.TOKEN);
    }
}
