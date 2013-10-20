package org.apache.camel.dropbox;

public class DropboxAppConfiguration {

    private String appKey;
    private String appSecret;
    private String accessToken;

    public DropboxAppConfiguration(final String appKey, final String appSecret, final String accessToken) {
        this.setAccessToken(accessToken);
        this.setAppSecret(appSecret);
        this.setAppKey(appKey);
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
}
