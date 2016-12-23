package de.hdm.vergissmeinnicht.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.activities.SeniorAppointmentsActivity;
import de.hdm.vergissmeinnicht.interfaces.ApiCallbackInterface;

/**
 * Created by Dennis Jonietz on 28.05.2015.
 */
public class DefaultCustomFragment<T> extends DialogFragment {

    public void setAdapter(Context context, T[] obj, Spinner spinner) {
        ArrayAdapter<T> spinnerArrayAdapter = new ArrayAdapter<T>(
                context,
                android.R.layout.simple_spinner_item,
                obj);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

}