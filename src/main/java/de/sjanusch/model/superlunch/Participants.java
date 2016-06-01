package de.sjanusch.model.superlunch;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sandro Janusch
 * Date: 13.05.16
 * Time: 19:19
 */
public class Participants {

    private List<Participant> participantList;

    public Participants() {
        participantList = new LinkedList<Participant>();
    }

    public void addWorklogAttribute(final Participant participant) {
        participantList.add(participant);
    }

    public List<Participant> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(final List<Participant> participantList) {
        this.participantList = participantList;
    }
}
