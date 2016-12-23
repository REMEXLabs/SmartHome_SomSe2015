package de.hdm.vergissmeinnicht.model;

import android.os.Parcel;
import android.os.Parcelable;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.helpers.StaticValues;

/**
 * Created by Dennis Jonietz on 5/3/15.
 */
public class PlantData implements Parcelable {

    private String temperature, soilmoisture, light, name;
    private int temperatureStatus, soilmoistureStatus, lightStatus;

    public PlantData(String name, String temperature, int temperatureStatus,
                     String soilmoisture, int soilmoistureStatus,
                     String light, int lightStatus) {
        this.name = name;
        this.temperature = temperature;
        this.temperatureStatus = temperatureStatus;
        this.soilmoisture = soilmoisture;
        this.soilmoistureStatus = soilmoistureStatus;
        this.light = light;
        this.lightStatus = lightStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getSoilmoisture() {
        return soilmoisture;
    }

    public void setSoilmoisture(String soilmoisture) {
        this.soilmoisture = soilmoisture;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public int getTemperatureStatus() {
        return temperatureStatus;
    }

    public void setTemperatureStatus(int i){
        this.temperatureStatus = i;
    }

    public int getSoilmoistureStatus() {
        return soilmoistureStatus;
    }

    public void setSoilmoistureStatus(int soilStatus){
        this.soilmoistureStatus = soilStatus;
    }

    public int getLightStatus() {
        return lightStatus;
    }

    public void setLightStatus(int lightStatus){
        this.lightStatus = lightStatus;
    }
    // returns the resource for the smiley fitting the current status of the plant
    public int getStatusIconResource() {
        switch (temperatureStatus) {
            case StaticValues.PLANT_STATUS_GOOD:
                return R.drawable.ic_smiley_good;
            case StaticValues.PLANT_STATUS_OK:
                return R.drawable.ic_smiley_ok;
            case StaticValues.PLANT_STATUS_BAD:
                return R.drawable.ic_smiley_bad;
        }
        return 0;
    }

    // make EventData parcelable
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(temperature);
        out.writeInt(temperatureStatus);
        out.writeString(soilmoisture);
        out.writeInt(soilmoistureStatus);
        out.writeString(light);
        out.writeInt(lightStatus);
    }
    public static final Creator<PlantData> CREATOR = new Creator<PlantData>() {
        public PlantData createFromParcel(Parcel in) {
            return new PlantData(in);
        }

        public PlantData[] newArray(int size) {
            return new PlantData[size];
        }
    };
    private PlantData(Parcel in) {
        name = in.readString();
        temperature = in.readString();
        temperatureStatus = in.readInt();
        soilmoisture = in.readString();
        soilmoistureStatus = in.readInt();
        light = in.readString();
        lightStatus = in.readInt();
    }

}