package de.sjanusch;

import java.util.TimerTask;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.sjanusch.guice.GuiceModule;
import de.sjanusch.hipchat.handler.HipchatRequestHandler;
import de.sjanusch.model.hipchat.HipchatMessage;

/**
 * Created by Sandro Janusch
 * Date: 16.05.16
 * Time: 20:54
 */
public class ChatReminderTask extends TimerTask {

    private final HipchatRequestHandler hipchatRequestHandler;

    public ChatReminderTask() {
        final Injector injector = Guice.createInjector(new GuiceModule());
        this.hipchatRequestHandler = injector.getInstance(HipchatRequestHandler.class);
    }

    @Override
    public void run() {
    final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@here Mittagessen Anmeldung nicht vergessen!");
        hipchatRequestHandler.sendNotification(new HipchatMessage(stringBuilder.toString()));
    }
}
