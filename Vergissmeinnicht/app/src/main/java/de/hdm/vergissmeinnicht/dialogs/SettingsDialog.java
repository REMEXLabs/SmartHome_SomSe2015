package de.hdm.vergissmeinnicht.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.interfaces.SettingsDialogInterface;

public class SettingsDialog extends DialogFragment {

    private static final String BUNDLE_LANGUAGE_DATA = "currentLanguage",
                                BUNDLE_TEXTSIZE_DATA = "currentTextSize",
                                BUNDLE_REMINDER_SOUND_DATA = "currentReminderSound",
                                BUNDLE_REMINDER_LIGHT_DATA = "currentReminderLight",
                                STACKNAME_ACCOUNT_DIALOG_FRAGMENT = "accountsDialogFragment";

    @Bind(R.id.btn_german)              TextView mGermanBtn;
    @Bind(R.id.btn_english)             TextView mEnglishBtn;
    @Bind(R.id.btn_textsize_small)      TextView mTextSizeSmallBtn;
    @Bind(R.id.btn_textsize_medium)     TextView mTextSizeMediumBtn;
    @Bind(R.id.btn_textsize_big)        TextView mTextSizeBigBtn;
    @Bind(R.id.btn_save)                TextView mSaveBtn;
    @Bind(R.id.btn_close)               TextView mCloseBtn;
    @Bind(R.id.txt_reminder_audio)      CheckBox mAudioCheckbox;
    @Bind(R.id.txt_reminder_light)      CheckBox mLightCheckbox;
    @Bind(R.id.btn_account)             TextView mAccountBtn;

    private SettingsDialogInterface mCallbackInterface;
    private String mLanguage, mTextsize;
    private boolean mReminderSound, mReminderLight;
    private CustomApplication mApplication;

    public static SettingsDialog newInstance(SettingsDialogInterface callbackInterface, String language,
                                             String textSize, boolean reminderSound, boolean reminderLight) {
        SettingsDialog fragment = new SettingsDialog();

        final Bundle args = new Bundle(1);
        args.putString(BUNDLE_LANGUAGE_DATA, language);
        args.putString(BUNDLE_TEXTSIZE_DATA, textSize);
        args.putBoolean(BUNDLE_REMINDER_SOUND_DATA, reminderSound);
        args.putBoolean(BUNDLE_REMINDER_LIGHT_DATA, reminderLight);

        fragment.setArguments(args);

        fragment.mCallbackInterface = callbackInterface;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLanguage = getArguments().getString(BUNDLE_LANGUAGE_DATA);
        mTextsize = getArguments().getString(BUNDLE_TEXTSIZE_DATA);
        mReminderLight = getArguments().getBoolean(BUNDLE_REMINDER_LIGHT_DATA);
        mReminderSound = getArguments().getBoolean(BUNDLE_REMINDER_SOUND_DATA);
        mApplication = (CustomApplication) getActivity().getApplication();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // initialize interface and set custom color
        View view = inflater.inflate(R.layout.dialog_settings, null);

        // initialize Butter Knife
        ButterKnife.bind(this, view);

        // set up current values
        if (mLanguage.equals("de")) {
            makeButtonActive(mGermanBtn);
            makeButtonInactive(mEnglishBtn);

        } else if (mLanguage.equals("en")) {
            makeButtonInactive(mGermanBtn);
            makeButtonActive(mEnglishBtn);
        }

        if (mTextsize.equals(StaticValues.TEXTSIZE_SMALL)) {
            makeButtonActive(mTextSizeSmallBtn);
            makeButtonInactive(mTextSizeMediumBtn);
            makeButtonInactive(mTextSizeBigBtn);

        } else if (mTextsize.equals(StaticValues.TEXTSIZE_MEDIUM)) {
            makeButtonInactive(mTextSizeSmallBtn);
            makeButtonActive(mTextSizeMediumBtn);
            makeButtonInactive(mTextSizeBigBtn);

        } else if (mTextsize.equals(StaticValues.TEXTSIZE_BIG)) {
            makeButtonInactive(mTextSizeSmallBtn);
            makeButtonInactive(mTextSizeMediumBtn);
            makeButtonActive(mTextSizeBigBtn);
        }

        if (mReminderLight){
            mLightCheckbox.setChecked(true);
        }
        if (mReminderSound) {
            mAudioCheckbox.setChecked(true);
        }

            // change buttons if in senior interface
        if (((CustomApplication) getActivity().getApplication()).getAppInterface()
                == StaticValues.SENIOR_INTERFACE) {
            StaticMethods.makeSeniorInterfaceStyle(getActivity(), mSaveBtn);
            StaticMethods.makeSeniorInterfaceStyle(getActivity(), mAccountBtn);
            StaticMethods.makeSeniorInterfaceStyle(getActivity(), mCloseBtn);
        }

        // set current textsize
        float currentTextsize = ((CustomApplication) getActivity().getApplication()).getCurrentTextsize();
        mSaveBtn.setTextSize(currentTextsize - 10);
        mAccountBtn.setTextSize(currentTextsize - 10);
        mCloseBtn.setTextSize(currentTextsize - 10);

        // create dialog
        builder.setView(view);
        return builder.create();
    }

    // called if switchInterfaceBtn, saveBtn or closeBtn is clicked
    @OnTouch({R.id.btn_save, R.id.btn_close, R.id.btn_account})
    protected boolean buttonClicked(View v, MotionEvent evt) {
        int action = evt.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            v.setScaleX(StaticValues.SCALE_PRESSED);
            v.setScaleY(StaticValues.SCALE_PRESSED);

        } else if (action == MotionEvent.ACTION_UP) {
            v.setScaleX(StaticValues.SCALE_RELEASED);
            v.setScaleY(StaticValues.SCALE_RELEASED);

            if (v.getId() == R.id.btn_save) {
                if (mReminderSound || mReminderLight) {
                    mCallbackInterface.saveCurrentSettings(mLanguage, mTextsize, mReminderSound, mReminderLight);
                    SettingsDialog.this.dismiss();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getResources().getString(R.string.dialog_message_reminder_submit))
                            .setPositiveButton(
                                    R.string.dialog_reminder_submit,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mCallbackInterface.saveCurrentSettings(mLanguage, mTextsize, mReminderSound, mReminderLight);
                                            SettingsDialog.this.dismiss();
                                        }
                                    }
                            )
                            .setNegativeButton(
                                    R.string.dialog_reminder_cancel,
                                    null
                            )
                            .show();
                }

            } else if (v.getId() == R.id.btn_account) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getResources().getString(R.string.dialog_google_account_message))
                        .setPositiveButton(
                                R.string.ok,
                                null
                        )
                        .show();


            } else {
                SettingsDialog.this.dismiss();
            }
        }
        return true;
    }

    // used to show active / inactive buttons
    private void makeButtonActive(TextView button) {
        button.setBackground(getResources().getDrawable(R.drawable.bg_settings_btn_active));
        button.setTextColor(getResources().getColor(R.color.default_text_color));
        button.setPadding(
                getResources().getDimensionPixelSize(R.dimen.default_button_margin),
                getResources().getDimensionPixelSize(R.dimen.default_button_margin),
                getResources().getDimensionPixelSize(R.dimen.default_button_margin),
                getResources().getDimensionPixelSize(R.dimen.default_button_margin));
    }
    private void makeButtonInactive(TextView button) {
        button.setBackgroundColor(getResources().getColor(R.color.default_white));
        button.setTextColor(getResources().getColor(R.color.default_text_color));
    }

    @OnClick({R.id.btn_german, R.id.btn_english})
    protected void languageButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_german:
                mLanguage = "de";
                makeButtonActive(mGermanBtn);
                makeButtonInactive(mEnglishBtn);
                break;

            case R.id.btn_english:
                mLanguage = "en";
                makeButtonInactive(mGermanBtn);
                makeButtonActive(mEnglishBtn);
                break;
        }
    }

    @OnClick({R.id.btn_textsize_small, R.id.btn_textsize_medium, R.id.btn_textsize_big})
    protected void textsizeButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_textsize_small:
                mTextsize = StaticValues.TEXTSIZE_SMALL;
                makeButtonActive(mTextSizeSmallBtn);
                makeButtonInactive(mTextSizeMediumBtn);
                makeButtonInactive(mTextSizeBigBtn);
                break;

            case R.id.btn_textsize_medium:
                mTextsize = StaticValues.TEXTSIZE_MEDIUM;
                makeButtonInactive(mTextSizeSmallBtn);
                makeButtonActive(mTextSizeMediumBtn);
                makeButtonInactive(mTextSizeBigBtn);
                break;

            case R.id.btn_textsize_big:
                mTextsize = StaticValues.TEXTSIZE_BIG;
                makeButtonInactive(mTextSizeSmallBtn);
                makeButtonInactive(mTextSizeMediumBtn);
                makeButtonActive(mTextSizeBigBtn);
                break;
        }

    }

    @OnClick({R.id.txt_reminder_light, R.id.txt_reminder_audio})
    protected void reminderCheckClicked(View v) {
        switch (v.getId()) {
            case R.id.txt_reminder_light:
                mReminderLight = ((CheckBox) v).isChecked();
                break;

            case R.id.txt_reminder_audio:
                mReminderSound = ((CheckBox) v).isChecked();
        }
    }

}