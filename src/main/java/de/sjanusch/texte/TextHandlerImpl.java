package de.sjanusch.texte;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.sjanusch.configuration.TexteConfiguration;

/**
 * Created by Sandro Janusch
 * Date: 17.05.16
 * Time: 11:58
 */
public class TextHandlerImpl implements TextHandler {

    private static final Logger logger = LoggerFactory.getLogger(TextHandlerImpl.class);

    private final TexteConfiguration texteConfiguration;

    @Inject
    public TextHandlerImpl(final TexteConfiguration texteConfiguration) {
        this.texteConfiguration = texteConfiguration;
    }

    public String getRandomGeneratedText() {
        try {
            return this.getRandomText(texteConfiguration.getRandomTexteAsList());
        } catch (IOException e) {
            logger.error("Error loading Configuration: " + e.getMessage());
        }
        return null;
    }

    public String getRandomText(final String text) {
        try {
            return this.getText(text, texteConfiguration.getRandomTexteAsList());
        } catch (IOException e) {
            logger.error("Error loading Configuration: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getThankYouText() {
        try {
            return this.getText(texteConfiguration.getThankYouTexteAsList());
        } catch (IOException e) {
            logger.error("Error loading Configuration: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getHelloText() {
        try {
            return this.getText(texteConfiguration.getHelloTexteAsList());
        } catch (IOException e) {
            logger.error("Error loading Configuration: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean containsHelloText(final String text) {
        try {
            for (String hello : texteConfiguration.getHelloTexteAsList()) {
                if (this.containsWord(text.toLowerCase().trim(), hello.toLowerCase().trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error("Error loading Configuration: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean containsLunchLoginText(final String text) {
        final String compare = text.toLowerCase().trim();
        if ((compare.contains("was") && compare.contains("gibt") && compare.contains("essen"))
            || (compare.contains("habe") && compare.contains("hunger"))
            || (compare.contains("was") && compare.contains("gibt") && compare.contains("mittagessen"))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean containsLunchLogoutText(final String text) {
        final String compare = text.toLowerCase().trim();
        if ((compare.contains("was") && compare.contains("anderes") && compare.contains("essen"))
            || (compare.contains("keinen") && compare.contains("hunger"))
            || (compare.contains("was") && compare.contains("anders") && compare.contains("mittagessen"))) {
            return true;
        }
        return false;
    }

    @Override
    public String getByeText() {
        try {
            return this.getText(texteConfiguration.getByeTexteAsList());
        } catch (IOException e) {
            logger.error("Error loading Configuration: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean containsByeText(final String text) {
        try {
            for (String bye : texteConfiguration.getByeTexteAsList()) {
                if (this.containsWord(text.toLowerCase().trim(), bye.toLowerCase().trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error("Error loading Configuration: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean conatainsLunchLoginCommands(final String text) {
        try {
            final String compare = text.toLowerCase().trim();
            for (String command : texteConfiguration.getLunchLoginCommandsAsList()) {
                if (compare.startsWith("/") && this.containsWord(compare, command.toLowerCase().trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error("Error loading Configuration: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean conatainsLunchLogoutCommands(final String text) {
        try {
            final String compare = text.toLowerCase().trim();
            for (String command : texteConfiguration.getLunchLogoutCommandsAsList()) {
                if (compare.startsWith("/") && this.containsWord(compare, command.toLowerCase().trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error("Error loading Configuration: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean containsHelpCommand(final String text) {
        final String compare = text.toLowerCase().trim();
        if (text.startsWith("/") && (compare.contains("help") || compare.contains("hilfe") || compare.contains("befehle"))) {
            return true;
        }
        return false;
    }

    @Override
    public String getHelpText() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<b>Befehle für Lunchbot:</b><br></br>");
            stringBuilder.append("<b>Anmelden</b><br>");
            for (String s : texteConfiguration.getLunchLoginCommandsAsList()) {
                stringBuilder.append("<li>" + s + "</li>");
            }
            stringBuilder.append("<br><b>Abmelden</b><br>");
            for (String s : texteConfiguration.getLunchLogoutCommandsAsList()) {
                stringBuilder.append("<li>" + s + "</li>");
            }
            return stringBuilder.toString();

        } catch (IOException e) {
            logger.error("Error loading Configuration: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getOverviewErrorText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Mittagessen Übersicht noch nicht verfügbar!\n");
        stringBuilder.append("https://confluence.rp.seibert-media.net/dashboard.action");
        return stringBuilder.toString();
    }

    private String getText(final String text, final List<String> texts) {
        int number = this.getRandomNumberInRange(0, texts.size());
        if (number >= 0 && number < texts.size() && !texts.contains(text)) {
            return texts.get(number);
        }
        return this.getText(text, texts);
    }

    private String getText(final List<String> texts) {
        int number = this.getRandomNumberInRange(0, texts.size());
        if (number >= 0 && number < texts.size()) {
            return texts.get(number);
        }
        return this.getText(texts);
    }

    private String getRandomText(final List<String> texts) {
        if (this.getRandomTrueFalse()) {
            return this.getText(texts);
        }
        return null;
    }

    private boolean getRandomTrueFalse() {
        final int i = getRandomNumberInRange(0, 3);
        return (i == 1) ? true : false;
    }

    private int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private boolean containsWord(final String sentence, final String word) {
        return sentence.indexOf(word) != -1;
    }

}
