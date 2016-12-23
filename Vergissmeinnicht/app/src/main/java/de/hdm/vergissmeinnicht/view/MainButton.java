package de.hdm.vergissmeinnicht.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.helpers.StaticValues;

/**
 * Created by Dennis Jonietz on 05.05.2015.
 */
public class MainButton extends RelativeLayout {

    private TextView mButtonTxt;

    public MainButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_main_button, this);

        mButtonTxt = ((TextView) findViewById(R.id.button_txt));
    }

    // sets the given text on the button
    public void setTextOnBtn(String text) {
        mButtonTxt.setText(text);
    }

    // sets the text size of the button
    public void setTextSize(float textsize) {
        mButtonTxt.setTextSize(textsize);
    }

    // puts the given content on the button and formats it depending on the activity
    public void setup(int textId, int imageId, boolean isInMainActivity) {
        TextView textView = ((TextView) findViewById(R.id.button_txt));
        ImageView imageView = ((ImageView) findViewById(R.id.button_icon));

        // set main content in button
        textView.setText(getResources().getString(textId));
        imageView.setImageResource(imageId);

        // format button
        RelativeLayout.LayoutParams txtParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();

        if (isInMainActivity) {
            // TextView right
            txtParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            txtParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
            // ImageView left
            imgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            imgParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);

        } else {
            // TextView left
            txtParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            txtParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            // ImageView right
            imgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            imgParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
        }
        textView.setLayoutParams(txtParams);
        imageView.setLayoutParams(imgParams);
    }

    // scales the MainButton down when it is pressed
    public void pressed() {
        setScaleX(StaticValues.SCALE_PRESSED);
        setScaleY(StaticValues.SCALE_PRESSED);
    }

    // scales the MainButton up when it is released
    public void released() {
        setScaleX(StaticValues.SCALE_RELEASED);
        setScaleY(StaticValues.SCALE_RELEASED);
    }

}