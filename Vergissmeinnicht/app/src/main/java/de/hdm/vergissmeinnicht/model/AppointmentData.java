package de.hdm.vergissmeinnicht.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * Created by Dennis Jonietz on 5/3/15.
 */
public class AppointmentData extends CustomData implements Parcelable {

    private String name, leadTime, info, gcal_binding;
    private DateTime dateTime;
    private int sectionFirstPosition;

    public AppointmentData(){};
    public AppointmentData(DateTime dateTime, String info, int sectionFirstPosition, String leadTime, boolean isHeader) {
        this.dateTime = dateTime;
        this.info = info;
        this.sectionFirstPosition = sectionFirstPosition;
        this.leadTime = leadTime;
        setIsHeader(isHeader);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public DateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getLeadTime(){
        return leadTime;
    }
    public void setLeadTime(String leadTime){
        this.leadTime = leadTime;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public int getSectionFirstPosition() {
        return sectionFirstPosition;
    }
    public void setSectionFirstPosition(int sectionFirstPosition) {
        this.sectionFirstPosition = sectionFirstPosition;
    }

    public String getGcal_binding() {
        return gcal_binding;
    }
    public void setGcal_binding(String gcal_binding) {
        this.gcal_binding = gcal_binding;
    }

    // make EventData parcelable
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getEventId());
        out.writeLong(dateTime.getMillis());
        out.writeString(info);
    }
    public static final Parcelable.Creator<AppointmentData> CREATOR = new Parcelable.Creator<AppointmentData>() {
        public AppointmentData createFromParcel(Parcel in) {
            return new AppointmentData(in);
        }

        public AppointmentData[] newArray(int size) {
            return new AppointmentData[size];
        }
    };
    private AppointmentData(Parcel in) {
        setEventId(in.readString());
        dateTime = new DateTime(in.readLong());
        info = in.readString();
    }

}