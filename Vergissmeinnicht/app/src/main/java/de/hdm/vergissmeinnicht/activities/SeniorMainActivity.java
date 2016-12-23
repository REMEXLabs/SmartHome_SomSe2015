package de.hdm.vergissmeinnicht.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.interfaces.SettingsDialogInterface;
import de.hdm.vergissmeinnicht.view.MainButton;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SeniorMainActivity extends Activity implements SettingsDialogInterface {

    private static final String STACKNAME_SETTINGS_DIALOG_FRAGMENT = "settingsDialogFragment";

    @Bind(R.id.plants_btn)              MainButton mPlantsBtn;
    @Bind(R.id.medicine_btn)            MainButton mMedicineBtn;
    @Bind(R.id.appointments_btn)        MainButton mAppointmentsBtn;
    @Bind(R.id.switch_btn)              MainButton mSwitchBtn;
    @Bind(R.id.settings_btn)            MainButton mSettingsBtn;
    @Bind(R.id.txt_current_dates)       TextView mImportantEventsTxt;
    @Bind(R.id.txt_dayOfMonth)          TextView mCurrentDayTxt;
    @Bind(R.id.txt_month)               TextView mCurrentMonthTxt;
    @Bind(R.id.txt_year)                TextView mCurrentYearTxt;

    private CustomApplication mApplication;
    private Vibrator mVibrator;
    private boolean mExitApp;               // handles click on back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_main);

        // initialize Butter Knife
        ButterKnife.bind(this);

        mApplication = (CustomApplication) getApplication();

        // set up the text and icon resources for every btn
        mPlantsBtn.setup(R.string.btn_navigation_plants, R.drawable.ic_plant_white, true);
        mMedicineBtn.setup(R.string.btn_navigation_medicine, R.drawable.ic_pill_white, true);
        mAppointmentsBtn.setup(R.string.btn_navigation_appointments, R.drawable.ic_calendar_white, true);
        mSwitchBtn.setup(R.string.btn_switch_interfaces, R.drawable.ic_elderly, true);
        mSettingsBtn.setup(R.string.btn_navigation_settings, R.drawable.ic_edit_white, true);

        // initialize the device vibrator for touch feedback
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator.hasVibrator()) {
            Log.v("Can Vibrate", "YES");
        } else {
            Log.v("Can Vibrate", "NO");
        }

        // set date to calendar view
        Calendar cal = Calendar.getInstance();
        mCurrentDayTxt.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
        mCurrentMonthTxt.setText(String.valueOf(cal.getDisplayName(
                        Calendar.MONTH,
                        Calendar.LONG,
                        Locale.getDefault())));
        mCurrentYearTxt.setText(String.valueOf(cal.get(Calendar.YEAR)));

        // check for important events in the next time
        mImportantEventsTxt.setText(mApplication.getImportantEvents());

        // update text sizes
        float currentTextsize = mApplication.getCurrentTextsize();
        mImportantEventsTxt.setTextSize(currentTextsize);
        mPlantsBtn.setTextSize(currentTextsize - 10);
        mMedicineBtn.setTextSize(currentTextsize - 10);
        mAppointmentsBtn.setTextSize(currentTextsize - 10);
        mSwitchBtn.setTextSize(currentTextsize - 10);
        mSettingsBtn.setTextSize(currentTextsize - 10);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // closes the app when back button is pressed twice
    @Override
    public void onBackPressed() {
        if (mExitApp) {
            finish();

        } else {
            Toast.makeText(
                    this,
                    getResources().getString(R.string.click_again_to_leave_app),
                    Toast.LENGTH_SHORT)
                    .show();
            mExitApp = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExitApp = false;
                }
            }, 2000);
        }
    }

    @Override
    public void saveCurrentSettings(String language, String textsize,
                                    boolean reminderSound, boolean reminderLight) {
        mApplication.saveSettings(language, textsize, reminderSound, reminderLight);
        mApplication.changeCurrentLocale(language, this, SeniorMainActivity.class);
    }

    // is called when a navigation button is clicked
    @OnTouch({R.id.plants_btn, R.id.medicine_btn, R.id.appointments_btn, R.id.switch_btn, R.id.settings_btn})
    protected boolean onNavigationButtonTouched(View v, MotionEvent evt) {
        int action = evt.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            ((MainButton) v).pressed();
            mVibrator.vibrate(500);

        } else if (action == MotionEvent.ACTION_UP) {
            ((MainButton) v).released();

            switch (v.getId()) {
                case R.id.plants_btn:
                    startActivity(new Intent(
                            SeniorMainActivity.this, SeniorPlantsActivity.class));
                    break;

                case R.id.medicine_btn:
                    startActivity(new Intent(
                            SeniorMainActivity.this, SeniorMedicineActivity.class));
                    break;

                case R.id.appointments_btn:
                    startActivity(new Intent(
                            SeniorMainActivity.this, SeniorAppointmentsActivity.class));
                    break;

                case R.id.switch_btn:
                    // save chosen interface in application
                    mApplication.saveInterfaceType(StaticValues.DEFAULT_INTERFACE);

                    // start default interface
                    startActivity(new Intent(SeniorMainActivity.this, DefaultMainActivity.class));
                    finish();
                    break;

                case R.id.settings_btn:
                    mApplication.showSettingsDialog(
                            this,
                            getFragmentManager(),
                            STACKNAME_SETTINGS_DIALOG_FRAGMENT
                    );
                    break;
            }
        }
        return true;
    }

}