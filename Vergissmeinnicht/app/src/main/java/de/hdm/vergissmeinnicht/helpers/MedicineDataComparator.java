

package de.hdm.vergissmeinnicht.helpers;

/**
 * Compares and sorts the passed MedicineData by their interval
 */

import java.util.Comparator;

import de.hdm.vergissmeinnicht.model.MedicineData;

public class MedicineDataComparator implements Comparator<MedicineData> {

    private final static String TAEGLICH = "Täglich";
    private final static String DAILY = "DAILY";

    private final static String WOECHENTLICH = "Wöchentlich";
    private final static String WEEKLY = "WEEKLY";

    public int compare(MedicineData medA, MedicineData medB) {
        int intervalA = getIntervalInInt(medA.getInterval());
        int intervalB = getIntervalInInt(medB.getInterval());

        boolean intervalAIsHeader = medA.isHeader();

        if (intervalB < intervalA) {
            return 1;
        } else if (intervalA < intervalB) {
            return -1;
        //If updated MedicineData move backwards in List
        } else if(intervalA == intervalB && intervalAIsHeader == true ) {
            return -1;
        } else{
            return 0;
        }
    }

    private int getIntervalInInt(String interval) {
        if (interval.equalsIgnoreCase(DAILY)) {
            return 1;
        } else if (interval.equalsIgnoreCase(WEEKLY)) {
            return 2;
        } else {
            return 3;
        }
    }

}