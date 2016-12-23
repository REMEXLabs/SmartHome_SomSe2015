package vergissmeinnicht.hdm.de.android;

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
public class TestCustomApplication_saveMedicineData {

    CustomApplication custApp;

    public MedicineData setUpMedData(String repeat, String interval, String info, int sectionFirstPosition,
                                        String name, DateTime dateTime, boolean isHeader)
    {
        return new MedicineData(dateTime,interval, repeat,
                name,info,sectionFirstPosition,isHeader);
    }

    @Before
    public void setUp(){
        custApp = new CustomApplication();
        custApp.setmMedicineList(new ArrayList<MedicineData>());
    }


    @Test
    public void testSaveMedicineData_01_01() {
        MedicineData expectedMedicine = this.setUpMedData("twice", "weekly", "Vor dem Essen", 1,
                "Herzmedizin", new DateTime(new Date()), true);
        boolean actual = custApp.saveMedicineData(expectedMedicine);
        assertTrue("Medicine data could not be saved successfully", actual);
    }


    @Test
    public void testSaveMedicineData_01_02() {
        MedicineData expectedMedicine = this.setUpMedData("twice", "weekly", "Vor dem Essen", 1,
                "Herzmedizin", new DateTime(new Date()), true);
        custApp.saveMedicineData(expectedMedicine);
        //check if the correct medicine data is saved in the mMedicineList
        MedicineData actualMedicine = custApp.getMedicineList().get(custApp.getMedicineList().size() - 1);
        assertEquals("The medicine which was saved is not the one in the list",expectedMedicine, actualMedicine);
    }

    @Test
    public void testSaveMedicineData_01_03(){
        MedicineData expectedMedicine = this.setUpMedData("twice", "weekly", "Vor dem Essen", 1,
                "Herzmedizin", new DateTime(new Date()), true);

        // +2 has to be added since the mMedicinelist was empty and therefore
        // there was no header in actual intervall, therefore a
        int expectedLengthOfMedList = custApp.getmMedicineList().size() + 2;

        custApp.saveMedicineData(expectedMedicine);

        //Double check if mMedicineList has grown correctly
        assertEquals("Length of MedicineList is not correct so the data wasn't saved correctly into the list",
                    expectedLengthOfMedList, custApp.getmMedicineList().size());

        //Now double check if list will grow only by one since the header now exists
        // Add one more MedicineList
        custApp.saveMedicineData(expectedMedicine);
        assertEquals("Length of Medicinelist is not correct so the data wasn't saved correctly into the list",
                expectedLengthOfMedList +1, custApp.getmMedicineList().size());
    }

    @Test
    public void testSaveMedicineData_02_01() {
        MedicineData testMedicine = this.setUpMedData(null, "weekly", "Vor dem Essen", 1,
                "Herzmedizin", new DateTime(new Date()), true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because repeat was null", actual);
    }
    @Test
    public void testSaveMedicineData_02_02(){
        MedicineData testMedicine = this.setUpMedData("", "weekly", "Vor dem Essen", 1,
                "Herzmedizin", new DateTime(new Date()), true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because repeat was empty", actual);
    }

    @Test
    public void testSaveMedicineData_03_01() {
        MedicineData testMedicine = this.setUpMedData("twice", null, "Vor dem Essen", 1,
                "Herzmedizin", new DateTime(new Date()), true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because interval was null", actual);
    }

    @Test
    public void testSaveMedicineData_03_02(){
        MedicineData testMedicine = this.setUpMedData("twice","","Vor dem Essen", 1,
                "Herzmedizin",new DateTime(new Date()),true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because intervall was empty", actual);
    }

    @Test
    public void testSaveMedicineData_04_01() {
        MedicineData testMedicine = this.setUpMedData("twice", "weekly", null, 1,
                "Herzmedizin", new DateTime(new Date()), true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because info was null", actual);
    }

    @Test
    public void testSaveMedicineData_04_02(){
        MedicineData testMedicine = this.setUpMedData("twice","weekly","", 1,
                "Herzmedizin",new DateTime(new Date()),true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because info was empty", actual);
    }

    @Test
    public void testSaveMedicineData_05_01() {
        MedicineData testMedicine = this.setUpMedData("twice", "weekly", "Vor dem Essen", 1,
                null, new DateTime(new Date()), true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because name was null", actual);
    }

    @Test
    public void testSaveMedicineData_05_02(){
        MedicineData testMedicine = this.setUpMedData("twice","weekly","Vor dem Essen", 1,
                "",new DateTime(new Date()),true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because name was empty", actual);
    }

    @Test
    public void testSaveMedicineData_06_01() {
        MedicineData testMedicine = this.setUpMedData("twice", "weekly", "Vor dem Essen", -1,
                "Herzmedizin", new DateTime(new Date()), true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because sectionFirstPosition was invalid", actual);
    }

    @Test
    public void testSaveMedicineData_06_02(){
        MedicineData testMedicine = this.setUpMedData("twice","weekly","Vor dem Essen", -5,
                "Herzmedizin",new DateTime(new Date()),true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because sectionFirstPosition was invalid", actual);
    }

    @Test
    public void testSaveMedicineData_06_03() {
        MedicineData testMedicine = this.setUpMedData("twice", "weekly", "Vor dem Essen", 65535,
                "Herzmedizin", new DateTime(new Date()), true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because sectionFirstPosition was invalid", actual);
    }

    @Test
    public void testSaveMedicineData_06_04(){
        MedicineData testMedicine = this.setUpMedData("twice","weekly","Vor dem Essen", 80000,
                "Herzmedizin",new DateTime(new Date()),true);
        boolean actual = custApp.saveMedicineData(testMedicine);
        assertFalse("Medicine data was saved, even though it shouldn't because sectionFirstPosition was invalid", actual);
    }

    @Test
    public void testSaveMedicineData_07(){
        boolean actual = custApp.saveMedicineData(null);
        assertFalse("Medicine data was saved, even though it was null", actual);
    }



}


