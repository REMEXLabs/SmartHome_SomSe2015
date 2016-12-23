package de.hdm.vergissmeinnicht.server;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;

import com.google.api.services.calendar.model.Event;

import java.io.IOException;

import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.interfaces.ApiCallbackInterface;
import de.hdm.vergissmeinnicht.model.CustomData;

/**
 * AsyncTask to delete a event from Google Calendar
 * Created by Dennis Jonietz on 16.08.2015.
 */
public class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = DeleteAsyncTask.class.getSimpleName();

    private ApiCallbackInterface mCallbackInterface;
    private String mEventId;

    /**
     * Constructor.
     * @param callbackInterface Activity that spawned this task and implements the ApiCallbackInterface.
     */
    public DeleteAsyncTask(ApiCallbackInterface callbackInterface, String eventId) {
        mCallbackInterface = callbackInterface;
        mEventId = eventId;
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
                application = ((CustomApplication) ((Activity) mCallbackInterface).getApplication());
            } else {
                application = ((CustomApplication) ((Fragment) mCallbackInterface).getActivity().getApplication());
            }

            application.mService
                    .events()
                    .delete("primary", mEventId)
                    .execute();
            mCallbackInterface.deleteEventCallback(true);

        } catch (Exception e) {
            mCallbackInterface.deleteEventCallback(false);
            e.printStackTrace();
        }
        return null;
    }

}