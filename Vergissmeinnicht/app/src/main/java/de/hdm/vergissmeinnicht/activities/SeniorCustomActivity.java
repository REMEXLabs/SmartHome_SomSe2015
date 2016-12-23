package de.hdm.vergissmeinnicht.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.interfaces.ApiCallbackInterface;
import de.hdm.vergissmeinnicht.interfaces.CategoryBtnInterface;
import de.hdm.vergissmeinnicht.listeners.CategoryBtnTouchListener;
import de.hdm.vergissmeinnicht.model.AppointmentData;
import de.hdm.vergissmeinnicht.model.CustomData;
import de.hdm.vergissmeinnicht.view.DataViewHolder;
import de.hdm.vergissmeinnicht.view.MainButton;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Dennis Jonietz on 28.05.2015.
 */
public class SeniorCustomActivity<T> extends Activity {

    @Nullable @Bind(R.id.add_btn)   MainButton mAddBtn;
    @Nullable @Bind(R.id.edit_btn)  MainButton mEditBtn;
    @Bind(R.id.back_btn)            MainButton mBackBtn;

    private String mAddDataFragmentStackName;
    private Activity mChildActivity;
    protected FragmentManager mFragmentManager;
    protected Vibrator mVibrator;
    protected boolean mIsInEditMode = false, mAddDataFragmentIsShowing = false;

    protected void onCreate(Bundle savedInstanceState, int layoutId, Activity activity) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        // initialize Butter Knife
        ButterKnife.bind(this);

        // set up the CustomActivity for the child activity
        mChildActivity = activity;
        if (activity instanceof SeniorAppointmentsActivity) {
            mAddDataFragmentStackName = StaticValues.FRAGMENT_SENIOR_ADD_APPOINTMENT_DATA;

        } else if (activity instanceof SeniorMedicineActivity) {
            mAddDataFragmentStackName = StaticValues.FRAGMENT_SENIOR_ADD_MEDICINE_DATA;
        }

        setUpFragmentHandling();

        // initialize the device vibrator for touch feedback
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator.hasVibrator()) {
            Log.v("Can Vibrate", "YES");
        } else {
            Log.v("Can Vibrate", "NO");
        }

        // set current textsize
        float currentTextsize = ((CustomApplication) getApplication()).getCurrentTextsize();
        ((TextView) findViewById(R.id.navigation_category)).setTextSize(currentTextsize - 10);

    }

    // overrides the device back button to avoid bugs in app
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // sets up the mFragmentManager and the BackStackChangedListener
    private void setUpFragmentHandling() {
        mFragmentManager = getFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int backStackEntryCount = mFragmentManager.getBackStackEntryCount();

                if (backStackEntryCount > 0) {
                    // if there are fragments on backStack, get the one on top...
                    FragmentManager.BackStackEntry topEntry =
                            mFragmentManager.getBackStackEntryAt(backStackEntryCount - 1);

                    // ... and check if it's the AddEventFragment
                    if (topEntry.getName().equals(mAddDataFragmentStackName)) {
                        mAddDataFragmentIsShowing = true;
                        return;
                    }
                }

                mAddDataFragmentIsShowing = false;
                mAddBtn.setVisibility(View.VISIBLE);
                mEditBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    // sets up the navigation buttons
    protected void setUpBtns(CategoryBtnInterface categoryBtnInterface) {
        // initialize buttons
        mAddBtn.setup(R.string.btn_navigation_add, R.drawable.ic_plus_white, false);
        mEditBtn.setup(R.string.btn_navigation_edit, R.drawable.ic_edit_white, false);
        mBackBtn.setup(R.string.btn_navigation_back, R.drawable.ic_back_white, false);

        // create TouchListener for buttons
        View.OnTouchListener touchListener = new CategoryBtnTouchListener(categoryBtnInterface, mVibrator);
        mAddBtn.setOnTouchListener(touchListener);
        mEditBtn.setOnTouchListener(touchListener);
        mBackBtn.setOnTouchListener(touchListener);

        // set current textsize
        float currentTextsize = ((CustomApplication) getApplication()).getCurrentTextsize();
        mAddBtn.setTextSize(currentTextsize - 10);
        mEditBtn.setTextSize(currentTextsize - 10);
        mBackBtn.setTextSize(currentTextsize - 10);
    }

    // is called by CategoryTouchListener when edit button is clicked
    public void editBtnClicked() {
        if (!mIsInEditMode) {
            switchEditMode(true);
        }
    }

    // is called by CategoryTouchListener when back button is clicked
    public void backBtnClicked() {
        if (mIsInEditMode) {
            // quit the edit mode
            switchEditMode(false);

        } else if (mAddDataFragmentIsShowing) {
            // remove AddEventFragment and enable EditBtn again
            mFragmentManager.popBackStack();
            resetAllBtns();

        } else {
            finish();
        }
    }

    // enables all buttons and puts the default text on them
    protected void resetAllBtns() {
        mAddBtn.setTextOnBtn(getResources().getString(R.string.btn_navigation_add));
        mEditBtn.setTextOnBtn(getResources().getString(R.string.btn_navigation_edit));
        mBackBtn.setTextOnBtn(getResources().getString(R.string.btn_navigation_back));

        enableBtn(mAddBtn);
        enableBtn(mEditBtn);
        enableBtn(mBackBtn);
    }

    // disables a button optical and functional
    protected void disableBtn(MainButton button) {
        button.setAlpha(StaticValues.ALPHA_DISABLED);
        button.setEnabled(false);
    }

    // enables a button optical and functional
    protected void enableBtn(MainButton button) {
        button.setAlpha(StaticValues.ALPHA_ENABLED);
        button.setEnabled(true);
    }

    // switches in edit mode and updates the Activities
    protected void switchEditMode(boolean isInEditMode) {
        mIsInEditMode = isInEditMode;

        int activityType = 0;

        if (mChildActivity instanceof SeniorAppointmentsActivity) {
            activityType = StaticValues.APPOINTMENT_TYPE;
        } else if (mChildActivity instanceof SeniorMedicineActivity) {
            activityType = StaticValues.MEDICINE_TYPE;
        }

        if (mIsInEditMode) {
            disableBtn(mAddBtn);
            disableBtn(mEditBtn);
            mBackBtn.setTextOnBtn(getResources().getString(R.string.btn_navigation_cancel));
            setVisibilityEventButtons(activityType, View.VISIBLE);

        } else {
            resetAllBtns();
            setVisibilityEventButtons(activityType, View.INVISIBLE);
        }
    }

    // sets the visibility of the edit and delete buttons on every event
    private void setVisibilityEventButtons(int activityType, int visibility) {
        ArrayList dataArrayList = null;
        RecyclerView recyclerView = null;

        switch (activityType) {
            case StaticValues.APPOINTMENT_TYPE:
                dataArrayList = ((SeniorAppointmentsActivity) mChildActivity).getAppointmentsList();
                recyclerView = ((SeniorAppointmentsActivity) mChildActivity).getRecyclerView();
                break;
            case StaticValues.MEDICINE_TYPE:
                dataArrayList = ((SeniorMedicineActivity) mChildActivity).getMedicineList();
                recyclerView = ((SeniorMedicineActivity) mChildActivity).getRecyclerView();
                break;
        }
        for (Object temp : dataArrayList) {
            if (! ((CustomData) temp).isHeader()) {
                DataViewHolder viewHolder = (DataViewHolder) recyclerView
                        .findViewHolderForAdapterPosition(dataArrayList.indexOf(temp));
                if (viewHolder != null) { // TODO: bugfix because of NullPointerException -> getAnchorAtEnd bug
                    viewHolder.editDataBtn.setVisibility(visibility);
                    viewHolder.deleteDataBtn.setVisibility(visibility);
                }
            }
        }
    }

    // shows a AddEventDataFragment or AddMedicineDataFragment
    protected void showAddDataFragment(Fragment fragment, String fragmentStackName) {
        mAddBtn.setVisibility(View.INVISIBLE);
        mEditBtn.setVisibility(View.INVISIBLE);
        mBackBtn.setTextOnBtn(getResources().getString(R.string.btn_navigation_cancel));

        // show fragment in activity
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(fragmentStackName);
        fragmentTransaction.add(R.id.fragment_container, fragment, fragmentStackName);
        fragmentTransaction.commit();
    }

        // hides the software keyboard if it's currently showing
    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}