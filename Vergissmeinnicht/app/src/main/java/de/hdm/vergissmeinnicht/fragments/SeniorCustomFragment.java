package de.hdm.vergissmeinnicht.fragments;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import butterknife.ButterKnife;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;

/**
 * Created by Dennis Jonietz on 12.04.2015.
 */
public class SeniorCustomFragment<T> extends Fragment {

    public int mMainBtnPadding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, int layout) {
        View view = inflater.inflate(layout, container, false);

        // get main button padding form resources
        mMainBtnPadding = (int) getResources().getDimension(R.dimen.default_container_padding);

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setAdapter(Context context, T[] obj, Spinner spinner){
        ArrayAdapter<T> spinnerArrayAdapter = new ArrayAdapter<T>(
                context,
                android.R.layout.simple_spinner_item,
                obj);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

}