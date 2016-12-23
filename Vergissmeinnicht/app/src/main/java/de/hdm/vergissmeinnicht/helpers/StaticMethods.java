package de.hdm.vergissmeinnicht.helpers;

import android.content.Context;
import android.widget.TextView;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.model.AppointmentData;
import de.hdm.vergissmeinnicht.model.MedicineData;

/**
 * Created by Dennis Jonietz on 12.04.2015.
 */
public class StaticMethods<T> {

    // adds a 0 to numbers with only one digit
    public static String makeTwoDigits(int number) {
        String result;

        if (number < 10) {
            result = "0" + number;
        } else {
            result = String.valueOf(number);
        }
        return result;
    }

    public static Integer[] castInToGeneric(int[] obj) {
        Integer[] integerArray = new Integer[obj.length];
        for (int i = 0; i < obj.length; ++i) integerArray[i] = new Integer(obj[i]);
        return integerArray;
    }

    public static ArrayList<AppointmentData> createAppointmentDummyData(int dataLength) {
        ArrayList<AppointmentData> data = new ArrayList<AppointmentData>();
        DateTime dateTime = new DateTime();
        // create heading
        data.add(new AppointmentData(
                dateTime,
                "",
                0, "", true
        ));

        for (int i = 0; i < dataLength; i++) {
            data.add(new AppointmentData(
                    dateTime,
                    "Event " + (i + 1),
                    0, "15", false));
        }
        return data;
    }

    //Initially set Daily, Weekly, Monthly Header
    public static ArrayList<MedicineData> getMedicineDummydata() {
        ArrayList<MedicineData> data = new ArrayList<MedicineData>();
        return data;
    }

    public static String createDateHeader(DateTime dateTime) {
        return dateTime.dayOfWeek().getAsShortText(Locale.getDefault()) + ", " +
                dateTime.getDayOfMonth() + "." +
                dateTime.getMonthOfYear() + "." +
                dateTime.getYear();
    }

    // returns color id for given PLANT_STATUS
    public static int giveColorForStatus(int status) {
        int colorId = 0;

        switch (status) {
            case StaticValues.PLANT_STATUS_GOOD:
                colorId = R.color.default_green;
                break;
            default:
                colorId = R.color.default_yellow;
                break;
            case StaticValues.PLANT_STATUS_BAD:
                colorId = R.color.default_red;
                break;
        }
        return colorId;
    }

    // makes given textview to senior interface style
    public static void makeSeniorInterfaceStyle(Context context, TextView textView) {
        textView.setTextColor(context.getResources().getColor(R.color.default_white));
        textView.setBackgroundResource(R.drawable.bg_senior_button);
        textView.setPadding(10, 10, 10, 10);
    }

    // converts passed AppointmentData to a Google Calendar API Event
    public static Event convertAppointmentDataToEvent(AppointmentData data) {
        Event event = new Event()
                .setId(data.getEventId())
                .setSummary(data.getInfo())
                .setDescription(data.getGcal_binding())
                .setLocation(
                        data.getInfo()
                                + StaticValues.GOOGLE_CALENDAR_LEAD_TIME
                                + data.getLeadTime());

        // lead time has to be subtracted from start time when saving an event to the google calendar
        // reason: the gcal binding for the openHab ignores the lead time and just uses the start time!
        DateTime startDateTime = null;
        try {
            int leadTime = Integer.valueOf(data.getLeadTime());
            startDateTime = data.getDateTime().minusMinutes(leadTime);

        } catch (Exception e) {
            startDateTime = data.getDateTime();
        }
        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDateTime.getMillis()))
                .setTimeZone("Europe/Berlin");
        event.setStart(start);

        // Adding Lead Time
        try {
            EventReminder[] reminderOverrides = new EventReminder[]{
                    new EventReminder().setMethod("popup").setMinutes(Integer.parseInt(data.getLeadTime())),
            };

            Event.Reminders reminders = new Event.Reminders()
                    .setUseDefault(false)
                    .setOverrides(Arrays.asList(reminderOverrides));
            event.setReminders(reminders);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // make appointment last 1min = 60sec = 60.000ms
        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDateTime.getMillis() + 60000))
                .setTimeZone("Europe/Berlin");
        event.setEnd(end);
        event.setColorId(StaticValues.COLOR_APPOINTMENT_EVENTS);

        return event;
    }

    // converts passed MedicineData to a Google Calendar API Event
    public static Event convertMedicineDataToEvent(Context context, MedicineData data) {
        Event event = new Event()
                .setId(data.getEventId())
                .setDescription(data.getGcal_binding())
                .setSummary(data.getName());

        event.setLocation(data.getInfo());

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(data.getDateTime().getMillis()))
                .setTimeZone("Europe/Berlin");
        event.setStart(start);

        // make medicine last 1min = 60sec = 60.000ms
        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(start.getDateTime().getValue() + 60000))
                .setTimeZone("Europe/Berlin");
        event.setEnd(end);

        String recurrence = "RRULE:";
        if (!data.getInterval().equals("")) {
            recurrence = recurrence + "FREQ=" + translateToEnglish(data.getInterval());
        }
        event.setRecurrence(Arrays.asList(new String[]{recurrence.toString()}));

        event.setColorId(StaticValues.COLOR_MEDICINE_EVENTS);

        return event;
    }

    // translates passed text to english frequency (for Google Calendar API)
    public static String translateToEnglish(String string) {
               String result = "";
                if (string.equalsIgnoreCase("weekly") || string.equalsIgnoreCase("wöchentlich")) {
                        result = "WEEKLY";
                    } else if (string.equalsIgnoreCase("daily") || string.equalsIgnoreCase("täglich")) {
                        result = "DAILY";
                    } else {
                        result = "MONTHLY";
                    }
                return result;

            }

    // converts passed String into repeat (int)
    private static int convertRepeatToInt(Context context, String repeat) {
        if (repeat.equals(context.getResources().getString(R.string.medicine_add_data_repeat_single))) {
            return 1;
        } else if (repeat.equals(context.getResources().getString(R.string.medicine_add_data_repeat_twice))) {
            return 2;
        } else if (repeat.equals(context.getResources().getString(R.string.medicine_add_data_repeat_third))) {
            return 3;
        } else {
            return 0;
        }
    }

    // converts passed int into repeat (String)
    public static String convertRepeatToString(Context context, String repeat) {
        switch (Integer.valueOf(repeat)) {
            case 1:
                return context.getResources().getString(R.string.medicine_add_data_repeat_single);
            case 2:
                return context.getResources().getString(R.string.medicine_add_data_repeat_twice);
            case 3:
                return context.getResources().getString(R.string.medicine_add_data_repeat_third);
            default:
                return "";
        }
    }

    public static Object handleSensorResult(JSONObject list, String key) throws JSONException {
        Object data = list.get(key);
        return data;
    }

    public static String toPercentage(Double value) {
        String percentage = "";
        value *= 100;
        if (value != 0.0D) {
            percentage = value.toString().substring(0, 4);
        } else {
            percentage = "0.0";
        }
        return percentage;
    }

    public static int classifyTemperature(String value) {
        String subString = value.substring(0, value.indexOf("°"));
        Float temp = Float.parseFloat(subString.replaceAll("\\s", ""));
        if (temp > 30) {
            return StaticValues.PLANT_STATUS_BAD;
        } else if (20 < temp && temp < 30) {
            return StaticValues.PLANT_STATUS_GOOD;
        } else if (15 <= temp && temp < 20) {
            return StaticValues.PLANT_STATUS_OK;
        } else {
            return StaticValues.PLANT_STATUS_BAD;
        }
    }

    public static int classifySoil(String value) {
        if (value.length() > 0) {
            float soil = Float.parseFloat(value.substring(0, value.indexOf("%")));
            if (soil > 85F) {
                return StaticValues.PLANT_STATUS_BAD;
            } else if (35F < soil && soil < 85F) {
                return StaticValues.PLANT_STATUS_GOOD;
            } else if (10F <= soil && soil < 35F) {
                return StaticValues.PLANT_STATUS_OK;
            } else {
                return StaticValues.PLANT_STATUS_BAD;
            }
        } else {
            return 1;
        }
    }

    public static int classifyIlluminance(String value) {
        double lum = Double.parseDouble(value);
        if (lum > 75D) {
            return StaticValues.PLANT_STATUS_BAD;
        } else if (40D < lum && lum < 75D) {
            return StaticValues.PLANT_STATUS_GOOD;
        } else if (35D < lum && lum < 40D) {
            return StaticValues.PLANT_STATUS_OK;
        } else {
            return StaticValues.PLANT_STATUS_BAD;
        }

    }

    public static ArrayList<String> formatTaskString(ArrayList<String> list, Context context) {
        for (String task : list) {
            switch (task) {
                case "water_plant":
                    list.set(list.indexOf("water_plant"), context.getResources().getString(R.string.water_plant));
                    break;

                case "mist_plant":
                    list.set(list.indexOf("mist_plant"), context.getResources().getString(R.string.mist_plant));
                    break;

                case "put_into_light":
                    list.set(list.indexOf("put_into_light"), context.getResources().getString(R.string.put_into_light));
                    break;

                case "fertilize_plant":
                    list.set(list.indexOf("fertilize_plant"), context.getResources().getString(R.string.fertilize_plant));
                    break;
            }
        }
        return list;
    }


}