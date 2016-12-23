package de.hdm.vergissmeinnicht.server;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import de.hdm.vergissmeinnicht.activities.SplashActivity;
import de.hdm.vergissmeinnicht.android.CustomApplication;

/**
 * Created by Dennis Jonietz on 09.08.2015.
 */
public class EventsAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = EventsAsyncTask.class.getSimpleName();

    private SplashActivity mActivity;

    /**
     * Constructor.
     * @param activity SplashActivity that spawned this task.
     */
    public EventsAsyncTask(SplashActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.apiCallback(getDataFromApi());

        } catch (final GooglePlayServicesAvailabilityIOException e) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(e.getConnectionStatusCode());
            Log.e(TAG, e.getMessage());

        } catch (UserRecoverableAuthIOException e) {
            mActivity.startActivityForResult(e.getIntent(), SplashActivity.REQUEST_AUTHORIZATION);
            Log.e(TAG, e.getMessage());

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred while fetching events:\n" +
                    e.getMessage());
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    /**
     * Fetch a list of all events from the primary calendar.
     * @return List events.
     * @throws IOException
     */
    private List<Event> getDataFromApi() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = ((CustomApplication) mActivity.getApplication()).mService.events().list("primary")
                .setTimeMin(now)
                .execute();
        List<Event> items = events.getItems();
        return items;
    }

}