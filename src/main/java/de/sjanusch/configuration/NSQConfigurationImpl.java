package de.sjanusch.configuration;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Sandro Janusch
 * Date: 14.12.16
 * Time: 10:22
 */

public class NSQConfigurationImpl implements NSQConfiguration {

  private final ConfigurationLoader configurationLoader;

  @Inject
  public NSQConfigurationImpl() {
    this.configurationLoader = new ConfigurationLoader("nsq.properties");
  }

  @Override
  public String getNSQLookupAdress() throws IOException {
    final String env = System.getenv("NSQ_LOOKUPD_ADDRESS");
    if (env != null && !env.isEmpty()) {
      final String[] lookupAdressArray = this.splitEnvVariable(System.getenv("NSQ_LOOKUPD_ADDRESS"));
      return lookupAdressArray[0];
    }
    return this.configurationLoader.getPropertyStringValue("nsq.lookupadress");
  }

  @Override
  public Integer getNSQLookupAdressPort() throws IOException {
    final String env = System.getenv("NSQ_LOOKUPD_ADDRESS");
    if (env != null && !env.isEmpty()) {
      final String[] lookupAdressArray = this.splitEnvVariable(System.getenv("NSQ_LOOKUPD_ADDRESS"));
      return Integer.valueOf(lookupAdressArray[1]);
    }
    return Integer.valueOf(this.configurationLoader.getPropertyStringValue("nsq.lookupadressport"));
  }

  @Override
  public String getNSQAdress() throws IOException {
    final String env = System.getenv("NSQD_ADDRESS");
    if (env != null && !env.isEmpty()) {
      final String[] adressArray = this.splitEnvVariable(System.getenv("NSQD_ADDRESS"));
      return adressArray[0];
    }
    return this.configurationLoader.getPropertyStringValue("nsq.adress");
  }

  @Override
  public Integer getNSQAdressPort() throws IOException {
    final String env = System.getenv("NSQD_ADDRESS");
    if (env != null && !env.isEmpty()) {
      final String[] adressArray = this.splitEnvVariable(System.getenv("NSQD_ADDRESS"));
      return Integer.valueOf(adressArray[1]);
    }
    return Integer.valueOf(this.configurationLoader.getPropertyStringValue("nsq.adressport"));
  }

  @Override
  public String getNsqTopicName() throws IOException {
    return this.configurationLoader.getPropertyStringValue("nsq.topicName");
  }

  private String[] splitEnvVariable(final String string) {
    return string.split(":");
  }

}
