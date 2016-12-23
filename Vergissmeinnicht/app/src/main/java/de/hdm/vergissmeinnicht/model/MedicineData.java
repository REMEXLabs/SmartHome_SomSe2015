package de.hdm.vergissmeinnicht.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * Created by Dennis Jonietz on 5/3/15.
 */
public class MedicineData extends CustomData implements Parcelable {

    private String interval, info, name, gcal_binding;
    private DateTime dateTime;
    private int sectionFirstPosition;

    // empty constructor for adding new MedicineData
    public MedicineData() {
        this.dateTime = new DateTime();
        this.interval = "";
        this.info = "";
        this.name = "";
        this.sectionFirstPosition = 0;
        setIsHeader(false);
    }

    // constructor for headings
    public MedicineData(String interval, boolean isHeader) {
        this.dateTime = new DateTime();
        this.interval = interval;
        this.name = "";
        this.sectionFirstPosition = 0;
        this.info = "";
        setIsHeader(isHeader);
    }

    // default constructor
    public MedicineData(DateTime dateTime, String interval, String name, String info,
                        int sectionFirstPosition, boolean isHeader) {
        this.dateTime = dateTime;
        this.interval = interval;
        this.name = name;
        this.sectionFirstPosition = sectionFirstPosition;
        this.info = info;
        setIsHeader(isHeader);
    }

    public DateTime getDateTime() {
        return this.dateTime;
    }
    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getInterval(){ return this.interval;}
    public void setInterval(String interval) {this.interval = interval;}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() { return this.info; }
    public void setInfo(String info) {this.info = info;}

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
        out.writeString(name);
        out.writeString(info);
        out.writeLong(dateTime.getMillis());
        out.writeString(interval);
    }
    public static final Creator<MedicineData> CREATOR = new Creator<MedicineData>() {
        public MedicineData createFromParcel(Parcel in) {
            return new MedicineData(in);
        }

        public MedicineData[] newArray(int size) {
            return new MedicineData[size];
        }
    };
    private MedicineData(Parcel in) {
        setEventId(in.readString());
        name = in.readString();
        info = in.readString();
        dateTime = new DateTime(in.readLong());
        interval = in.readString();
    }

    public int getSectionFirstPosition() {
        return sectionFirstPosition;
    }

}