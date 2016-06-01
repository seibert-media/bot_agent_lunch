package de.sjanusch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.sjanusch.guice.GuiceModule;
import de.sjanusch.runner.BotRunner;

public class Main {
    
    public static void main(String[] args) {
        final Injector injector = Guice.createInjector(new GuiceModule());
        final ChatBot bot = injector.getInstance(ChatBot.class);
        final BotRunner botRunner = injector.getInstance(BotRunner.class);
        final Thread t = botRunner.runBotDesync(bot);
        t.start();
    }
}
