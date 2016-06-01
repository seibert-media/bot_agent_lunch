package de.sjanusch;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.sjanusch.bot.Bot;
import de.sjanusch.runner.RunnableBot;

public class ChatBot implements RunnableBot {

    private static final Logger logger = LoggerFactory.getLogger(ChatBot.class);

    private final Bot bot;

    @Inject
    public ChatBot(final Bot bot) {
        this.bot = bot;
    }

    public void run() {
        bot.run();
        /*
        ChatReminderTask chatReminderTask = new ChatReminderTask();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(chatReminderTask, this.getHoursUntilTarget(8), 24, TimeUnit.HOURS);
        */
    }

    private int getHoursUntilTarget(int targetHour) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour < targetHour ? targetHour - hour : targetHour - hour + 24;
    }
}
