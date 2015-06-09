package org.apache.camel.dropbox.registry;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class CamelBlueprintRegistrySupport extends CamelBlueprintTestSupport {
    @Override
    protected String getBlueprintDescriptor() {
        return "registry/blueprint.xml";
    }

    @Test
    public void checkRegistry() throws Exception {
        assertNotNull(context().getRegistry().lookupByName("datasource"));
    }
}
