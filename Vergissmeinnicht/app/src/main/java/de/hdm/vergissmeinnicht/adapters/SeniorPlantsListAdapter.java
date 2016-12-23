package de.hdm.vergissmeinnicht.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.model.PlantData;

/**
 * Created by Dennis Jonietz on 22.05.2015.
 */
public class SeniorPlantsListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<PlantData> mPlantsList;
    private float mCurrentTextsize;

    public SeniorPlantsListAdapter(Context context, ArrayList<PlantData> plantsList, float currentTextsize) {
        mContext = context;
        mPlantsList = plantsList;
        mCurrentTextsize = currentTextsize;
    }

    @Override
    public int getGroupCount() {
        return mPlantsList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mPlantsList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mPlantsList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final PlantData plantData = (PlantData) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_plant_listview_header, null);
        }

        ((TextView) convertView.findViewById(R.id.plant_name_txt)).setText(String.valueOf(plantData.getName()));
        ((ImageView) convertView.findViewById(R.id.plant_status_icon)).setImageResource(plantData.getStatusIconResource());
        ((ImageView) convertView.findViewById(R.id.plant_status_icon)).setColorFilter(
                mContext.getResources().getColor(StaticMethods.giveColorForStatus(plantData.getTemperatureStatus())),
                PorterDuff.Mode.MULTIPLY);

        // set current textsize
        ((TextView) convertView.findViewById(R.id.plant_name_txt)).setTextSize(mCurrentTextsize+1);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final PlantData plantData = (PlantData) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_plant_senior_listview_body, null);
        }

        ((TextView) convertView.findViewById(R.id.txt_temperature)).setText(String.valueOf(plantData.getTemperature()));
        ((TextView) convertView.findViewById(R.id.txt_soilmoisture)).setText(String.valueOf(plantData.getSoilmoisture()));
        ((TextView) convertView.findViewById(R.id.txt_light)).setText(String.valueOf(plantData.getLight() + " %"));

        ((ImageView) convertView.findViewById(R.id.img_temperature)).setColorFilter(
                mContext.getResources().getColor(StaticMethods.giveColorForStatus(plantData.getTemperatureStatus())),
                PorterDuff.Mode.MULTIPLY);
        ((ImageView) convertView.findViewById(R.id.img_soilmoisture)).setColorFilter(
                mContext.getResources().getColor(StaticMethods.giveColorForStatus(plantData.getSoilmoistureStatus())),
                PorterDuff.Mode.MULTIPLY);
        ((ImageView) convertView.findViewById(R.id.img_light)).setColorFilter(
                mContext.getResources().getColor(StaticMethods.giveColorForStatus(plantData.getLightStatus())),
                PorterDuff.Mode.MULTIPLY);

        // set current textsize
        ((TextView) convertView.findViewById(R.id.txt_temperature)).setTextSize(mCurrentTextsize);
        ((TextView) convertView.findViewById(R.id.txt_soilmoisture)).setTextSize(mCurrentTextsize);
        ((TextView) convertView.findViewById(R.id.txt_light)).setTextSize(mCurrentTextsize);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
