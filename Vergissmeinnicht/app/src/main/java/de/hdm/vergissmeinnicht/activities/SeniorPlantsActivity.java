package de.hdm.vergissmeinnicht.activities;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import butterknife.Bind;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.adapters.SeniorPlantsListAdapter;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.model.PlantData;
import de.hdm.vergissmeinnicht.view.MainButton;

public class SeniorPlantsActivity extends SeniorCustomActivity {

    @Bind(R.id.expandable_listview)    ExpandableListView mExpandableListView;

    private SeniorPlantsListAdapter mExpandableListAdapter;
    private ArrayList<PlantData> mPlantsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(
                savedInstanceState,
                R.layout.activity_senior_plants,
                this);
        mPlantsList = ((CustomApplication) getApplication()).getPlantsList();
        setUpListView();

        this.setUpBackBtn();


        // set current textsize
        float currentTextsize = ((CustomApplication) getApplication()).getCurrentTextsize();
        mBackBtn.setTextSize(currentTextsize - 10);
    }

    protected void setUpBackBtn() {
        mBackBtn = (MainButton) findViewById(R.id.back_btn);
        mBackBtn.setup(R.string.btn_navigation_back, R.drawable.ic_back_white, false);
        mBackBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent evt) {
                int action = evt.getActionMasked();

                if (action == MotionEvent.ACTION_DOWN) {
                    mVibrator.vibrate(50);
                    ((MainButton) v).pressed();

                } else if (action == MotionEvent.ACTION_UP) {
                    ((MainButton) v).released();
                    finish();
                }
                return true;
            }
        });
    }

    // sets up the ListView to show the plants
    private void setUpListView() {
        float currentTextsize = ((CustomApplication) getApplication()).getCurrentTextsize();
        mExpandableListAdapter = new SeniorPlantsListAdapter(this, mPlantsList, currentTextsize - 10);
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