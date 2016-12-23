package de.hdm.vergissmeinnicht.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;


public class AuthenticateDialog extends DialogFragment {
    protected static final String BUNDLE_PASSWORD = "password";
    private static final String STACKNAME_AUTH_DIALOG_FRAGMENT = "authDialogFragment";

    private CustomApplication mApplication;

    @Bind(R.id.password_confirmation)   EditText mConfirmPassword;
    @Bind(R.id.btn_close)               TextView mCloseBtn;
    @Bind(R.id.btn_save)                TextView mSaveBtn;

    public static AuthenticateDialog newInstance(String pwd) {
        AuthenticateDialog fragment = new AuthenticateDialog();
        Bundle args = new Bundle();
        args.putString(BUNDLE_PASSWORD, pwd);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (CustomApplication) getActivity().getApplication();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_authenticate, null);

        // initialize Butter Knife
        ButterKnife.bind(this, view);

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

        builder.setView(view);
        return builder.create();
    }

    @OnClick({R.id.btn_save, R.id.btn_close})
    protected void onConfirmPassword(View view){
        switch(view.getId()) {
            case R.id.btn_save:
                if (mConfirmPassword.getText().toString().equals(getArguments().getString(BUNDLE_PASSWORD))) {
                    mApplication.showAccountsDialog(
                            getFragmentManager(),
                            STACKNAME_AUTH_DIALOG_FRAGMENT
                    );
                    AuthenticateDialog.this.dismiss();
                } else {
                    Toast.makeText(
                            getActivity(),
                            getResources().getString(R.string.wrong_password),
                            Toast.LENGTH_SHORT)
                            .show();
                }
                break;

          case R.id.btn_close:
              AuthenticateDialog.this.dismiss();
        }
    }

}
