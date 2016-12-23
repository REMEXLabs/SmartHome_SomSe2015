package de.hdm.vergissmeinnicht.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.activities.SeniorAppointmentsActivity;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.model.AppointmentData;

/**
 * Created by Dennis Jonietz on 4/12/15.
 */
public class SeniorAddAppointmentDataFragment extends SeniorCustomFragment {

    private static final String BUNDLE_EVENT_DATA = "eventData";

    @Bind(R.id.edit_medicine_info)      AutoCompleteTextView mInfoEdit;
    @Bind(R.id.picker_date)             DatePicker mDatePicker;
    @Bind(R.id.time_picker)             TimePicker mTimePicker;
    @Bind(R.id.spinner_lead_time)       Spinner mLeadTimer;
    @Bind(R.id.txt_weekday)             TextView mWeekDayTxt;

    private AppointmentData mAppointmentData;
    private String mInfoString, mLeadTime;
    private DateTime mDateTime;

    // generates a new instance of the SeniorAddEventFragment using the default empty constructor
    public static final SeniorAddAppointmentDataFragment newInstance(AppointmentData appointmentData) {
        SeniorAddAppointmentDataFragment fragment = new SeniorAddAppointmentDataFragment();

        final Bundle args = new Bundle(1);
        args.putParcelable(BUNDLE_EVENT_DATA, appointmentData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppointmentData = getArguments().getParcelable(BUNDLE_EVENT_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, R.layout.fragment_senior_add_appointment_data);

        // initialize Butter Knife
        ButterKnife.bind(this, view);

        // add data to AutocompleteTextview
        String[] complete = ((CustomApplication) getActivity().getApplication()).getAutoCompleteArray();
        if (complete != null && complete.length>0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    complete);
//            mInfoEdit.setAdapter(adapter);
        }

        // lead time spinner
        int[] intArray = getResources().getIntArray(R.array.fragment_add_event_lead_time);
        setAdapter(getActivity(), StaticMethods.castInToGeneric(intArray), mLeadTimer);

        // update event, so fill in data of current EventData
        if (mAppointmentData != null) {
            mDateTime = mAppointmentData.getDateTime();
            mInfoEdit.setText(mAppointmentData.getInfo());
        } else {
            mDateTime = new DateTime();
        }

        // setup TextView for day of week initially
        updateWeekDayTextView(
                mDateTime.getYear(),
                mDateTime.getMonthOfYear(),
                mDateTime.getDayOfMonth());

        // set TimePicker to 24h-format
        mTimePicker.setIs24HourView(true);

        // set Listener on DatePicker
        mDatePicker.init(
                mDateTime.getYear(),
                mDateTime.getMonthOfYear() - 1,
                mDateTime.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        updateWeekDayTextView(year, month, day);
                    }
                }
        );

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void updateWeekDayTextView(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        mWeekDayTxt.setText(cal.getDisplayName(
                Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
    }

    // creates an EventData object from the data filled in by the user
    public AppointmentData getDataOfEvent() {
        getDataOfViews();
        return new AppointmentData(
                mDateTime,
                mInfoString,
                0,
                mLeadTime,
                false);
    }

    public void updateEventData() {
        getDataOfViews();
        mAppointmentData.setInfo(mInfoString);
        mAppointmentData.setLeadTime(mLeadTime);
        mAppointmentData.setDateTime(mDateTime);
        ((SeniorAppointmentsActivity) getActivity()).backToOverView();
    }

    public void getDataOfViews(){
        // get date from DatePicker
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth() + 1; // DatePicker is zero-based, e.g. Jan = 0, Feb = 1
        int day = mDatePicker.getDayOfMonth();

        // get time from TimePicker
        int hour = mTimePicker.getCurrentHour();
        int minute = mTimePicker.getCurrentMinute();

        mDateTime = new DateTime(year, month, day, hour, minute);

        // get info from EditTexts
        mInfoString = mInfoEdit.getText().toString();
        mLeadTime =  mLeadTimer.getSelectedItem().toString();
    }

    // checks if the data in the input fields is correctly filled in
    public boolean isDataCorrect() {
        if (mInfoEdit.getText().toString().equals("")) {
            // no info filled in
            Toast.makeText(
                    getActivity(),
                    getString(R.string.fragment_add_appointment_data_missing_info),
                    Toast.LENGTH_SHORT
            ).show();
            return false;

        } else {
            return true;
        }
    }

    // is called when save button is clicked
    @OnTouch(R.id.btn_save_event)
    protected boolean onSaveButtonTouched(View v, MotionEvent evt) {
        switch (evt.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                v.setScaleX(StaticValues.SCALE_PRESSED);
                v.setScaleY(StaticValues.SCALE_PRESSED);
                break;

            case MotionEvent.ACTION_UP:
                v.setScaleX(StaticValues.SCALE_RELEASED);
                v.setScaleY(StaticValues.SCALE_RELEASED);

                // save date of event
                if (isDataCorrect() && !StaticValues.updateFlag) {
                    mAppointmentData = getDataOfEvent();
                    ((SeniorAppointmentsActivity) getActivity()).addEventData(mAppointmentData);

                } else if(StaticValues.updateFlag == true) {
                    updateEventData();
                    ((SeniorAppointmentsActivity) getActivity()).addEventData(mAppointmentData);
                }

                break;
        }
        return true;
    }

}