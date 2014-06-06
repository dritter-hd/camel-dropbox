package org.apache.camel.dropbox;

public class DropboxAppConfiguration {

    private String proxyHost;
    private int proxyPort;
    private String appKey;
    private String appSecret;
    private String accessToken;

    public DropboxAppConfiguration(final String appKey, final String appSecret, final String accessToken, final String proxyHost, final int proxyPort) {
        this.setAccessToken(accessToken);
        this.setAppSecret(appSecret);
        this.setAppKey(appKey);
        this.setProxyHost(proxyHost);
        this.setProxyPort(proxyPort);
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }
}
