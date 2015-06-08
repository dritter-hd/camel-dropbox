package org.apache.camel.dropbox.registry;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.derby.jdbc.EmbeddedDataSource;

public class CamelRegistrySupport extends CamelTestSupport {
    private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static DataSource ds;

    public static void bindCamelRegistryToContext(final CamelContext context) {
        if (ds == null) {
            try {
                Class.forName(driver);
            } catch (final ClassNotFoundException e) {
                throw new IllegalStateException("Failed to register driver.", e);
            }
            ds = createDataSource();
        }
        ((JndiRegistry) ((PropertyPlaceholderDelegateRegistry) context.getRegistry()).getRegistry()).bind(
                CamelRegistrySupportApi.DATASOURCE, ds);
    }

    private static DataSource createDataSource() {
        final DataSource ds = new EmbeddedDataSource();
        ((EmbeddedDataSource) ds).setUser("");
        ((EmbeddedDataSource) ds).setPassword("");
        ((EmbeddedDataSource) ds).setDatabaseName("data/testdb");
        ((EmbeddedDataSource) ds).setCreateDatabase("create");
        return ds;
    }
}
