package de.sjanusch.configuration;

import javax.inject.Inject;
import java.io.IOException;

public class LunchConfigurationImpl implements LunchConfiguration {

  private final ConfigurationLoader configurationLoader;

  @Inject
  public LunchConfigurationImpl() {
    this.configurationLoader = new ConfigurationLoader("superlunch.properties");
  }

  @Override
  public String getRestApi() throws IOException {
    return this.configurationLoader.getPropertyStringValue("rest_api");
  }

  @Override
  public String getRestApiPath() throws IOException {
    return this.configurationLoader.getPropertyStringValue("rest_api_path");
  }

  @Override
  public String getRestApiConnectTimeout() throws IOException {
    return this.configurationLoader.getPropertyStringValue("rest_api_connect_timeout");
  }

  @Override
  public String getRestApiReadTimeout() throws IOException {
    return this.configurationLoader.getPropertyStringValue("rest_api_read_timeout");
  }

  @Override
  public String getLunchUsername() throws IOException {
    return this.configurationLoader.getPropertyStringValue("lunch_username");
  }

  @Override
  public String getLunchUserPassword() throws IOException {
    return System.getenv("LUNCH_CONFLUENCE_PASSWORD");
  }
}
