package vergissmeinnicht.hdm.de;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.activities.SeniorMedicineActivity;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.model.MedicineData;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class CustomApplicationTest extends ActivityInstrumentationTestCase2<SeniorMedicineActivity> {

    private SeniorMedicineActivity seniorMedicineActivity;
    private CustomApplication customApplication;

    public CustomApplicationTest() {
        super(SeniorMedicineActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        seniorMedicineActivity = getActivity();
        customApplication = (CustomApplication) seniorMedicineActivity.getApplication();
    }

    @Test
    public void testdataTransferFragmentApplication() {

        //click the button to show the fragment
        onView((withId(R.id.add_btn))).perform(click());

        // insert edit_medicine_name
        String medicineName = "Aspirin";
        onView(withId(R.id.edit_medicine_name)).perform(typeText(medicineName));

        // insert edit_medicine_info
        String medicineInfo = "Some long text for the medicine Info";
        onView(withId(R.id.edit_medicine_info)).perform(typeText(medicineInfo));

        // set frequency
        onView(withId(R.id.spinner_medicine_data_interval)).perform(click());

        // Click on the item
        onData(allOf(is(instanceOf(String.class)), is("Weekly"))).perform(click());

        // save
        onView(withId(R.id.btn_save_medicine)).perform(click());

        // Check Element in the Medicine List Array
        ArrayList<MedicineData> mMedicineListfromActivity = seniorMedicineActivity.getMedicineList();
        ArrayList<MedicineData> mMedicineListfromApplication = customApplication.getMedicineList();

        // Compare data from Activity and Custom Application
        assertEquals(mMedicineListfromActivity.get(1).getName(), mMedicineListfromApplication.get(1).getName());
        assertEquals(mMedicineListfromActivity.get(1).getInterval(), mMedicineListfromApplication.get(1).getInterval());
        assertEquals(mMedicineListfromActivity.get(1).getInfo(), mMedicineListfromApplication.get(1).getInfo());

    }




}
