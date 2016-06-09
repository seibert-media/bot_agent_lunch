package de.sjanusch.guice;

import com.google.inject.AbstractModule;
import de.benjaminborbe.bot.agent.MessageHandler;
import de.sjanusch.bot.Bot;
import de.sjanusch.bot.BotImpl;
import de.sjanusch.configuration.BotConfiguration;
import de.sjanusch.configuration.BotConfigurationImpl;
import de.sjanusch.configuration.ChatConnectionConfiguration;
import de.sjanusch.configuration.ChatConnectionConfigurationImpl;
import de.sjanusch.configuration.HipchatConfiguration;
import de.sjanusch.configuration.HipchatConfigurationImpl;
import de.sjanusch.configuration.LunchConfiguration;
import de.sjanusch.configuration.LunchConfigurationImpl;
import de.sjanusch.configuration.TexteConfiguration;
import de.sjanusch.configuration.TexteConfigurationImpl;
import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.confluence.handler.SuperlunchRequestHandlerImpl;
import de.sjanusch.confluence.rest.SuperlunchRestClient;
import de.sjanusch.confluence.rest.SuperlunchRestClientImpl;
import de.sjanusch.eventsystem.EventSystem;
import de.sjanusch.eventsystem.EventSystemImpl;
import de.sjanusch.hipchat.handler.HipchatRequestHandler;
import de.sjanusch.hipchat.handler.HipchatRequestHandlerImpl;
import de.sjanusch.hipchat.rest.HipchatRestClient;
import de.sjanusch.hipchat.rest.HipchatRestClientImpl;
import de.sjanusch.listener.LunchListenerHelper;
import de.sjanusch.listener.LunchListenerHelperImpl;
import de.sjanusch.listener.LunchMessageRecieveListener;
import de.sjanusch.listener.LunchMessageRecieveListenerImpl;
import de.sjanusch.listener.LunchPrivateMessageRecieveListener;
import de.sjanusch.listener.LunchPrivateMessageRecieveListenerImpl;
import de.sjanusch.listener.PrivateMessageRecieverBase;
import de.sjanusch.listener.PrivateMessageRecieverBaseImpl;
import de.sjanusch.networking.ChatClient;
import de.sjanusch.networking.ChatClientImpl;
import de.sjanusch.networking.Connection;
import de.sjanusch.networking.ConnectionImpl;
import de.sjanusch.protocol.LunchMessageProtocol;
import de.sjanusch.protocol.LunchMessageProtocolImpl;
import de.sjanusch.runner.BotRunner;
import de.sjanusch.runner.BotRunnerImpl;
import de.sjanusch.texte.TextHandler;
import de.sjanusch.texte.TextHandlerImpl;

public class GuiceModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Bot.class).to(BotImpl.class);
    bind(BotRunner.class).to(BotRunnerImpl.class);
    bind(SuperlunchRequestHandler.class).to(SuperlunchRequestHandlerImpl.class);
    bind(SuperlunchRestClient.class).to(SuperlunchRestClientImpl.class);
    bind(HipchatRequestHandler.class).to(HipchatRequestHandlerImpl.class);
    bind(PrivateMessageRecieverBase.class).to(PrivateMessageRecieverBaseImpl.class);
    bind(LunchConfiguration.class).to(LunchConfigurationImpl.class);
    bind(BotConfiguration.class).to(BotConfigurationImpl.class);
    bind(ChatConnectionConfiguration.class).to(ChatConnectionConfigurationImpl.class);
    bind(HipchatConfiguration.class).to(HipchatConfigurationImpl.class);
    bind(HipchatRestClient.class).to(HipchatRestClientImpl.class);
    bind(TextHandler.class).to(TextHandlerImpl.class);
    bind(TexteConfiguration.class).to(TexteConfigurationImpl.class);
    bind(LunchMessageRecieveListener.class).to(LunchMessageRecieveListenerImpl.class);
    bind(ChatClient.class).to(ChatClientImpl.class);
    bind(LunchPrivateMessageRecieveListener.class).to(LunchPrivateMessageRecieveListenerImpl.class);
    bind(LunchListenerHelper.class).to(LunchListenerHelperImpl.class);

    bind(EventSystem.class).to(EventSystemImpl.class).asEagerSingleton();
    bind(Connection.class).to(ConnectionImpl.class).asEagerSingleton();
    bind(LunchMessageProtocol.class).to(LunchMessageProtocolImpl.class).asEagerSingleton();
		bind(MessageHandler.class).to(LunchMessageHandler.class);
  }
}
