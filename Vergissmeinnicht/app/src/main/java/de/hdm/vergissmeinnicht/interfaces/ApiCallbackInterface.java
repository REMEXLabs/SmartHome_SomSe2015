package de.hdm.vergissmeinnicht.interfaces;

/**
 * Created by Dennis Jonietz on 8/12/15.
 */
public interface ApiCallbackInterface {

    public void saveEventCallback(boolean result, String eventId);
    public void updateEventCallback(boolean result);
    public void deleteEventCallback(boolean result);

}
