package de.hdm.vergissmeinnicht.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.tonicartos.superslim.LayoutManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.adapters.DefaultAppointmentsAdapter;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.dialogs.DefaultUpdateDeleteDialog;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.interfaces.ApiCallbackInterface;
import de.hdm.vergissmeinnicht.interfaces.DefaultUpdateDeleteDialogInterface;
import de.hdm.vergissmeinnicht.model.AppointmentData;
import de.hdm.vergissmeinnicht.model.CustomData;

/**
 * Created by Dennis Jonietz on 6/13/15.
 */
public class DefaultAppointmentsFragment extends DefaultCustomFragment implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, DefaultUpdateDeleteDialogInterface, ApiCallbackInterface {

    private static final String STACKNAME_UPDATE_DELETE_DIALOG = "updateDeleteDialog";

    @Bind(R.id.recycler_view)   RecyclerView mRecyclerView;
    @Bind(R.id.fab)             FloatingActionButton mFab;

    private ArrayList<AppointmentData> mAppointmentsList;
    private DefaultAppointmentsAdapter mAppointmentsAdapter;
    private FragmentManager mFragmentManager;
    private DatePickerDialog mDpd;
    private TimePickerDialog mTpd;
    private DateTime mDateTime;
    private LocalTime mLocalTime;
    private Spinner mLeadSpinner;
    private AppointmentData mAppointmentData,mCurrentlyHandledAppointmentData;
    private int mDay, mMonth, mYear, mHour, mMinutes;
    private int[] mLeadArray;
    private boolean mUpdateFlag = false;
    private TextView mTextView;
    private int mItemPos;
    private Activity mParentActivity;

    public static DefaultAppointmentsFragment newInstance() {
        DefaultAppointmentsFragment fragment = new DefaultAppointmentsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAppointmentsList = ((CustomApplication) getActivity().getApplication()).getAppointmentsList();
        mDateTime = new DateTime();
        mLocalTime = new LocalTime();
        mLeadArray = getResources().getIntArray(R.array.fragment_add_event_lead_time);
        mFragmentManager = getFragmentManager();
        mParentActivity = getActivity();

        View view = inflater.inflate(R.layout.fragment_default_appointments, container, false);

        // initialize Butter Knife
        ButterKnife.bind(this, view);

        setUpRecyclerView(view);
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        mDay = day;
        mMonth = convertMonthToJoda(month);
        mYear = year;
        if(mAppointmentData.getDateTime() == null){
            mAppointmentData.setDateTime(new DateTime(year,month, day, 0, 0));
        }
        showTimePicker(mAppointmentData);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minutes) {
        mHour = hour;
        mMinutes = minutes;
        mAppointmentData.setDateTime(new DateTime(mYear, mMonth, mDay, mHour, mMinutes));
        showLeadTimeDialog();
    }

    private void setUpRecyclerView(View view) {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LayoutManager(view.getContext()));
        mAppointmentsAdapter= new DefaultAppointmentsAdapter(this, mAppointmentsList, mRecyclerView);
        mRecyclerView.setAdapter(mAppointmentsAdapter);

        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppointmentData = new AppointmentData();
                showDatePicker(mAppointmentData);
                mUpdateFlag = false;
            }
        });
    }

    public void initializeViews(View view){
        mLeadSpinner = (Spinner) view.findViewById(R.id.spinner_lead_time);
        mTextView = (TextView) view.findViewById(R.id.edit_medicine_info);
        // add data to AutocompleteTextview
        String[] complete = ((CustomApplication) getActivity().getApplication()).getAutoCompleteArray();
        if (complete != null && complete.length>0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    complete);
//            ((AutoCompleteTextView) mTextView).setAdapter(adapter);
        }
        setViewUpdateValues();
    }

    public void showDatePicker(AppointmentData data){
        if (data.getDateTime() == null) {
            mDpd = DatePickerDialog.newInstance(
                    this,
                    mDateTime.getYear(),
                    convertMonthToCalendar(mDateTime.getMonthOfYear()),
                    mDateTime.getDayOfMonth()
            );
        } else {
            mAppointmentData = data;
            mDpd = DatePickerDialog.newInstance(
                    this,
                    data.getDateTime().getYear(),
                    convertMonthToCalendar(data.getDateTime().getMonthOfYear()),
                    data.getDateTime().getDayOfMonth()
            );
        }
        mDpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void showTimePicker(AppointmentData data){
        if(data.getDateTime().getHourOfDay() == 0) {
            mTpd = TimePickerDialog.newInstance(
                    this,
                    mLocalTime.getHourOfDay(),
                    mLocalTime.getMinuteOfHour(),
                    false
            );
        }else{
            mTpd = TimePickerDialog.newInstance(
                    this,
                    data.getDateTime().getHourOfDay(),
                    data.getDateTime().getMinuteOfHour(),
                    false
            );
        }
        mTpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showDatePicker(mAppointmentData);
            }
        });
        mTpd.show(getFragmentManager(), "Timepickerdialog");
    }

    public void showLeadTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_appointments_add_dialog, null, false);
        initializeViews(view);
        setAdapter(getActivity(), StaticMethods.castInToGeneric(mLeadArray), mLeadSpinner);
        builder.setView(view)
                .setTitle(getResources().getString(R.string.dialog_title_add_appointment_data))
                .setPositiveButton(R.string.btn_navigation_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mAppointmentData.setLeadTime(mLeadSpinner.getSelectedItem().toString());
                        mAppointmentData.setInfo(mTextView.getText().toString());
                        ((CustomApplication) mParentActivity.getApplication())
                                .saveEventInGoogleCalendar(
                                        DefaultAppointmentsFragment.this,
                                        StaticMethods.convertAppointmentDataToEvent(mAppointmentData));
                    }
                })
                .setNegativeButton(R.string.btn_navigation_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showTimePicker(mAppointmentData);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
    }

    // DefaultAppointmentsAdapter: called on LongClick
    public void showUpdateDeleteDialog(int itemPos) {
        mItemPos = itemPos;
        final AppointmentData data = mAppointmentsList.get(itemPos);
        mCurrentlyHandledAppointmentData = data;

        DefaultUpdateDeleteDialog.newInstance(this, data, R.color.default_red)
                .show(getFragmentManager(), STACKNAME_UPDATE_DELETE_DIALOG);
    }

    public void setViewUpdateValues(){
        if(mUpdateFlag != false){
            mTextView.setText(mAppointmentData.getInfo());
            //TODO set LeadSpinner Value
        }
    }

    public int convertMonthToCalendar(int month){
        return month - 1;
    }

    public int convertMonthToJoda(int month){
        return month + 1;
    }

    // DefaultUpdateDeleteDialogInterface: called by DefaultUpdateDeleteDialog
    @Override
    public void updateEvent(CustomData customData) {
        if (customData instanceof AppointmentData) {
            mUpdateFlag = true;
            showDatePicker((AppointmentData) customData);
        }
    }

    // DefaultUpdateDeleteDialogInterface: called by DefaultUpdateDeleteDialog
    @Override
    public void deleteEvent(CustomData customData) {
        if (customData instanceof AppointmentData) {
            ((CustomApplication) getActivity().getApplication())
                    .deleteEventFromGoogleCalendar(this, customData.getEventId());
            mAppointmentsAdapter.notifyDataSetChanged();
        }
    }

    // SaveAsyncTask
    @Override
    public void saveEventCallback(boolean result, String eventId) {
        if (result) {
            // save AppointmentData locally after its successfully saved in Google Calendar
            mAppointmentData.setEventId(eventId);
            ((CustomApplication) mParentActivity.getApplication())
                    .saveAppointmentData(mAppointmentData);

            mParentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAppointmentsAdapter.notifyDataSetChanged();
                    Toast.makeText(
                            mParentActivity,
                            getResources().getString(R.string.toast_success_save_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

        } else {
            mAppointmentData = null;
            mParentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            mParentActivity,
                            getResources().getString(R.string.toast_error_save_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    // UpdateAsyncTask
    @Override
    public void updateEventCallback(boolean result) {
        if (result) {
            mParentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAppointmentsAdapter.notifyDataSetChanged();
                    ((CustomApplication) getActivity().getApplication()).updateAppointmentData();
                    Toast.makeText(
                            mParentActivity,
                            getResources().getString(R.string.toast_success_update_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

        } else {
            mParentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            mParentActivity,
                            getResources().getString(R.string.toast_error_update_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    // DeleteAsyncTask
    @Override
    public void deleteEventCallback(boolean result) {
        if (result) {
            // delete AppointmentData locally after its successfully deleted from Google Calendar
            int index = mAppointmentsList.indexOf(mCurrentlyHandledAppointmentData);
            ((CustomApplication) mParentActivity.getApplication())
                    .deleteAppointmentData(mAppointmentsList.indexOf(mCurrentlyHandledAppointmentData));

            mParentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAppointmentsAdapter.notifyDataSetChanged();
                    Toast.makeText(
                            mParentActivity,
                            getResources().getString(R.string.toast_success_delete_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

        } else {
            mParentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            mParentActivity,
                            getResources().getString(R.string.toast_error_delete_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

}