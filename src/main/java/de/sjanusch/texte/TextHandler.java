package de.sjanusch.texte;

public interface TextHandler {

    String getRandomText(final String text);

    String getThankYouText();

    String getHelloText();

    String getByeText();

    boolean containsHelloText(final String text);

    boolean containsByeText(final String text);

    boolean containsLunchLoginText(final String text);

    boolean conatainsLunchLoginCommands(final String text);

    boolean containsHelpCommand(final String text);

    boolean containsLunchLogoutText(final String text);

    boolean conatainsLunchLogoutCommands(final String text);

    String getHelpText();

    String getRandomGeneratedText();

    String getOverviewErrorText();
}
