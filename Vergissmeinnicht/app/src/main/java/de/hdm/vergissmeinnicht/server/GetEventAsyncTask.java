package de.hdm.vergissmeinnicht.server;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.List;

import de.hdm.vergissmeinnicht.activities.SplashActivity;
import de.hdm.vergissmeinnicht.android.CustomApplication;

/**
 * Created by Dennis Jonietz on 09.08.2015.
 */
public class GetEventAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = GetEventAsyncTask.class.getSimpleName();

    private CustomApplication mApp;
    private String mCalId;

    public GetEventAsyncTask(CustomApplication app, String calId) {
        mCalId = calId;
        mApp = app;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Event event = mApp.mService
                    .events()
                    .get("primary", mCalId)
                    .execute();
            if (event != null) {
                Log.i("Calendar", "--> " + event.getSummary());
            } else {
                Log.i("Calendar", "fail");

            }
//            mCallbackInterface.saveEventCallback(true, event.getId());

        } catch (Exception e) {
//            mCallbackInterface.saveEventCallback(false, null);
            e.printStackTrace();
        }

        return null;

    }

}