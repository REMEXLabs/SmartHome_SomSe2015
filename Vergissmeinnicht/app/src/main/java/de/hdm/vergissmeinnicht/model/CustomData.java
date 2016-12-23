package de.hdm.vergissmeinnicht.model;

/**
 * Created by root on 13.06.15.
 */
public class CustomData {

    private String eventId;
    private boolean isHeader = false;

    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public boolean isHeader() {
        return isHeader;
    }
    public void setIsHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

}
