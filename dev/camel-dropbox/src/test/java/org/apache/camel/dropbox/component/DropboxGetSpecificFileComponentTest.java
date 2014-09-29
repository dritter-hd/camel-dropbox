/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.dropbox.component;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dropbox.utils.DropboxConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

public class DropboxGetSpecificFileComponentTest extends CamelTestSupport {
    private String appKey;
    private String appSecret;
    private String accessToken;
  
    @Ignore
    @Test
    public void testDropbox() throws Exception {
        final MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        this.setupConfiguration();
        Assume.assumeNotNull(this.accessToken);

        return new RouteBuilder() {
            public void configure() {
                from("dropbox://get?path=" + "/Public/ioio.txt" + "&appKey=" + appKey + "&appSecret=" + appSecret + "&accessToken=" + accessToken).to(
                        "file:data/outbox").to("mock:result");
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
