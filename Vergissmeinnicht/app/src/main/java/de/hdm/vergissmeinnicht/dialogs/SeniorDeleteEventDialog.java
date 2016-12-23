package de.hdm.vergissmeinnicht.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnTouch;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.interfaces.SeniorDeleteDialogInterface;
import de.hdm.vergissmeinnicht.model.AppointmentData;
import de.hdm.vergissmeinnicht.model.CustomData;
import de.hdm.vergissmeinnicht.model.MedicineData;

public class SeniorDeleteEventDialog extends DialogFragment {

    private static final String BUNDLE_APPOINTMENT_DATA = "appointmentData";
    private static final String BUNDLE_MEDICINE_DATA = "medicineData";
    private static final String BUNDLE_DIALOG_COLOR = "dialogColor";

    private SeniorDeleteDialogInterface mCallbackInterface;
    private CustomData mCustomData;

    public static SeniorDeleteEventDialog newInstance(SeniorDeleteDialogInterface callbackInterface,
                                                      CustomData customData, int colorId) {
        SeniorDeleteEventDialog fragment = new SeniorDeleteEventDialog();

        final Bundle args = new Bundle(1);
        if (customData instanceof AppointmentData) {
            args.putParcelable(BUNDLE_APPOINTMENT_DATA, (AppointmentData) customData);

        } else if (customData instanceof MedicineData) {
            args.putParcelable(BUNDLE_MEDICINE_DATA, (MedicineData) customData);
        }
        args.putInt(BUNDLE_DIALOG_COLOR, colorId);
        fragment.setArguments(args);

        fragment.mCallbackInterface = callbackInterface;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCustomData = getArguments().getParcelable(BUNDLE_APPOINTMENT_DATA);
        if (mCustomData == null) {
            mCustomData = getArguments().getParcelable(BUNDLE_MEDICINE_DATA);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // initialize interface and set custom color
        int colorId = getArguments().getInt(BUNDLE_DIALOG_COLOR);
        View view = inflater.inflate(R.layout.dialog_senior_delete_event, null);
        view.findViewById(R.id.txt_title).setBackgroundColor(getResources().getColor(colorId));

        // initialize Butter Knife
        ButterKnife.bind(this, view);

        // create dialog
        builder.setView(view);
        return builder.create();
    }

    // is called when button is clicked
    @OnTouch({R.id.txt_btn_yes, R.id.txt_btn_no})
    protected boolean onButtonClicked(View v, MotionEvent evt) {
        int action = evt.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            v.setScaleX(StaticValues.SCALE_PRESSED);
            v.setScaleY(StaticValues.SCALE_PRESSED);

        } else if (action == MotionEvent.ACTION_UP) {
            v.setScaleX(StaticValues.SCALE_RELEASED);
            v.setScaleY(StaticValues.SCALE_RELEASED);

            switch (v.getId()) {
                case R.id.txt_btn_yes:
                    mCallbackInterface.deleteEventCommited(mCustomData);
                    // no "break", because dialog should be dismissed anyway

                case R.id.txt_btn_no:
                    SeniorDeleteEventDialog.this.dismiss();
                    break;
            }
        }
        return true;
    }

}