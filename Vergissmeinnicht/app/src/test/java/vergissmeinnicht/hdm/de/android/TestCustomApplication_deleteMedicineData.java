package vergissmeinnicht.hdm.de.android;

import android.util.Log;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;

import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.model.MedicineData;

/**
 * Created by Stefan Reinhardt on 7/22/2015.
 */
@RunWith(RobolectricTestRunner.class)
public class TestCustomApplication_deleteMedicineData {
    CustomApplication custApp;

    public void fillMedDataList()
    {
        for(int i = 0; i<5; ++i)
            custApp.saveMedicineData(new MedicineData(new DateTime(new Date()), "weekly", "twice",
                                                      "Herzmedizin", "Vor dem Essen", 1, false));
    }

    @Before
    public void setUp(){
        custApp = new CustomApplication();
        custApp.setmMedicineList(new ArrayList<MedicineData>());
        this.fillMedDataList();
    }

    @Test
    public void testDeleteMedicineData_01_01(){
        int expectedSizeOfMedList = custApp.getMedicineList().size() - 1;
        custApp.deleteMedicineData(2);
        int actualSizeOfMedList = custApp.getMedicineList().size();
        assertEquals("The size of the list didn't shrink correctly so the object wasn't deleted correctly",
                expectedSizeOfMedList, actualSizeOfMedList);
    }

    @Test
    public void testDeleteMedicineData_01_02(){

        boolean actSuccessValue = true;
        int arrayListLength = custApp.getMedicineList().size();
        for(int i = 0;i<arrayListLength+2; ++i ) {
            actSuccessValue = custApp.deleteMedicineData(custApp.getMedicineList().size() - 1);
        }
        assertFalse("Could delete something from the emptylist", actSuccessValue);
    }

    @Test
    public void testDeleteMedicineData_01_03(){

        int expectedArrayListSize = 0;

        int arrayListLength = custApp.getMedicineList().size();
        for(int i = 0;i<arrayListLength-1; ++i ) {
            custApp.deleteMedicineData(custApp.getMedicineList().size() - 1);
        }
        assertEquals("Header wasn't removed, even though there were no elements left anymore", expectedArrayListSize, custApp.getMedicineList().size());
    }

    @Test
    public void testDeleteMedicineData_01_04(){
        MedicineData expectedElementAfterDeleted = custApp.getMedicineList().get(3);
        custApp.deleteMedicineData(2);
        MedicineData actualElementAfterDeleted = custApp.getMedicineList().get(2);
        assertEquals("The wrong element was deleted", expectedElementAfterDeleted, actualElementAfterDeleted);
    }


    @Test
    public void testDeleteMedicineData_02_01(){
        boolean actSuccessValue = custApp.deleteMedicineData(-5);

        assertFalse("The Medicinedata was deleted even though the index was invalid", actSuccessValue);
    }
    @Test
    public void testDeleteMedicineData_02_02(){
        boolean actSuccessValue = custApp.deleteMedicineData(80_000);

        assertFalse("The Medicinedata was deleted even though the index was invalid",actSuccessValue);
    }
    @Test
    public void testDeleteMedicineData_02_03(){
        boolean actSuccessValue = custApp.deleteMedicineData(-1);

        assertFalse("The Medicinedata was deleted even though the index was invalid",actSuccessValue);
    }
    @Test
    public void testDeleteMedicineData_02_04(){
        boolean actSuccessValue = custApp.deleteMedicineData(custApp.getMedicineList().size()+1);

        assertFalse("The Medicinedata was deleted even though the index was invalid",actSuccessValue);
    }


}