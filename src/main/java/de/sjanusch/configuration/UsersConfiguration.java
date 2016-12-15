package de.sjanusch.configuration;

import java.io.IOException;

/**
 * Created by Sandro Janusch Date: 17.05.16 Time: 11:15
 */
public interface UsersConfiguration {

  String castHipchatUser(final String userName) throws IOException;

  String castConfluenceUser(final String userName) throws IOException;
}
