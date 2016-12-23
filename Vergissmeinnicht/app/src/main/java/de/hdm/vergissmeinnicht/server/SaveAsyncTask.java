package de.hdm.vergissmeinnicht.server;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;

import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.interfaces.ApiCallbackInterface;

/**
 * AsyncTask to save a new event in Google Calendar
 * Created by Dennis Jonietz on 09.08.2015.
 */
public class SaveAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = SaveAsyncTask.class.getSimpleName();

    private ApiCallbackInterface mCallbackInterface;
    private Event eventToSave;

    /**
     * Constructor.
     * @param callbackInterface Activity that spawned this task and implements the ApiCallbackInterface.
     */
    public SaveAsyncTask(ApiCallbackInterface callbackInterface, Event event) {
        mCallbackInterface = callbackInterface;
        eventToSave = event;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            CustomApplication application = null;

            if (mCallbackInterface instanceof Activity) {
                application = ((CustomApplication) ((Activity) mCallbackInterface)
                        .getApplication());
            } else {
                application = ((CustomApplication) ((Fragment) mCallbackInterface)
                        .getActivity()
                        .getApplication());
            }

            Event event = application.mService
                    .events()
                    .insert("primary", eventToSave)
                    .execute();
            mCallbackInterface.saveEventCallback(true, event.getId());

        } catch (Exception e) {
            mCallbackInterface.saveEventCallback(false, null);
            e.printStackTrace();
        }
        return null;
    }

}