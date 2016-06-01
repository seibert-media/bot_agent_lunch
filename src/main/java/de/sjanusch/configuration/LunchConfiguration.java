package de.sjanusch.configuration;

import java.io.IOException;

public interface LunchConfiguration {

    String getRestApi() throws IOException;

    String getRestApiPath() throws IOException;

    String getRestApiConnectTimeout() throws IOException;

    String getRestApiReadTimeout() throws IOException;

    String getLunchUsername() throws IOException;

    String getLunchUserPassword() throws IOException;
}
