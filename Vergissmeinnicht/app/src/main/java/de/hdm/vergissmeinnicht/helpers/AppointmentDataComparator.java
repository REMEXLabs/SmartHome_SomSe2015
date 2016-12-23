package de.hdm.vergissmeinnicht.helpers;

/**
 * Compares and sorts the given AppointmentData by their dates
 */

import java.util.Comparator;

import de.hdm.vergissmeinnicht.model.AppointmentData;

public class AppointmentDataComparator implements Comparator<AppointmentData> {

    public int compare(AppointmentData appA, AppointmentData appB) {
        long dateA = appA.getDateTime().getMillis();
        long dateB = appB.getDateTime().getMillis();

        boolean dateAIsHeader= appA.isHeader();

        if (dateA > dateB) {
            return 1;
        } else if (dateA < dateB) {
            return -1;
         //If updated MedicineData move backwards in List
        } else if(dateA == dateB && dateAIsHeader == true ) {
            return -1;
        }
        else {
            return 0;
        }
    }

}