package de.sjanusch.configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Sandro Janusch Date: 17.05.16 Time: 11:15
 */
public class UsersConfigurationImpl implements UsersConfiguration {

  private final ConfigurationLoader configurationLoader;

  @Inject
  public UsersConfigurationImpl() {
    this.configurationLoader = new ConfigurationLoader("users.properties");
  }

  @Override
  public String castHipchatUser(final String userName) throws IOException {
    final String apiUserCombo = "hipchat." + userName;
    final Properties properties = configurationLoader.getProperties();
    if (properties.containsKey(apiUserCombo)) {
      return this.configurationLoader.getPropertyStringValue(apiUserCombo);
    }
    return userName + this.getEmailPostfix();
  }

  @Override
  public String castConfluenceUser(final String userName) throws IOException {
    final String apiUserCombo = "confluence." + userName;
    final Properties properties = configurationLoader.getProperties();
    if (properties.containsKey(apiUserCombo)) {
      return this.configurationLoader.getPropertyStringValue(apiUserCombo);
    }
    return userName;
  }

  private String getEmailPostfix() throws IOException {
    return this.configurationLoader.getPropertyStringValue("emailPostfix");
  }

}
