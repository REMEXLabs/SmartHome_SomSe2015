package de.hdm.vergissmeinnicht.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.adapters.DefaultPlantsListAdapter;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.model.PlantData;

/**
 * Created by Dennis Jonietz on 6/13/15.
 */
public class DefaultPlantsFragment extends DefaultCustomFragment {

    @Bind(R.id.expandable_listview)     ExpandableListView mExpandableListView;

    private DefaultPlantsListAdapter mExpandableListAdapter;
    private ArrayList<PlantData> mPlantsList;

    public static DefaultPlantsFragment newInstance() {
        DefaultPlantsFragment fragment = new DefaultPlantsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default_plants, container, false);

        // initialize Butter Knife
        ButterKnife.bind(this, view);

        mPlantsList = ((CustomApplication) getActivity().getApplication()).getPlantsList();

        setUpListView();

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // sets up the ListView to show the plants
    private void setUpListView() {
        float currentTextsize = ((CustomApplication) getActivity().getApplication()).getCurrentTextsize();
        mExpandableListAdapter = new DefaultPlantsListAdapter(getActivity(), mPlantsList, currentTextsize - 10);
        mExpandableListView.setAdapter(mExpandableListAdapter);

        // handle clicks on group header
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                View groupIndicator = v.findViewById(R.id.plant_group_indicator);
                if (groupIndicator.getRotation() == 180) {
                    groupIndicator.setRotation(0);
                } else {
                    groupIndicator.setRotation(180);
                }
                return false;
            }
        });
    }

}