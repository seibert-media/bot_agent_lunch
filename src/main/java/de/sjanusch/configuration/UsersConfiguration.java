package de.sjanusch.configuration;

import java.io.IOException;

/**
 * Created by Sandro Janusch Date: 17.05.16 Time: 11:15
 */
public interface UsersConfiguration {

  String castUser(final String userName) throws IOException;

}
