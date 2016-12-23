package de.hdm.vergissmeinnicht.helpers;

/**
 * Created by Dennis Jonietz on 12.04.2015.
 */
public class StaticValues {

    // Senior interface Fragment stack names
    public static final String FRAGMENT_SENIOR_ADD_APPOINTMENT_DATA = "fragmentSeniorAddAppointmentData";
    public static final String FRAGMENT_SENIOR_ADD_MEDICINE_DATA = "fragmentSeniorAddMedicineData";

    // Default interface Fragment stack names
    public static final String FRAGMENT_DEFAULT_ADD_APPOINTMENT_DATA = "fragmentDefaultAddAppointmentData";
    public static final String FRAGMENT_DEFAULT_ADD_MEDICINE_DATA = "fragmentDefaultAddMedicineData";

    // Application Settings Keys
    public static final String PREFS_APP_NAME = "vergissMeinNichtPrefs",
                               PREFS_USER_NAME = "username",
                               PREFS_PASSWORD = "password",
                               PREFS_INTERFACE_TYPE = "interfaceType",
                               PREFS_LANGUAGE = "language",
                               PREFS_TEXTSIZE = "textsize",
                               PREFS_REMINDER_AUDIO = "reminderSound",
                               PREFS_REMINDER_LIGHT = "reminderLight";

    // Colors for the events
    public static String COLOR_MEDICINE_EVENTS = "6";
    public static String COLOR_APPOINTMENT_EVENTS = "11";

    // Interface types
    public static final int DEFAULT_INTERFACE = 0;
    public static final int SENIOR_INTERFACE = 1;

    // Textsizes
    public static final String TEXTSIZE_SMALL = "small";
    public static final String TEXTSIZE_MEDIUM = "medium";
    public static final String TEXTSIZE_BIG = "big";

    // LanguageOptions
    public static final String LANGUAGE_DE = "de";
    public static final String LANGUAGE_EN = "en";

    // AddDataFragment types
    public static final int APPOINTMENT_TYPE = 1;
    public static final int MEDICINE_TYPE = 2;

    // Status categories for plants
    public static final int PLANT_STATUS_GOOD = 1;
    public static final int PLANT_STATUS_OK = 2;
    public static final int PLANT_STATUS_BAD = 3;

    // Button state
    public static final float ALPHA_DISABLED = 0.3f;
    public static final float ALPHA_ENABLED = 1.0f;
    public static final float SCALE_PRESSED = 0.95f;
    public static final float SCALE_RELEASED = 1f;

    // Constant for Google Calendar API
    public static final String GOOGLE_CALENDAR_LEAD_TIME = " | -: ";

    public static boolean updateFlag = false;

    // Credits:
    // Icons made by Freepik (http://www.freepik.com) hosted on Flaticon (http://www.flaticon.com) and is licensed by CC BY 3.0 (http://creativecommons.org/licenses/by/3.0)

}