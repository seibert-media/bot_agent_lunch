package de.sjanusch.configuration;

import java.io.IOException;

/**
 * Created by Sandro Janusch
 * Date: 14.12.16
 * Time: 10:22
 */
public interface NSQConfiguration {

  String getNSQAdress() throws IOException;

  String getNSQLookupAdress() throws IOException;

  Integer getNSQLookupAdressPort() throws IOException;

  Integer getNSQAdressPort() throws IOException;

  String getNsqTopicName() throws IOException;
}
