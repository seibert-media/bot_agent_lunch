package de.sjanusch.configuration;

import java.io.IOException;

/**
 * Created by Sandro Janusch
 * Date: 17.05.16
 * Time: 11:15
 */
public class BotConfigurationImpl implements BotConfiguration {

    private ConfigurationLoader configurationLoader;

    public BotConfigurationImpl() {
        this.configurationLoader = new ConfigurationLoader("bot.properties");
    }

    @Override
    public String getBotNickname() throws IOException {
        return this.configurationLoader.getPropertyStringValue("bot_nickname");
    }

    @Override
    public String getBotUsername() throws IOException {
        return this.configurationLoader.getPropertyStringValue("bot_username");
    }

    @Override
    public String getBotMentionName() throws IOException {
        return this.configurationLoader.getPropertyStringValue("bot_mention_name");
    }

    @Override
    public String getBotPassword() throws IOException {
        return System.getenv("LUNCH_HIPCHAT_PASSWORD");
    }

    @Override
    public String getBotChatRoom() throws IOException {
        return this.configurationLoader.getPropertyStringValue("bot_chat_room");
    }

}
