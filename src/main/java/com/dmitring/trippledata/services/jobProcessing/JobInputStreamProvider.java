package com.dmitring.trippledata.services.jobProcessing;

import java.io.InputStream;

public interface JobInputStreamProvider {
    InputStream getInputStream(String urlString);
}
