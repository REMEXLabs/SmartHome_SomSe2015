package de.hdm.vergissmeinnicht.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.android.CustomApplication;

/**
 * Created by Dennis Jonietz on 6/13/15.
 */
public class DefaultHomeFragment extends DefaultCustomFragment {

    @Bind(R.id.txt_current_dates)       TextView mImportantEventsTxt;
    @Bind(R.id.txt_dayOfMonth)          TextView mCurrentDayTxt;
    @Bind(R.id.txt_month)               TextView mCurrentMonthTxt;
    @Bind(R.id.txt_year)                TextView mCurrentYearTxt;

    public static DefaultHomeFragment newInstance() {
        DefaultHomeFragment fragment = new DefaultHomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default_home, container, false);

        // initialize Butter Knife
        ButterKnife.bind(this, view);

        // check for important events in the next time
        mImportantEventsTxt.setText(
                ((CustomApplication) getActivity().getApplication()).getImportantEvents());

        // set date to calendar view
        Calendar cal = Calendar.getInstance();
        mCurrentDayTxt.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
        mCurrentMonthTxt.setText(String.valueOf(cal.getDisplayName(
                Calendar.MONTH,
                Calendar.LONG,
                Locale.getDefault())));
        mCurrentYearTxt.setText(String.valueOf(cal.get(Calendar.YEAR)));

        // get textsize from settings
        mImportantEventsTxt.setTextSize(
                ((CustomApplication) getActivity().getApplication()).getCurrentTextsize());

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}