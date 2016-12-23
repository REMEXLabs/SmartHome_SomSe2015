package de.hdm.vergissmeinnicht.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;

import butterknife.Bind;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.adapters.SeniorMedicineViewAdapter;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.dialogs.SeniorDeleteEventDialog;
import de.hdm.vergissmeinnicht.fragments.SeniorAddMedicineDataFragment;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.interfaces.ApiCallbackInterface;
import de.hdm.vergissmeinnicht.interfaces.CategoryBtnInterface;
import de.hdm.vergissmeinnicht.interfaces.SeniorDeleteDialogInterface;
import de.hdm.vergissmeinnicht.interfaces.DataHolderClickInterface;
import de.hdm.vergissmeinnicht.model.CustomData;
import de.hdm.vergissmeinnicht.model.MedicineData;

public class SeniorMedicineActivity extends SeniorCustomActivity implements CategoryBtnInterface,
        DataHolderClickInterface, SeniorDeleteDialogInterface, ApiCallbackInterface {

    private static final String STACKNAME_DELETE_DIALOG = "deleteDialog";

    @Bind(R.id.recycler_view)   RecyclerView mRecyclerView;
    @Bind(R.id.info_data_txt)   TextView mInfoTxt;

    protected SeniorMedicineViewAdapter mMedicineAdapter;
    private ArrayList<MedicineData> mMedicineList;
    private CustomApplication mApplication;
    private MedicineData mCurrentlyHandledMedicineData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(
                savedInstanceState,
                R.layout.activity_senior_medicine,
                this);

        mApplication = (CustomApplication) getApplication();
        mMedicineList = mApplication.getMedicineList();

        setUpBtns(this);
        setUpRecyclerView();
        updateDataInfo();

        // set current textsize
        float currentTextsize = ((CustomApplication) getApplication()).getCurrentTextsize();
        mInfoTxt.setTextSize(currentTextsize - 10);
    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public ArrayList<MedicineData> getMedicineList() {
        return mMedicineList;
    }

    // sets up the RecyclerView
    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LayoutManager(this));
        float currentTextsize = ((CustomApplication) getApplication()).getCurrentTextsize();
        mMedicineAdapter = new SeniorMedicineViewAdapter(this, mMedicineList, mRecyclerView, currentTextsize - 10);
        mRecyclerView.setAdapter(mMedicineAdapter);
    }

    // is called by CategoryTouchListener when add button is clicked
    public void addBtnClicked() {
        StaticValues.updateFlag = false;

        // show AddEventDataFragment to add new event
        showAddDataFragment(
                SeniorAddMedicineDataFragment.newInstance(null),
                StaticValues.FRAGMENT_SENIOR_ADD_MEDICINE_DATA
        );
    }

    // SeniorAddMedicineFragment: called when user wants to save inserted data
    public void saveBtnClicked(MedicineData medicineData) {
        mCurrentlyHandledMedicineData = medicineData;
        mApplication.saveEventInGoogleCalendar(
                this,
                StaticMethods.convertMedicineDataToEvent(this, medicineData));
        backToOverView();
    }

    // DataHolderClickInterface: called when update button is clicked
    public void updateBtnClicked(CustomData customData) {
        if (mIsInEditMode && customData instanceof MedicineData) {
            switchEditMode(false);
            super.showAddDataFragment(
                    SeniorAddMedicineDataFragment.newInstance((MedicineData) customData),
                    StaticValues.FRAGMENT_SENIOR_ADD_MEDICINE_DATA
            );
        }
    }

    // DataHolderClickInterface: called when delete button is clicked
    public void deleteBtnClicked(CustomData customData) {
        if (mIsInEditMode) {
            SeniorDeleteEventDialog.newInstance(SeniorMedicineActivity.this, customData, R.color.default_yellow)
                    .show(getFragmentManager(), STACKNAME_DELETE_DIALOG);
        }
    }

    // Get Back to updated MedicineList
    public void backToOverView() {
        mFragmentManager.popBackStack();
        resetAllBtns();
    }

    // show info if there is no MedicineData
    private void updateDataInfo() {
        if (mMedicineList.size() > 0) {
            mInfoTxt.setVisibility(View.INVISIBLE);
        } else {
            mInfoTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void deleteEventCommited(CustomData customData) {
        if (customData instanceof MedicineData) {
            switchEditMode(false);
            mCurrentlyHandledMedicineData = (MedicineData) customData;
            mApplication.deleteEventFromGoogleCalendar(this, customData.getEventId());
        }
    }

    @Override
    public void saveEventCallback(boolean result, String eventId) {
        if (result) {
            // save MedicineData locally after its successfully saved in Google Calendar
            mCurrentlyHandledMedicineData.setEventId(eventId);
            mApplication.saveMedicineData(mCurrentlyHandledMedicineData);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMedicineAdapter.notifyDataSetChanged();
                    updateDataInfo();
                    Toast.makeText(
                            SeniorMedicineActivity.this,
                            getResources().getString(R.string.toast_success_save_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

        } else {
            mCurrentlyHandledMedicineData = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            SeniorMedicineActivity.this,
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
                    updateDataInfo();
                    mMedicineAdapter.notifyDataSetChanged();
                    Toast.makeText(
                            SeniorMedicineActivity.this,
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
                            SeniorMedicineActivity.this,
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
            mApplication.deleteMedicineData(mMedicineList.indexOf(mCurrentlyHandledMedicineData));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDataInfo();
                    mMedicineAdapter.notifyDataSetChanged();
                    Toast.makeText(
                            SeniorMedicineActivity.this,
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
                            SeniorMedicineActivity.this,
                            getResources().getString(R.string.toast_error_delete_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

}