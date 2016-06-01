package de.sjanusch.configuration;

import java.io.IOException;

/**
 * Created by Sandro Janusch
 * Date: 17.05.16
 * Time: 11:32
 */
public interface ChatConnectionConfiguration {

    String getXmppUrl() throws IOException;

    String getConfUrl() throws IOException;

    Integer getXmppPort() throws IOException;

}
