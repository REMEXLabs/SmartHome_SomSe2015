
package de.hdm.vergissmeinnicht.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.interfaces.DefaultAddMedicineDialogInterface;
import de.hdm.vergissmeinnicht.model.MedicineData;

public class DefaultAddMedicineDataDialog extends DialogFragment {

    private static final String BUNDLE_MEDICINE_DATA = "medicineData";

    @Bind(R.id.spinner_medicine_data_interval) Spinner mMedicineIntervalSpinner;
    @Bind(R.id.edit_medicine_name)      AutoCompleteTextView mMedicineNameEdit;
    @Bind(R.id.edit_medicine_info)      AutoCompleteTextView mMedicineInfoEdit;

    private DefaultAddMedicineDialogInterface mCallbackInterface;
    private MedicineData mMedicineData;

    public static DefaultAddMedicineDataDialog newInstance(
            DefaultAddMedicineDialogInterface callbackInterface, MedicineData medicineData) {
        DefaultAddMedicineDataDialog fragment = new DefaultAddMedicineDataDialog();

        final Bundle args = new Bundle(1);
        args.putParcelable(BUNDLE_MEDICINE_DATA, medicineData);
        fragment.setArguments(args);

        fragment.mCallbackInterface = callbackInterface;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMedicineData = getArguments().getParcelable(BUNDLE_MEDICINE_DATA);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // initialize interface
        View view = inflater.inflate(R.layout.dialog_default_add_medicine_data, null);

        // initialize Butter Knife
        ButterKnife.bind(this, view);

        builder.setView(view)
                .setTitle(R.string.dialog_title_add_medicine_info)
                .setPositiveButton(
                        R.string.btn_navigation_save,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mMedicineData == null) {
                                    mMedicineData = new MedicineData();
                                }
                                mMedicineData.setName(mMedicineNameEdit.getText().toString());
                                mMedicineData.setInfo(mMedicineInfoEdit.getText().toString());
                                mMedicineData.setInterval(StaticMethods.translateToEnglish( mMedicineIntervalSpinner.getSelectedItem().toString()));
                                mCallbackInterface.addedMedicineData(mMedicineData);
                            }
                        })
                .setNegativeButton(
                        R.string.dialog_settings_close_button,
                        null
                );

        // update EditTexts if updating
        String name = mMedicineData.getName();
        if (name != null && !name.equals("")) {
            mMedicineNameEdit.setText(name);
        }

        String info = mMedicineData.getInfo();
        if (info != null && !info.equals("")) {
            mMedicineInfoEdit.setText(info);
        }

        // add data to AutocompleteTextview
        String[] array = ((CustomApplication) getActivity().getApplication()).getAutoCompleteArray();
        if (array != null && array.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_spinner_dropdown_item,
                    array);

//            mMedicineNameEdit.setAdapter(adapter);
//            mMedicineInfoEdit.setAdapter(adapter);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.fragment_add_medicine_data_interval));
        mMedicineIntervalSpinner.setAdapter(dataAdapter);

        return builder.create();
    }

}