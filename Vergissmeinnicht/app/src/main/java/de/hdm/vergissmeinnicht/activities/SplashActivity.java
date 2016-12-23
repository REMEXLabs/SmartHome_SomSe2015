package de.hdm.vergissmeinnicht.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.server.EventsAsyncTask;


/**
 * Created by Dennis Jonietz on 6/13/15.
 */
public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            Log.d(TAG, "Google Play Services required: after installing, close and relaunch this app.");
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;

            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    Log.d(TAG, "Account specified.");
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        Log.d(TAG, "Accountname valid");
                        ((CustomApplication) getApplication()).mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getSharedPreferences(StaticValues.PREFS_APP_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(CustomApplication.PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Log.d(TAG, "Account unspecified.");
                }
                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (((CustomApplication) getApplication()).mCredential.getSelectedAccountName() == null) {
            chooseAccount();

        } else {
            if (isDeviceOnline()) {
                new EventsAsyncTask(this).execute();

            } else {
                Log.d(TAG, "No network connection available.");
            }
        }
    }

    /**
     * Callback for EventsAsyncTask: passes all events from Google Calendar
     * @param eventsList a List of events.
     */
    public void apiCallback(final List<Event> eventsList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = null;
                if (eventsList == null) {
                    Log.d(TAG, "Error retrieving data!");
                    message = getResources().getString(R.string.error_retrieving_data);

                } else if (eventsList.size() == 0) {
                    Log.d(TAG, "Currently no data saved in calendar.");
                    message = getResources().getString(R.string.no_data);

                } else {
                    Log.d(TAG, "Events retrieved using the Google Calendar API: " + eventsList.size());
                    message = getResources().getString(R.string.retrieved_events) + eventsList.size();
                }

                Toast.makeText(
                        SplashActivity.this,
                        message,
                        Toast.LENGTH_SHORT)
                        .show();

                // create ArrayLists of EventData with data from Google Calendar API
                final CustomApplication customApplication = ((CustomApplication) getApplication());
                customApplication.createEventDataLists(eventsList);

                // start MainActivity with correct interface and event data
                Intent intent = null;
                if (customApplication.getAppInterface() == StaticValues.SENIOR_INTERFACE) {
                    intent = new Intent(SplashActivity.this, SeniorMainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, DefaultMainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * EventsAsyncTask: Error callback
     * @param message a String to display in the UI header TextView.
     */
    public void updateStatus(final String message) {
        Log.e(TAG, "EventsAsyncTask: " + message);

        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(
                        SplashActivity.this,
                        message,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
            startActivityForResult(((CustomApplication) getApplication()).mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;

        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        SplashActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

}
