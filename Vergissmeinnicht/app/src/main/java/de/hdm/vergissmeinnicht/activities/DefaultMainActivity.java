package de.hdm.vergissmeinnicht.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.LinePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.adapters.DefaultViewPagerAdapter;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.fragments.DefaultHomeFragment;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.interfaces.SettingsDialogInterface;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DefaultMainActivity extends Activity implements SettingsDialogInterface {

    private static final String STACKNAME_DEFAULT_MAIN_FRAGMENT = "defaultMainFragment";
    private static final String STACKNAME_SETTINGS_DIALOG_FRAGMENT = "settingsDialogFragment";

    @Bind(R.id.navigation_container)            RelativeLayout mNavigationBar;
    @Bind(R.id.container_activity_content)      RelativeLayout mContentContainer;
    @Bind(R.id.navigation_heading)              TextView mNavigationBarHeading;
    @Bind(R.id.navigation_icon)                 ImageView mNavigationBarIcon;
    @Bind(R.id.navigation_overview_icon)        ImageView mNavigationOverviewBtn;
    @Bind(R.id.indicators)                      LinePageIndicator mLineIndicator;
    @Bind(R.id.viewpager)                       ViewPager mViewPager;

    private boolean mExitApp;                   // handles click on back button
    private CustomApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_main);

        // initialize Butter Knife
        ButterKnife.bind(this);

        mApplication = (CustomApplication) getApplication();

        setUpViewPager();

        // show DefaultMainFragment on startup
        showMainFragment(true, true);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // sets up the ViewPager and the indicators
    private void setUpViewPager() {
        mViewPager.setAdapter(new DefaultViewPagerAdapter(getFragmentManager()));

        // indicators
        mLineIndicator.setViewPager(mViewPager);
        mLineIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updatePage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    // shows or removes the DefaultMainFragment and updates interface
    private void showMainFragment(boolean show, boolean startUp) {
        if (show) {
            // show DefaultMainFragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // don't animate fragment when activity is opened for first time
            int anim;
            if (startUp) {
                anim = 0;
            } else {
                anim = R.anim.slide_from_top;
            }

            transaction.addToBackStack(STACKNAME_DEFAULT_MAIN_FRAGMENT)
                        .setCustomAnimations(
                                anim,
                                R.anim.slide_to_top,
                                anim,
                                R.anim.slide_to_top
                        )
                        .replace(
                                R.id.container_activity_content,
                                DefaultHomeFragment.newInstance())
                        .commit();

            // disable ViewPager while fragment is shown
            mViewPager.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent evt) {
                    return true;
                }
            });
            mLineIndicator.setVisibility(View.GONE);                            // hide LineIndicators
            mNavigationOverviewBtn.setImageResource(R.drawable.ic_menu);        // set menu icon to navigation bar
            updatePage(1337);                                                   // update the colors of the screen

        } else {
            // remove DefaultMainFragment again
            getFragmentManager().popBackStack();

            mViewPager.setOnTouchListener(null);                                // enable ViewPager again when no fragment is shown
            mLineIndicator.setVisibility(View.VISIBLE);                         // show LineIndicators
            mNavigationOverviewBtn.setImageResource(R.drawable.ic_home);    // set home icon to navigation bar
            updatePage(mViewPager.getCurrentItem());                            // update the colors of the screen for the currently showing item
        }
    }

    // updates the color and the heading on the navigation bar for the current ViewPager item
    private void updatePage(int position) {
        int imageResource, stringId, colorId;

        mNavigationBarIcon.setVisibility(View.VISIBLE);
        switch (position) {
            case 0:
                imageResource = R.drawable.ic_plant_white;
                stringId = R.string.plants;
                colorId = R.color.default_green;
                break;

            case 1:
                imageResource = R.drawable.ic_pill_white;
                stringId = R.string.medicine;
                colorId = R.color.default_yellow;
                break;

            case 2:
                imageResource = R.drawable.ic_calendar_white;
                stringId = R.string.appointments;
                colorId = R.color.default_red;
                break;

            default:
                mNavigationBarIcon.setVisibility(View.GONE);
                imageResource = 0;
                stringId = R.string.btn_navigation_overview;
                colorId = R.color.default_blue;
                break;
        }

        mNavigationBarHeading.setText(stringId);
        mNavigationBarIcon.setImageResource(imageResource);
        mNavigationBar.setBackgroundColor(getResources().getColor(colorId));
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

    // creates and shows notification
    public void showNotification(View view) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//
//        builder.setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle("VergissMeinNicht")
//                .setContentText("Anstehendes Event!");
//
//        Intent resultIntent = new Intent(this, DefaultMainActivity.class);
//
//        // create artificial BackStack
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        stackBuilder.addParentStack(DefaultMainActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        builder.setContentIntent(resultPendingIntent);
//
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // 1337 = id
//        mNotificationManager.notify(1337, builder.build());
    }

    // SettingsDialogInterface: called when user wants to switch to senior interface
    @Override
    public void saveCurrentSettings(String language, String textsize,
                                    boolean reminderAudio, boolean reminderLight) {
        mApplication.saveSettings(language, textsize, reminderAudio, reminderLight);
        mApplication.changeCurrentLocale(language, this, DefaultMainActivity.class);
    }

    // is called when button in navbar is clicked
    @OnClick({R.id.navigation_overview_icon, R.id.navigation_switch_icon, R.id.navigation_settings_icon})
    protected void onNavigationOverviewButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.navigation_overview_icon:
                int backStackEntryCount = getFragmentManager().getBackStackEntryCount();

                if (backStackEntryCount > 0) {
                    showMainFragment(false, false);

                } else {
                    showMainFragment(true, false);
                }
                break;

            case R.id.navigation_switch_icon:
                // save chosen interface in application
                mApplication.saveInterfaceType(StaticValues.SENIOR_INTERFACE);

                // start senior interface
                startActivity(new Intent(DefaultMainActivity.this, SeniorMainActivity.class));
                finish();
                break;

            case R.id.navigation_settings_icon:
                mApplication.showSettingsDialog(
                        this,
                        getFragmentManager(),
                        STACKNAME_SETTINGS_DIALOG_FRAGMENT
                );
                break;
        }
    }

}