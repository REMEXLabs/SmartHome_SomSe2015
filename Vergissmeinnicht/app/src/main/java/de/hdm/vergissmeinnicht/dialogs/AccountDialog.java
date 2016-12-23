package de.hdm.vergissmeinnicht.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;


public class AccountDialog extends DialogFragment {

    private static final String BUNDLE_USER_NAME = "username";
    private static final String BUNDLE_USER_PWD = "password";

    private String mUserNameValue, mPasswordValue;
    private CustomApplication mApplication;

    @Bind(R.id.account_name_edit_btn)
    ImageView mNameEditButton;
    @Bind(R.id.account_pwd_edit_btn)
    ImageView mPwdEditButton;
    @Bind(R.id.account_pwd)
    EditText mPassword;
    @Bind(R.id.account_name)
    EditText mUserName;
    @Bind(R.id.btn_close)
    TextView mCloseBtn;
    @Bind(R.id.btn_save)
    TextView mSaveBtn;

    public static AccountDialog newInstance(String name, String pwd) {
        AccountDialog fragment = new AccountDialog();

        Bundle args = new Bundle();
        args.putString(BUNDLE_USER_NAME, name);
        args.putString(BUNDLE_USER_PWD, pwd);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mApplication = (CustomApplication)getActivity().getApplication();
        if (getArguments() != null) {
            mUserNameValue = getArguments().getString(BUNDLE_USER_NAME);
            mPasswordValue = getArguments().getString(BUNDLE_USER_PWD);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_account, null);

        // initialize Butter Knife
        ButterKnife.bind(this, view);

        mPassword.setText(mPasswordValue);
        disableEdit(mPassword);
        mUserName.setText(mUserNameValue);
        disableEdit(mUserName);
        mNameEditButton.setColorFilter(getResources().getColor(R.color.dialog_settings_color));
        mPwdEditButton.setColorFilter(getResources().getColor(R.color.dialog_settings_color));

        // set up views  to fit chosen interface
        if (((CustomApplication) getActivity().getApplication()).getAppInterface()
                == StaticValues.SENIOR_INTERFACE) {

            StaticMethods.makeSeniorInterfaceStyle(getActivity(), mSaveBtn);
            StaticMethods.makeSeniorInterfaceStyle(getActivity(), mCloseBtn);
        }

        // set current textsize
        float currentTextsize = ((CustomApplication) getActivity().getApplication()).getCurrentTextsize();
        mSaveBtn.setTextSize(currentTextsize - 10);
        mCloseBtn.setTextSize(currentTextsize - 10);

        // Inflate the layout for this fragment
        builder.setView(view);
        return builder.create();
    }

    //  saveBtn or closeBtn is clicked
    @OnTouch({R.id.btn_save, R.id.btn_close})
    protected boolean buttonClicked(View v, MotionEvent evt) {
        int action = evt.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            v.setScaleX(StaticValues.SCALE_PRESSED);
            v.setScaleY(StaticValues.SCALE_PRESSED);

        } else if (action == MotionEvent.ACTION_UP) {
            v.setScaleX(StaticValues.SCALE_RELEASED);
            v.setScaleY(StaticValues.SCALE_RELEASED);

            if (v.getId() == R.id.btn_save) {
                updatePreferences();
                showSuccessToast();
                AccountDialog.this.dismiss();
            } else {
                AccountDialog.this.dismiss();
            }
        }
        return true;
    }

    @OnTouch({R.id.account_pwd_edit_btn, R.id.account_name_edit_btn})
    protected boolean editButtonClicked(View v, MotionEvent evt) {
        int action = evt.getActionMasked();

            if (action == MotionEvent.ACTION_DOWN) {
                v.setScaleX(StaticValues.SCALE_PRESSED);
                v.setScaleY(StaticValues.SCALE_PRESSED);

            } else if (action == MotionEvent.ACTION_UP) {
                v.setScaleX(StaticValues.SCALE_RELEASED);
                v.setScaleY(StaticValues.SCALE_RELEASED);

                if (v.getId() == R.id.account_pwd_edit_btn) {
                    enableEdit(mPassword);
                    mPassword.requestFocus();
                } else if(v.getId() == R.id.account_name_edit_btn){
                    enableEdit(mUserName);
                    mUserName.requestFocus();
                }

            }
            return true;
        }

    private void disableEdit(EditText editText){
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        editText.setClickable(false);
    }

    public void enableEdit(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
        editText.setClickable(true);
    }

    public void updatePreferences(){
        SharedPreferences settings = mApplication.getSharedPreferences(StaticValues.PREFS_APP_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(StaticValues.PREFS_USER_NAME, mUserName.getText().toString());
        editor.putString(StaticValues.PREFS_PASSWORD, mPassword.getText().toString());
        editor.commit();
    }

    public void showSuccessToast(){
        Toast.makeText(
                getActivity(),
                R.string.account_success_message,
                Toast.LENGTH_LONG).show();

    }

}