package de.hdm.vergissmeinnicht.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.adapters.DefaultMedicineViewAdapter;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.dialogs.DefaultAddMedicineDataDialog;
import de.hdm.vergissmeinnicht.dialogs.DefaultUpdateDeleteDialog;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.interfaces.ApiCallbackInterface;
import de.hdm.vergissmeinnicht.interfaces.DefaultAddMedicineDialogInterface;
import de.hdm.vergissmeinnicht.interfaces.DefaultUpdateDeleteDialogInterface;
import de.hdm.vergissmeinnicht.model.CustomData;
import de.hdm.vergissmeinnicht.model.MedicineData;


public class DefaultMedicineFragment extends DefaultCustomFragment
        implements DefaultAddMedicineDialogInterface, DefaultUpdateDeleteDialogInterface, ApiCallbackInterface {

    private static final String STACKNAME_UPDATE_DELETE_DIALOG = "updateDeleteDialog";
    private static final String STACKNAME_ADD_MEDICINE_INFO_DIALOG = "addMedicineInfoDialogFragment";
    private static final String STACKNAME_ADD_MEDICINE_TIME_DIALOG = "addMedicineTimeDialogFragment";

    @Bind(R.id.recycler_view)       RecyclerView mRecyclerView;
    @Bind(R.id.fab)                 FloatingActionButton mFab;

    private ArrayList<MedicineData> mMedicineList;
    private DefaultMedicineViewAdapter mMedicineAdapter;
    private CustomApplication mApplication;
    private boolean mUpdateFlag;
    private Activity mParentActivity;
    private MedicineData mCurrentlyHandledMedicineData;

    public static DefaultMedicineFragment newInstance() {
        DefaultMedicineFragment fragment = new DefaultMedicineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (CustomApplication) getActivity().getApplication();
        mMedicineList = mApplication.getMedicineList();
        mParentActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default_medicine, container, false);

        // initialize Butter Knife
        ButterKnife.bind(this, view);

        setUpInterface(view);
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // sets up the RecyclerView and the FloatingActionButton
    private void setUpInterface(View view) {
        mRecyclerView.setLayoutManager(new LayoutManager(view.getContext()));
        mMedicineAdapter = new DefaultMedicineViewAdapter(this, mMedicineList, mRecyclerView);
        mRecyclerView.setAdapter(mMedicineAdapter);

        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // shows the first EventDataDialog
                DefaultAddMedicineDataDialog.newInstance(DefaultMedicineFragment.this, new MedicineData())
                        .show(getFragmentManager(), STACKNAME_ADD_MEDICINE_INFO_DIALOG);

            }
        });
    }

    // DefaultMedicineAdapter: called on LongClick
    public void showUpdateDeleteDialog(int itemPos) {
        mCurrentlyHandledMedicineData = mMedicineList.get(itemPos);

        DefaultUpdateDeleteDialog.newInstance(this, mCurrentlyHandledMedicineData, R.color.default_yellow)
                .show(getFragmentManager(), STACKNAME_UPDATE_DELETE_DIALOG);
    }

    // DefaultAddMedicineTimeDialog: called when positive button is clicked
    @Override
    public void addedMedicineData(MedicineData medicineData) {
        mCurrentlyHandledMedicineData = medicineData;
        mApplication.saveEventInGoogleCalendar(
                this,
                StaticMethods.convertMedicineDataToEvent(getActivity(), medicineData));
    }

    // DefaultUpdateDeleteDialogInterface: called by DefaultUpdateDeleteDialog
    @Override
    public void updateEvent(CustomData customData) {
        if (customData instanceof MedicineData) {
            mUpdateFlag = true;
            // shows the first EventDataDialog
            DefaultAddMedicineDataDialog.newInstance(DefaultMedicineFragment.this, (MedicineData) customData)
                    .show(getFragmentManager(), STACKNAME_ADD_MEDICINE_INFO_DIALOG);
        }
    }

    // DefaultUpdateDeleteDialogInterface: called by DefaultUpdateDeleteDialog
    @Override
    public void deleteEvent(CustomData customData) {
        if (customData instanceof MedicineData) {
            ((CustomApplication) getActivity().getApplication())
                    .deleteEventFromGoogleCalendar(this, customData.getEventId());
        }
    }

    // SaveAsyncTask
    @Override
    public void saveEventCallback(boolean result, String eventId) {
        if (result) {
            // save AppointmentData locally after its successfully saved in Google Calendar
            mCurrentlyHandledMedicineData.setEventId(eventId);
            ((CustomApplication) mParentActivity.getApplication())
                    .saveMedicineData(mCurrentlyHandledMedicineData);


            mParentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMedicineAdapter.notifyDataSetChanged();
                    Toast.makeText(
                            mParentActivity,
                            getResources().getString(R.string.toast_success_save_api),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

        } else {
            mCurrentlyHandledMedicineData = null;
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
                    mApplication.updateMedicineData();
                    mMedicineAdapter.notifyDataSetChanged();
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
            ((CustomApplication) mParentActivity.getApplication())
                    .deleteMedicineData(mMedicineList.indexOf(mCurrentlyHandledMedicineData));

            mParentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMedicineAdapter.notifyDataSetChanged();
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