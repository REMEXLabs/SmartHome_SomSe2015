package de.hdm.vergissmeinnicht.android;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.dialogs.AccountDialog;
import de.hdm.vergissmeinnicht.dialogs.AuthenticateDialog;
import de.hdm.vergissmeinnicht.dialogs.SettingsDialog;
import de.hdm.vergissmeinnicht.helpers.AppointmentDataComparator;
import de.hdm.vergissmeinnicht.helpers.MedicineDataComparator;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.interfaces.ApiCallbackInterface;
import de.hdm.vergissmeinnicht.interfaces.SettingsDialogInterface;
import de.hdm.vergissmeinnicht.model.AppointmentData;
import de.hdm.vergissmeinnicht.model.MedicineData;
import de.hdm.vergissmeinnicht.model.PlantData;
import de.hdm.vergissmeinnicht.server.DeleteAsyncTask;
import de.hdm.vergissmeinnicht.server.SaveAsyncTask;
import de.hdm.vergissmeinnicht.server.UpdateAsyncTask;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Dennis Jonietz on 6/13/15.
 */
public class CustomApplication extends MultiDexApplication {

    private static final String GCAL_BINDING_SOUND = "send playuri \"//192.168.188.34/sounds/notification_vgm.wav\"";
    private static final String GCAL_BINDING_LIGHT = "send switchlight ON";
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String[] SCOPES = { CalendarScopes.CALENDAR };

    private ArrayList<MedicineData> mMedicineList;
    private ArrayList<PlantData> mPlantsList;
    private ArrayList<AppointmentData> mAppointmentsList;
    private ArrayList<String> mTaskList;

    private String mAppTextsize, mAppLanguage, mAccountMail, mAccountPwd;
    private boolean mReminderAudio, mReminderLight;
    private int mAppInterface;
    private String[] mAutoCompleteArray;
    private ServerCommunication mServerCommunication;

    /**
     * A Google Calendar API service object used to access the API.
     * Note: Do not confuse this class with API library's model classes, which
     * represent specific data structures.
     */
    public com.google.api.services.calendar.Calendar mService;
    public GoogleAccountCredential mCredential;
    private final HttpTransport mTransport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory mJsonFactory = GsonFactory.getDefaultInstance();

    public void setmMedicineList(ArrayList<MedicineData> mMedicineList){
        this.mMedicineList = mMedicineList;
    }

    public ArrayList<MedicineData> getmMedicineList(){
        return mMedicineList;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // setup Calligraphy library
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/X360.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        String user_creds = getResources().getString(R.string.koubachi_user_credentials);
        String app_key = getResources().getString(R.string.koubachi_app_key);
        mServerCommunication = new ServerCommunication();
        mServerCommunication.getSmartDeviceInfo(user_creds, app_key, this);
        mServerCommunication.getPlantTasks(user_creds, app_key, this);
        mPlantsList = new ArrayList<PlantData>();
        mTaskList =  new ArrayList<String>();

        // Initialize credentials and service object.
        SharedPreferences settings = getSharedPreferences(StaticValues.PREFS_APP_NAME, Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                mTransport, mJsonFactory, mCredential)
                .setApplicationName(getResources().getString(R.string.app_name))
                .build();
    }

    public String getLanguage() {
        loadPrefs();
        return mAppLanguage;
    }
    public String getTextsize() {
        loadPrefs();
        return mAppTextsize;
    }

    public boolean getReminderSound() {
        loadPrefs();
        return mReminderAudio;
    }
    public boolean getReminderLight() {
        loadPrefs();
        return mReminderLight;
    }
    public int getAppInterface() {
        loadPrefs();
        return mAppInterface;
    }
    public String[] getAutoCompleteArray() {
        return mAutoCompleteArray;
    }

    public ArrayList<MedicineData> getMedicineList() {
        return mMedicineList;
    }
    public ArrayList<PlantData> getPlantsList() {
        return mPlantsList;
    }
    public ArrayList<AppointmentData> getAppointmentsList() {
        return mAppointmentsList;
    }


    // --------------------------------------
    // --------------- EVENTS ---------------
    // --------------------------------------
    // checks for any important event to show in overview
    public String getImportantEvents() {
        DateTime today = new DateTime(Calendar.getInstance());
        String result = "";
        if (mTaskList != null) {
            // TaskList for given Plant
            for (String task : mTaskList) {
                result = result
                        + task
                        + "\n";
            }
        }

        if(mAppointmentsList != null) {
            // appointments in the next two days (48h = 2880min = 172.800s = 172.800.000ms)
            for (AppointmentData temp : mAppointmentsList) {
                if ((today.getMillis() + 172800000) >= temp.getDateTime().getMillis()) {
                    String name = temp.getName();

                    if (name != null && !name.equals("")) {
                        result = result
                                + name
                                + " - "
                                + StaticMethods.makeTwoDigits(temp.getDateTime().getHourOfDay())
                                + ":"
                                + StaticMethods.makeTwoDigits(temp.getDateTime().getMinuteOfHour())
                                + "\n";
                    }
                }
            }
        }

        if(mMedicineList != null) {
            // medicine in next 12 hours (12h = 720min = 43.200s = 43.200.000ms)
            for (MedicineData temp : mMedicineList) {
                if ((today.getMillis() + 43200000) >= temp.getDateTime().getMillis()) {
                    String name = temp.getName();
                    if (name != null && !name.equals("")) {
                        result = result
                                + temp.getName()
                                + "\n";
                    }
                }
            }
        }
        return result;
    }

    // creates ArrayLists for passed ArrayList of Events from Google Calendar API
    public void createEventDataLists(List<Event> googleEventsList) {
        mMedicineList = new ArrayList<>();
        mAppointmentsList = new ArrayList<>();

        for (Event event : googleEventsList) {
            try {
                if (event.getColorId().equals(StaticValues.COLOR_MEDICINE_EVENTS)) {
                    MedicineData data = new MedicineData();
                    data.setEventId(event.getId());
                    data.setName(event.getSummary());

                    // get Repeat from Description, if one is saved
    //                String desc = event.getLocation();
    //                if (desc.contains(StaticValues.GOOGLE_CALENDAR_REPEAT)) {
    //                    int startRepeat = desc.indexOf(StaticValues.GOOGLE_CALENDAR_REPEAT);
    //                    data.setRepeat(desc.substring(startRepeat + 6));
    //                    data.setInfo(desc.substring(0, startRepeat));
    //                } else {
    //                    data.setInfo(desc);
    //                }
                    data.setInfo(event.getLocation());

                    // find value for frequency ("FREQ")
                    String stringRecurrence =  event.getRecurrence().get(0).toString();
                    String result = stringRecurrence.substring(stringRecurrence.indexOf("FREQ=") + 5);
                    if (result.contains(";")) {
                        result = result.substring(0, result.indexOf(";"));
                    }
                    data.setInterval(result);
                    data.setDateTime(new DateTime(event.getStart().getDateTime().getValue()));
                    mMedicineList.add(data);

                } else if (event.getColorId().equals(StaticValues.COLOR_APPOINTMENT_EVENTS)) {
                    AppointmentData data = new AppointmentData();
                    data.setEventId(event.getId());
                    data.setName(event.getSummary());
                    data.setDateTime(new DateTime(event.getStart().getDateTime().getValue()));

                    // Doku: Location muss genommen werden, weil OpenHab Description für Befehle braucht!
                    String desc = event.getLocation();
                    if (desc.contains(StaticValues.GOOGLE_CALENDAR_LEAD_TIME)) {
                        // get leadtime from location, if one is saved
                        int startLeadTime = desc.indexOf(StaticValues.GOOGLE_CALENDAR_LEAD_TIME);
                        data.setLeadTime(desc.substring(startLeadTime + 6));
                        data.setInfo(desc.substring(0, startLeadTime));

                        // lead time has to be subtracted from start time when getting an event  from the google calendar
                        // reason: the gcal binding for the openHab ignores the lead time and just uses the start time!
                        data.setDateTime(
                                data.getDateTime().plusMinutes(Integer.valueOf(data.getLeadTime())));

                    } else {
                        data.setInfo(desc);
                    }

                    mAppointmentsList.add(data);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CustomApplication", "Event from GCal is neither an medicine event nor an Appointment");
            }
        }

        // create headings in the lists
        updateMedicineListHeadings();
        updateAppointmentsListHeadings();
    }

    // saves passed event in Google Calendar
    public void saveEventInGoogleCalendar(ApiCallbackInterface activity, Event event) {
        // get current notification settings and add them to the event
        String desc = "";
        if (mReminderAudio) {
            desc += GCAL_BINDING_SOUND;
        }
        if (mReminderLight) {
            if (!desc.equals("")) {
                desc += "\n";
            }
            desc += GCAL_BINDING_LIGHT;
        }
        event.setDescription(desc);

        if (event.getId() == null) {
            new SaveAsyncTask(activity, event)
                    .execute();
        } else {
            if (activity instanceof ApiCallbackInterface) {
                new UpdateAsyncTask(activity, event)
                        .execute();
            } else {
                new UpdateAsyncTask((Context) activity, event)
                        .execute();
            }
        }
    }

    // deletes passed event from Google Calendar
    public void deleteEventFromGoogleCalendar(ApiCallbackInterface activity, String eventId) {
        new DeleteAsyncTask(activity, eventId)
                .execute();
    }


    // --------------------------------------
    // -------------- SETTINGS --------------
    // --------------------------------------
    // loads settings from SharedPreferences
    private void loadPrefs() {
        SharedPreferences settings = getSharedPreferences(StaticValues.PREFS_APP_NAME, 0);
        mAppInterface = settings.getInt(StaticValues.PREFS_INTERFACE_TYPE, StaticValues.DEFAULT_INTERFACE);
        mAppTextsize = settings.getString(StaticValues.PREFS_TEXTSIZE, StaticValues.TEXTSIZE_MEDIUM);
        mAppLanguage = settings.getString(StaticValues.PREFS_LANGUAGE, StaticValues.LANGUAGE_DE);
        mReminderAudio = settings.getBoolean(StaticValues.PREFS_REMINDER_AUDIO, false);
        mReminderLight = settings.getBoolean(StaticValues.PREFS_REMINDER_LIGHT, false);
        mAccountPwd = settings.getString(StaticValues.PREFS_PASSWORD, getResources().getString(R.string.account_pwd));
        mAccountMail = settings.getString(StaticValues.PREFS_USER_NAME, getResources().getString(R.string.account_mail));

        // load names of already saved events for AutoCompleteTextViews
        int size = settings.getInt("autoCompleteArray_size", 0);
        mAutoCompleteArray = new String[size];
        for (int i=0; i<size; i++) {
            mAutoCompleteArray[i] = settings.getString("autoCompleteData_" + i, null);
        }
    }

    private void saveInAutoCompleteArray(String string) {
        SharedPreferences settings = getSharedPreferences(StaticValues.PREFS_APP_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("autoCompleteData_" + mAutoCompleteArray.length, string);
        editor.putInt("autoCompleteArray_size", mAutoCompleteArray.length+1);
        editor.commit();

        // update mAutoCompleteArray
        loadPrefs();
    }

    // saves the given interface type in the local storage of the device
    public void saveInterfaceType(int interfaceType) {
        SharedPreferences settings = getSharedPreferences(StaticValues.PREFS_APP_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(StaticValues.PREFS_INTERFACE_TYPE, interfaceType);
        editor.commit();
    }

    // saves the given settings in the local storage of the device
    public void saveSettings(String language, String textsize,
                             boolean reminderSound, boolean reminderLight) {
        SharedPreferences settings = getSharedPreferences(StaticValues.PREFS_APP_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(StaticValues.PREFS_LANGUAGE, language);
        editor.putString(StaticValues.PREFS_TEXTSIZE, textsize);
        editor.putBoolean(StaticValues.PREFS_REMINDER_AUDIO, reminderSound);
        editor.putBoolean(StaticValues.PREFS_REMINDER_LIGHT, reminderLight);
        editor.commit();

        // update GCal-binding info for every EventData
        String desc = "";
        if (reminderSound) {
            desc += GCAL_BINDING_SOUND;
        }
        if (reminderLight) {
            if (!desc.equals("")) {
                desc += "\n";
            }
            desc += GCAL_BINDING_LIGHT;
        }

        for (AppointmentData data : mAppointmentsList) {
            data.setGcal_binding(desc);
            new UpdateAsyncTask(this, StaticMethods.convertAppointmentDataToEvent(data))
                    .execute();
        }

        for (MedicineData data : mMedicineList) {
            data.setGcal_binding(desc);
            new UpdateAsyncTask(this, StaticMethods.convertMedicineDataToEvent(this, data))
                    .execute();
        }

    }

    // returns the currently chosen textsize
    public float getCurrentTextsize() {
        loadPrefs();

        float textsize = 0;
        switch (mAppTextsize) {
            case StaticValues.TEXTSIZE_SMALL:
                textsize = getResources().getDimension(R.dimen.text_size_small);
                break;
            case StaticValues.TEXTSIZE_MEDIUM:
                textsize = getResources().getDimension(R.dimen.text_size_medium);
                break;
            case StaticValues.TEXTSIZE_BIG:
                textsize = getResources().getDimension(R.dimen.text_size_big);
                break;
        }

        return textsize;
    }

    public void showSettingsDialog(Context context, FragmentManager fm, String stackName) {
        // get current language
        Resources res = getResources();
        Configuration conf = res.getConfiguration();

        SettingsDialog.newInstance(
                (SettingsDialogInterface) context,
                conf.locale.getLanguage(),
                mAppTextsize, mReminderAudio, mReminderLight)
                .show(fm, stackName);
    }

    public void showAccountsDialog( FragmentManager fm, String stackName){
        AccountDialog.newInstance(
                mAccountMail,
                mAccountPwd)
                .show(fm, stackName);
    }

    public void showAuthDialog( FragmentManager fm, String stackName){
        AuthenticateDialog.newInstance(mAccountPwd).show(fm, stackName);
    }

    // sets the in-app language to the given one
    public void changeCurrentLocale(String lang, Context context, Class currentActivity) {
        Locale locale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);

        Intent intent = new Intent(context, currentActivity);
        context.startActivity(intent);
    }


    // --------------------------------------
    // ------------ APPOINTMENTS ------------
    // --------------------------------------
    // saves the given AppointmentData in list
    public void saveAppointmentData(AppointmentData appointmentData) {
        // if everything is correct then insert medicine
        mAppointmentsList.add(appointmentData);

        // save values for AutoCompleteTextViews
        saveInAutoCompleteArray(appointmentData.getName());

        // update headings if necessary
        updateAppointmentsListHeadings();
    }

    // updates headings of mAppointmentsList
    public boolean updateAppointmentData() {
        // update headings if necessary
        updateAppointmentsListHeadings();
        return true;
    }

        // deletes the given AppointmentData from list
    public void deleteAppointmentData(int pos) {
        // if everything is correct then insert medicine
        mAppointmentsList.remove(pos);

        // update headings if necessary
        updateAppointmentsListHeadings();
    }

    // sorts mAppointmentsList and iterates over it to add headings where they are need
    private void updateAppointmentsListHeadings() {
        // order list elements depending on their DateTime
        Collections.sort(mAppointmentsList, new AppointmentDataComparator());

        // delete all headers of ArrayList
        for (int i = 0; i < mAppointmentsList.size(); i++) {
            if(mAppointmentsList.get(i).isHeader()){
                mAppointmentsList.remove(i);
                i--;
            }
        }

        // first position has to be a heading
       if (mAppointmentsList.size() > 0 ) {
            mAppointmentsList.add(
                0,
                new AppointmentData(mAppointmentsList.get(0).getDateTime(), "", 0, "", true));
        }

        // compare objects in ArrayList in pairs and insert header on differ
        for (int i = 0; i < mAppointmentsList.size()-1; i++) {
            AppointmentData temp = mAppointmentsList.get(i);

            // add Header if LocalDate of following Object is different
            if (!temp.getDateTime().toLocalDate().equals(mAppointmentsList.get(i + 1).getDateTime().toLocalDate())) {
                mAppointmentsList.add(
                        i + 1,
                        new AppointmentData(mAppointmentsList.get(i + 1).getDateTime(), "", 0, "", true));
            }
        }
    }


    // --------------------------------------
    // -------------- MEDICINE --------------
    // --------------------------------------
    // saves the passed MedicineData in list
    public boolean saveMedicineData(MedicineData medicineData) {
        // check if MedicineData has correct Fields
        if(medicineData == null)
            return false;
        if(medicineData.getInterval() == null || medicineData.getInterval() == "")
            return false;
        if(medicineData.getInfo() == null || medicineData.getInfo() == "")
            return false;
        if(medicineData.getName() == null || medicineData.getName() == "")
            return false;
        if(medicineData.getSectionFirstPosition()>65534 || medicineData.getSectionFirstPosition()<0)
            return false;

        // if everything is correct then insert medicine
        mMedicineList.add(medicineData);

        // save values for AutoCompleteTextViews
        saveInAutoCompleteArray(medicineData.getName());

        // update headings if necessary
        updateMedicineListHeadings();

        return true;
    }

    // updates headings of mMedicineList
    public boolean updateMedicineData() {
        // update headings if necessary
        updateMedicineListHeadings();

        return true;
    }

    // deletes the passed MedicineData from mMedicineList
    public boolean deleteMedicineData(int position) {
        // Check if the list is not empty
        if (mMedicineList.size() < 0) {
            return false;
        }

        mMedicineList.remove(position);

        // update headings if necessary
        updateMedicineListHeadings();

        return true;
    }

    // sorts mMedicineList and iterates over it to add headings where they are need
    private void updateMedicineListHeadings() {
        // order list elements depending on their Interval
        Collections.sort(mMedicineList, new MedicineDataComparator());

        // delete all headers of ArrayList
        for (int i = 0; i < mMedicineList.size(); i++) {
            if(mMedicineList.get(i).isHeader()){
                mMedicineList.remove(i);
                i--;
            }
        }

        // first position has to be a heading
        if (mMedicineList.size() > 0 ) {
            mMedicineList.add(
                    0,
                    new MedicineData(mMedicineList.get(0).getInterval(), true));
        }

        // compare objects in ArrayList in pairs and insert header on differ
        for (int i = 0; i < mMedicineList.size()-1; i++) {
            MedicineData temp = mMedicineList.get(i);

            //Next Object with different Interval
            if (!temp.getInterval().equalsIgnoreCase(mMedicineList.get(i + 1).getInterval())) {
                //Add new Header to Arraylist
                mMedicineList.add(
                        i + 1,
                        new MedicineData(mMedicineList.get(i + 1).getInterval(), true));
            }
        }
    }

    // --------------------------------------
    // --------------- PLANTS ---------------
    // --------------------------------------
    public void loadKoubachiDeviceInfo(String temperature, String soil, String plantName) {
            mPlantsList.add(new PlantData(
                    plantName,
                    temperature, StaticMethods.classifyTemperature(temperature),
                    soil, StaticMethods.classifySoil(soil),
                    "", 0
            ));
        String user_creds = getResources().getString(R.string.koubachi_user_credentials);
        String app_key = getResources().getString(R.string.koubachi_app_key);
        mServerCommunication.getPlantInfo(user_creds, app_key, this);

    }

    public void loadPlantinfo(String illuminance, String plantName){
            PlantData data = mPlantsList.get(0);
            data.setName(plantName);
            data.setLight(illuminance);
            data.setLightStatus(StaticMethods.classifyIlluminance(illuminance));
    }

    public void loadTasks(ArrayList<String> list){
        StaticMethods.formatTaskString(list, getApplicationContext());
        this.mTaskList = list;
    }
}