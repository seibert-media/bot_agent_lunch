package de.sjanusch.configuration;

import java.io.IOException;

/**
 * Created by Sandro Janusch
 * Date: 17.05.16
 * Time: 11:15
 */
public interface HipchatConfiguration {

    String getHipchatRestApi() throws IOException;

    String getHipchatRestApiKeyMessage() throws IOException;

    String getHipchatRestApiKeyNotification() throws IOException;

    String getHipchatRestApiRoomId() throws IOException;

}
