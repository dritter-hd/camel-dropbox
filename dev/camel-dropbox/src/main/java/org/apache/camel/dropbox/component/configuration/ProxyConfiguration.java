package org.apache.camel.dropbox.component.configuration;

import org.apache.camel.spi.UriParam;

public class ProxyConfiguration {
    @UriParam
    private String proxyHost = System.getProperty("http.proxyHost");
    @UriParam
    private int proxyPort = Integer.parseInt(System.getProperty("http.proxyPort", "0"));
    public String getProxyHost() {
        return proxyHost;
    }
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }
    public int getProxyPort() {
        return proxyPort;
    }
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
}
