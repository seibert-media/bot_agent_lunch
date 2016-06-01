package de.sjanusch.configuration;

import java.io.IOException;

/**
 * Created by Sandro Janusch
 * Date: 17.05.16
 * Time: 11:15
 */
public class HipchatConfigurationImpl implements HipchatConfiguration {

    private ConfigurationLoader configurationLoader;

    public HipchatConfigurationImpl() {
        this.configurationLoader = new ConfigurationLoader("hipchat.properties");
    }

    @Override
    public String getHipchatRestApi() throws IOException {
        return this.configurationLoader.getPropertyStringValue("hipchat_rest_api");
    }

    @Override
    public String getHipchatRestApiKeyNotification() throws IOException {
        return this.configurationLoader.getPropertyStringValue("hipchat_rest_api_key_notification");
    }

    @Override
    public String getHipchatRestApiKeyMessage() throws IOException {
        return this.configurationLoader.getPropertyStringValue("hipchat_rest_api_key_message");
    }

    @Override
    public String getHipchatRestApiRoomId() throws IOException {
        return this.configurationLoader.getPropertyStringValue("hipchat_rest_api_room_id");
    }

}
