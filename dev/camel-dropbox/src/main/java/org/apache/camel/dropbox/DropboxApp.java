package org.apache.camel.dropbox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

public class DropboxApp {
    private Logger logger = LoggerFactory.getLogger(DropboxApp.class);

    private DbxRequestConfig config;
    private DropboxAppConfiguration appConfig;

    private DropboxApp(final DropboxAppConfiguration appConfig) {
        this.appConfig = appConfig;
        config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
    }

    public static DropboxApp create(final DropboxAppConfiguration appConfig) {
        return new DropboxApp(appConfig);
    }

    public DbxClient connect() throws FileNotFoundException, IOException, DbxException {
        final DbxAppInfo appInfo = new DbxAppInfo(appConfig.getAppKey(), appConfig.getAppSecret());

        this.addOauthAccessTokenToConfiguration(appInfo);

        final DbxClient client = new DbxClient(config, appConfig.getAccessToken());
        logger.debug("Linked account: " + client.getAccountInfo().displayName);
        return client;
    }

    private void addOauthAccessTokenToConfiguration(final DbxAppInfo appInfo) throws FileNotFoundException, IOException, DbxException {
        String code = appConfig.getAccessToken();
        if (null == code) {
            // Get your app key and secret from the Dropbox developers website.
            final DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
            final String authorizeUrl = webAuth.start();

            System.out.println("1. Go to: " + authorizeUrl);
            System.out.println("2. Click \"Allow\" (you might have to log in first)");
            System.out.println("3. Copy the authorization code.");

            code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

            final DbxAuthFinish authFinish = webAuth.finish(code);

            final String accessToken = authFinish.accessToken;
            appConfig.setAccessToken(accessToken);
        }
    }
}
