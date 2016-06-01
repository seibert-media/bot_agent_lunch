package de.sjanusch.configuration;

import java.io.IOException;

/**
 * Created by Sandro Janusch
 * Date: 17.05.16
 * Time: 11:32
 */
public class ChatConnectionConfigurationImpl implements ChatConnectionConfiguration {

    private ConfigurationLoader configurationLoader;

    public ChatConnectionConfigurationImpl() {
        this.configurationLoader = new ConfigurationLoader("connection.properties");
    }

    @Override
    public String getXmppUrl() throws IOException {
        return this.configurationLoader.getPropertyStringValue("xmpp_url");
    }

    @Override
    public String getConfUrl() throws IOException {
        return this.configurationLoader.getPropertyStringValue("conf_url");
    }

    @Override
    public Integer getXmppPort() throws IOException {
        final String port = this.configurationLoader.getPropertyStringValue("xmpp_port");
        return Integer.valueOf(port);
    }

}
