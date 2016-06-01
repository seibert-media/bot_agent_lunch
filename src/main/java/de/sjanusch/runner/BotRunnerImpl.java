package de.sjanusch.runner;

import com.google.inject.Inject;

import de.sjanusch.networking.Connection;

public class BotRunnerImpl implements BotRunner {

    private final Connection connection;

    @Inject
    public BotRunnerImpl(final Connection connection) {
        this.connection = connection;
    }

  private void run(final RunnableBot bot) {
        bot.run();
        try {
            connection.waitForEnd();
    } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Thread runBotDysync(final RunnableBot bot) {
    final Thread t = new Thread() {

            @Override
            public void run() {
                BotRunnerImpl.this.run(bot);
            }
        };
        return t;
    }

  @Override
  public void runBot(final RunnableBot bot) {
        new BotRunnerImpl(connection).run(bot);
    }

  @Override
  public Thread runBotDesync(final RunnableBot bot) {
        return new BotRunnerImpl(connection).runBotDysync(bot);
    }
}
