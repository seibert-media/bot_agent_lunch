package de.sjanusch.flow;

/**
 * Created by Sandro Janusch
 * Date: 19.05.16
 * Time: 10:01
 */
public enum LunchMessageZustand {

    ANMELDEN("Möchtest du dich anmelden? (ja/nein)"),
    ANMELDEN_JA("Für welches Essen? (Essen-ID)"),
    ANMELDEN_NEIN("Ok!"),
    ANMELDUNG_ERFOLGREICH("Die Anmeldung war erfolgreich!"),
    ANMELDUNG_FEHLGESCHLAGEN("Die Anmeldung war nicht erfolgreich! Bitte versuch es nocheinmal."),

    ABMELDEN("Du bist bereits angemeldet. Möchtest du dich abmelden? (ja/nein)"),
    ABMELDEN_JA("Für welches Essen? (Essen-ID)"),
    ABMELDEN_NEIN("Ok!"),
    ABMELDEN_ERFOLGREICH("Die Abmeldung war erfolgreich!"),
    ABMELDEN_FEHLGESCHLAGEN("Die Abmeldung war nicht erfolgreich! Bitte versuch es nocheinmal.");

    private String text;

    private LunchMessageZustand(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
