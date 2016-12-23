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
import de.hdm.vergissmeinnicht.view.CircleDisplay;

public class DefaultPlantsListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<PlantData> mPlantsList;
    private float mCurrentTextsize;

    public DefaultPlantsListAdapter(Context context, ArrayList<PlantData> plantsList, float currentTextsize) {
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
        ((TextView) convertView.findViewById(R.id.plant_name_txt)).setTextSize(mCurrentTextsize + 1);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final PlantData plantData = (PlantData) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_plant_default_listview_body, null);
        }

        CircleDisplay cd_temperature = (CircleDisplay) convertView.findViewById(R.id.circle_display_temperature);
        CircleDisplay cd_soilmoisture = (CircleDisplay) convertView.findViewById(R.id.circle_display_soilmoisture);
        CircleDisplay cd_light = (CircleDisplay) convertView.findViewById(R.id.circle_display_light);

        // set animation duration for CircleDisplays
        cd_temperature.setAnimDuration(750);
        cd_soilmoisture.setAnimDuration(750);
        cd_light.setAnimDuration(750);

        // set color of CircleDisplays
        cd_temperature.setColor(mContext.getResources().getColor(StaticMethods.giveColorForStatus(plantData.getTemperatureStatus())));
        cd_soilmoisture.setColor(mContext.getResources().getColor(StaticMethods.giveColorForStatus(plantData.getSoilmoistureStatus())));
        cd_light.setColor(mContext.getResources().getColor(StaticMethods.giveColorForStatus(plantData.getLightStatus())));
        cd_temperature.setUnit("°C");

        //Format the Strings from J́SON Resonse to enable Convertion to Float
        String formattedTemperature = plantData.getTemperature().substring(0, plantData.getTemperature().indexOf("°"));
        String formattedSoil = plantData.getSoilmoisture().substring(0, plantData.getSoilmoisture().indexOf("%"));
        String trimmedString = formattedTemperature.replaceAll("\\s", "");;

        // animates the CircleDisplays to the current values
        cd_temperature.showValue(Float.parseFloat(trimmedString), 50, true);
        cd_soilmoisture.showValue(Float.parseFloat(formattedSoil), 100, true);
        cd_light.showValue(Float.parseFloat(plantData.getLight()), 100, true);

        // remove touch from CircleViews
        cd_temperature.setTouchEnabled(false);
        cd_light.setTouchEnabled(false);
        cd_soilmoisture.setTouchEnabled(false);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}