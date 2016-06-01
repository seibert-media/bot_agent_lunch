package de.sjanusch.model.hipchat;

/**
 * Created by Sandro Janusch
 * Date: 17.05.16
 * Time: 20:38
 */
public class HipchatMessage {

    private String color = "yellow";

    private String message_format = "text";

    private String message;

    private boolean notify = true;

    public HipchatMessage(final String message, final String typ) {
        this.message = message;
        this.message_format = typ;
    }

    public HipchatMessage(final String message) {
        this.message = message;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getMessage_format() {
        return message_format;
    }

    public void setMessage_format(final String message_format) {
        this.message_format = message_format;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(final boolean notify) {
        this.notify = notify;
    }
}
