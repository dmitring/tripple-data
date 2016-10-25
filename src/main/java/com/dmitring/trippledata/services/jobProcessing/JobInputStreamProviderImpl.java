package com.dmitring.trippledata.services.jobProcessing;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Component
public class JobInputStreamProviderImpl implements JobInputStreamProvider {
    @Value("${com.dmitring.trippledata.urlReadTimeout}")
    private int readTimeoutMillis;

    @Value("${com.dmitring.trippledata.urlConnectionTimeout}")
    private int connectTimeoutMillis;

    @Override
    @SneakyThrows
    public InputStream getInputStream(String urlString) {
        final URLConnection connection = getConnection(urlString);
        return connection.getInputStream();
    }

    private URLConnection getConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.setReadTimeout(readTimeoutMillis);
        connection.setConnectTimeout(connectTimeoutMillis);
        connection.setAllowUserInteraction(false);
        return connection;
    }
}
