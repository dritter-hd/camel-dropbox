package org.apache.camel.dropbox.component.configuration;

import org.apache.camel.spi.UriParam;

public class SecurityConfiguration {
    @UriParam
    private String appKey;
    @UriParam
    private String appSecret;
    @UriParam
    private String accessToken;

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
}
