package de.hdm.vergissmeinnicht.listeners;

import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.interfaces.CategoryBtnInterface;
import de.hdm.vergissmeinnicht.view.MainButton;

/**
 * Created by Dennis Jonietz on 29.05.2015.
 */
public class CategoryBtnTouchListener implements View.OnTouchListener {

    private CategoryBtnInterface mCallbackInterface;
    private Vibrator mVibrator;

    public CategoryBtnTouchListener(CategoryBtnInterface callbackInterface, Vibrator vibrator) {
        mCallbackInterface = callbackInterface;
        mVibrator = vibrator;
    }

    @Override
    public boolean onTouch(View v, MotionEvent evt) {
        int action = evt.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            mVibrator.vibrate(50);
            ((MainButton) v).pressed();

        } else if (action == MotionEvent.ACTION_UP) {
            ((MainButton) v).released();

            switch (v.getId()) {
                case R.id.add_btn:
                    mCallbackInterface.addBtnClicked();
                    break;

                case R.id.edit_btn:
                    mCallbackInterface.editBtnClicked();
                    break;

                case R.id.back_btn:
                    mCallbackInterface.backBtnClicked();
                    break;
            }
        }
        return true;
    }
}
