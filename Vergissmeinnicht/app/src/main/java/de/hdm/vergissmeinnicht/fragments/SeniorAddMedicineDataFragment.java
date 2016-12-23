package de.hdm.vergissmeinnicht.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import org.joda.time.DateTime;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.activities.SeniorMedicineActivity;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.model.MedicineData;

/**
 * Created by Dennis Jonietz on 4/12/15.
 */
public class SeniorAddMedicineDataFragment extends SeniorCustomFragment {

    private static final String BUNDLE_MEDICINE_DATA = "medicineData";

    @Bind(R.id.spinner_medicine_data_interval)  Spinner mIntervalSpinner;
    @Bind(R.id.edit_medicine_name)              AutoCompleteTextView mNameEdit;
    @Bind(R.id.edit_medicine_info)              AutoCompleteTextView mInfoEdit;

    private MedicineData mMedicineData;
    private String mInfoText, mNameText, mIntervalValue;

    // generates a new instance of the SeniorAddMedicineFragment using the default empty constructor
    public static final SeniorAddMedicineDataFragment newInstance(MedicineData medicineData) {
        SeniorAddMedicineDataFragment fragment = new SeniorAddMedicineDataFragment();

        final Bundle args = new Bundle(1);
        args.putParcelable(BUNDLE_MEDICINE_DATA, medicineData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMedicineData = getArguments().getParcelable(BUNDLE_MEDICINE_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, R.layout.fragment_senior_add_medicine_data);

        // bind Butter Knife
        ButterKnife.bind(this, view);

        // add data to autocomplete textview
        String[] complete = ((CustomApplication) getActivity().getApplication()).getAutoCompleteArray();
        if (complete != null && complete.length>0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    complete);
//            mNameEdit.setAdapter(adapter);
//            mInfoEdit.setAdapter(adapter);
        }

        // update event, so fill in data of current MedicineData
        if (mMedicineData != null) {
            mNameEdit.setText(mMedicineData.getName());
            mInfoEdit.setText(mMedicineData.getInfo());
        }

        // set Strings to spinners
        setAdapter(getActivity(), getResources().getStringArray(
                R.array.fragment_add_medicine_data_interval), mIntervalSpinner);

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // creates an MedicineData object from the data filled in by the user
    public MedicineData getDataOfMedicine() {
        getValues();
        return new MedicineData(
                new DateTime(),
                mIntervalValue,
                mNameText,
                mInfoText,
                0,
                false);
    }

    public void updateMedicineData() {
        //get Values of Views
        getValues();
        mMedicineData.setInfo(mInfoText);
        mMedicineData.setName(mNameText);
//        mMedicineData.setRepeat(mRepeatValue);
        mMedicineData.setInterval(mIntervalValue);
        ((SeniorMedicineActivity) getActivity()).backToOverView();

    }

    private void getValues() {
        mIntervalValue = StaticMethods.translateToEnglish(mIntervalSpinner.getSelectedItem().toString());
        mInfoText = mInfoEdit.getText().toString();
        mNameText = mNameEdit.getText().toString();
    }

    // checks if the data in the input fields is correctly filled in
    public boolean isDataCorrect() {
        if (mNameEdit.getText().toString().equals("")) {
            // no name filled in
            Toast.makeText(
                    getActivity(),
                    getString(R.string.medicine_add_data_missing_name),
                    Toast.LENGTH_SHORT
            ).show();
            return false;

        } else if (mInfoEdit.getText().toString().equals("")) {
            // no info filled in
            Toast.makeText(
                    getActivity(),
                    getString(R.string.medicine_add_data_missing_info),
                    Toast.LENGTH_SHORT
            ).show();
            return false;

        } else {
            return true;
        }
    }

    // gets called when save button is clicked
    @OnTouch(R.id.btn_save_medicine)
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
                if (isDataCorrect()) {
                    if (!StaticValues.updateFlag) {
                        mMedicineData = getDataOfMedicine();
                        ((SeniorMedicineActivity) getActivity()).saveBtnClicked(mMedicineData);

                    } else {
                        updateMedicineData();
                        ((SeniorMedicineActivity) getActivity()).saveBtnClicked(mMedicineData);
                    }
                }
                break;
        }
        return true;

    }

    // For JUnit Testing purposes
    public void setInfoEdit(AutoCompleteTextView mInfoEdit){
        this.mInfoEdit = mInfoEdit;
    }

    public void setNameEdit(AutoCompleteTextView mNameEdit){
        this.mNameEdit = mNameEdit;
    }



}