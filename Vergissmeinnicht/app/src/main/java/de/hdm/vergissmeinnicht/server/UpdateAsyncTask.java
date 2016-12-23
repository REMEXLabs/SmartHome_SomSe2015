package de.hdm.vergissmeinnicht.server;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;

import com.google.api.services.calendar.model.Event;

import java.lang.reflect.Constructor;

import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.interfaces.ApiCallbackInterface;

/**
 * AsyncTask to save a new event in Google Calendar
 * Created by Dennis Jonietz on 09.08.2015.
 */
public class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = UpdateAsyncTask.class.getSimpleName();

    private Context mContext;
    private Event mEventToSave;
    private ApiCallbackInterface mCallback;

    /**
     * Constructor.
     * @param context Activity/Application that spawned this task.
     */
    public UpdateAsyncTask(Context context, Event event) {
        mContext = context;
        mEventToSave = event;
    }

    /** Constructor.
    * @param callback Activity/Fragment that spawned this task and implements the ApiCallbackInterface.
    */
    public UpdateAsyncTask(ApiCallbackInterface callback, Event event) {
        mCallback = callback;
        mEventToSave = event;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            CustomApplication application = null;
            if (mCallback instanceof Activity) {
                application = ((CustomApplication) ((Activity) mCallback).getApplication());
            } else if (mContext instanceof CustomApplication) {
                application = (CustomApplication) mContext;
            } else {
                application = ((CustomApplication) ((Fragment) mCallback).getActivity().getApplication());
            }

            application.mService
                    .events()
                    .update("primary", mEventToSave.getId(), mEventToSave)
                    .execute();
            if (mCallback != null) {
                mCallback.updateEventCallback(true);
            }

        } catch (Exception e) {
            if (mCallback != null) {
                mCallback.updateEventCallback(false);
            }
            e.printStackTrace();
        }
        return null;
    }

}