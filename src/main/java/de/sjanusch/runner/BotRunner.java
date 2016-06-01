package de.sjanusch.runner;

/**
 * Created by Sandro Janusch
 * Date: 13.05.16
 * Time: 06:37
 */
public interface BotRunner {

    void runBot(final RunnableBot bot);

    Thread runBotDesync(final RunnableBot bot);

}
