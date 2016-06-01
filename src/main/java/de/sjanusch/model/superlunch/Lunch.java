package de.sjanusch.model.superlunch;

import java.util.List;

/**
 * Created by Sandro Janusch
 * Date: 13.05.16
 * Time: 18:42
 */
public class Lunch {

    private String title;

    private String description;

    private String date;

    private int id;

    private boolean currentUserJoined;

    private boolean closed;

    private boolean alreadyNotified;

    private String price;

    private String formattedPrice;

    private String creatorName;

    private boolean inhouse;

    private boolean veggy;

    private boolean invoiceLunch;

    private String detailLink;

    private Participant[] participants;

    public boolean isAlreadyNotified() {
        return alreadyNotified;
    }

    public void setAlreadyNotified(final boolean alreadyNotified) {
        this.alreadyNotified = alreadyNotified;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(final boolean closed) {
        this.closed = closed;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(final String creatorName) {
        this.creatorName = creatorName;
    }

    public boolean isCurrentUserJoined() {
        return currentUserJoined;
    }

    public void setCurrentUserJoined(final boolean currentUserJoined) {
        this.currentUserJoined = currentUserJoined;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(final String detailLink) {
        this.detailLink = detailLink;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }

    public void setFormattedPrice(final String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public boolean isInhouse() {
        return inhouse;
    }

    public void setInhouse(final boolean inhouse) {
        this.inhouse = inhouse;
    }

    public boolean isInvoiceLunch() {
        return invoiceLunch;
    }

    public void setInvoiceLunch(final boolean invoiceLunch) {
        this.invoiceLunch = invoiceLunch;
    }

    public Participant[] getParticipants() {
        return participants;
    }

    public void setParticipants(final List<Participant> participantList) {
        Participant[] participants = new Participant[participantList.size()];
        for (int i = 0; i < participantList.size(); i++) {
            participants[i] = participantList.get(i);
        }
        this.participants = participants;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public boolean isVeggy() {
        return veggy;
    }

    public void setVeggy(final boolean veggy) {
        this.veggy = veggy;
    }
}
