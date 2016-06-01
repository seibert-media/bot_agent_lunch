package de.sjanusch.configuration;

import java.io.IOException;
import java.util.List;

/**
 * Created by Sandro Janusch
 * Date: 17.05.16
 * Time: 15:48
 */
public interface TexteConfiguration {

    List<String> getHelloTexteAsList() throws IOException;

    List<String> getThankYouTexteAsList() throws IOException;

    List<String> getRandomTexteAsList() throws IOException;

    List<String> getByeTexteAsList() throws IOException;

    List<String> getLunchLoginCommandsAsList() throws IOException;

    List<String> getLunchLogoutCommandsAsList() throws IOException;
}
