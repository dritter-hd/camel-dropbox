package org.apache.camel.dropbox.registry;

import org.apache.camel.CamelContext;
import org.apache.camel.dropbox.component.registry.CamelRegistrySupportApi;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;

public class ApiImplementationRegistration {
    private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    public ApiImplementationRegistration(SimpleRegistry registry) {
        registry.put("datasource", getDataSource());
    }

    public DataSource getDataSource() {
        try {
            Class.forName(driver);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Failed to register driver.", e);
        }
        return createDataSource();
    }

    private DataSource createDataSource() {
        final DataSource ds = new EmbeddedDataSource();
        ((EmbeddedDataSource) ds).setUser("");
        ((EmbeddedDataSource) ds).setPassword("");
        ((EmbeddedDataSource) ds).setDatabaseName("data/testdb");
        ((EmbeddedDataSource) ds).setCreateDatabase("create");
        return ds;
    }
}
