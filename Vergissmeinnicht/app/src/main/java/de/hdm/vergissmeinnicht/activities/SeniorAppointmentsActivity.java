package de.hdm.vergissmeinnicht.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;

import butterknife.Bind;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.adapters.SeniorAppointmentsViewAdapter;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.dialogs.SeniorDeleteEventDialog;
import de.hdm.vergissmeinnicht.fragments.SeniorAddAppointmentDataFragment;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.interfaces.ApiCallbackInterface;
import de.hdm.vergissmeinnicht.interfaces.CategoryBtnInterface;
import de.hdm.vergissmeinnicht.interfaces.DataHolderClickInterface;
import de.hdm.vergissmeinnicht.interfaces.SeniorDeleteDialogInterface;
import de.hdm.vergissmeinnicht.model.AppointmentData;
import de.hdm.vergissmeinnicht.model.CustomData;

public class SeniorAppointmentsActivity extends SeniorCustomActivity implements CategoryBtnInterface,
        DataHolderClickInterface, SeniorDeleteDialogInterface, ApiCallbackInterface {

    private static final String STACKNAME_DELETE_DIALOG = "deleteDialog";

    @Bind(R.id.recycler_view)   RecyclerView mRecyclerView;

    private SeniorAppointmentsViewAdapter mRecyclerAdapter;
    private ArrayList<AppointmentData> mAppointmentsList;
    private CustomApplication mApplication;
    private AppointmentData mCurrentlyHandledAppointmentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(
                savedInstanceState,
                R.layout.activity_senior_appointments,
                this);

        mApplication = (CustomApplication) getApplication();
        mAppointmentsList = mApplication.getAppointmentsList();

        setUpBtns(this);
        setUpRecyclerView();

    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected ArrayList<AppointmentData> getAppointmentsList() {
        return mAppointmentsList;
    }

    // sets up the RecyclerView and the RecyclerAdapter for it
    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LayoutManager(this));
        float currentTextsize = ((CustomApplication) getApplication()).getCurrentTextsize();
        mRecyclerAdapter = new SeniorAppointmentsViewAdapter(this, mAppointmentsList, mRecyclerView, currentTextsize - 10);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    // CategoryTouchListener: is called when add button is clicked
    public void addBtnClicked() {
        StaticValues.updateFlag = false;
        // show AddEventDataFragment to add new event
        showAddDataFragment(
                SeniorAddAppointmentDataFragment.newInstance(null),
                StaticValues.FRAGMENT_SENIOR_ADD_APPOINTMENT_DATA
        );
    }

    public void addEventData(AppointmentData appointmentData) {
        mApplication.saveEventInGoogleCalendar(
                this,
                StaticMethods.convertAppointmentDataToEvent(appointmentData));
        mCurrentlyHandledAppointmentData = appointmentData;
        backToOverView();
    }

    // EventHolderClickInterface: is called by EventViewHolder to update event
    @Override
    public void updateBtnClicked(CustomData customData) {
        if (mIsInEditMode && customData instanceof AppointmentData) {
            switchEditMode(false);
            mAddBtn.setTextOnBtn(getResources().getString(R.string.btn_navigation_save));

            // show AddEventDataFragment to mUpdate the given event
            super.showAddDataFragment(
                    SeniorAddAppointmentDataFragment.newInstance((AppointmentData) customData),
                    StaticValues.FRAGMENT_SENIOR_ADD_APPOINTMENT_DATA
            );
        }
    }

    // EventHolderClickInterface: is called by EventViewHolder to delete event
    @Override
    public void deleteBtnClicked(CustomData customData) {
        if (mIsInEditMode) {
            SeniorDeleteEventDialog.newInstance(SeniorAppointmentsActivity.this, customData, R.color.default_red)
                .show(getFragmentManager(), STACKNAME_DELETE_DIALOG);
        }
    }

    public void deleteEventCommited(CustomData customData) {
        if (customData instanceof AppointmentData) {
            switchEditMode(false);
            mApplication.deleteEventFromGoogleCalendar(this, customData.getEventId());
            mCurrentlyHandledAppointmentData = (AppointmentData) customData;
        }
    }

    public void backToOverView(){
        mRecyclerAdapter.notifyDataSetChanged();
        mFragmentManager.popBackStack();
        resetAllBtns();
    }

    @Override
    public void saveEventCallback(boolean result, String eventId) {
        if (result) {
            // save MedicineData locally after its successfully saved in Google Calendar
            mCurrentlyHandledAppointmentData.setEventId(eventId);
            mApplication.saveAppointmentData(mCurrentlyHandledAppointmentData);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerAdapter.notifyDataSetChanged();
                    Toast.makeText(
                            SeniorAppointmentsActivity.this,
                            getResources().getString(R.string.toast_success_save_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

        } else {
            mCurrentlyHandledAppointmentData = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            SeniorAppointmentsActivity.this,
                            getResources().getString(R.string.toast_error_save_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    @Override
    public void updateEventCallback(boolean result) {
        if (result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerAdapter.notifyDataSetChanged();
                    mApplication.updateAppointmentData();
                    Toast.makeText(
                            SeniorAppointmentsActivity.this,
                            getResources().getString(R.string.toast_success_update_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            SeniorAppointmentsActivity.this,
                            getResources().getString(R.string.toast_error_update_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    @Override
    public void deleteEventCallback(boolean result) {
        if (result) {
            // delete MedicineData locally after its successfully deleted from Google Calendar

            mApplication.deleteAppointmentData(mAppointmentsList.indexOf(mCurrentlyHandledAppointmentData));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerAdapter.notifyDataSetChanged();
                    Toast.makeText(
                            SeniorAppointmentsActivity.this,
                            getResources().getString(R.string.toast_success_delete_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            SeniorAppointmentsActivity.this,
                            getResources().getString(R.string.toast_error_delete_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

}