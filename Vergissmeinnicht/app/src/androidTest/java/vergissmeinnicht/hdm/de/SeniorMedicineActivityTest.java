package vergissmeinnicht.hdm.de;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.activities.SeniorMedicineActivity;
import de.hdm.vergissmeinnicht.model.MedicineData;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class SeniorMedicineActivityTest extends ActivityInstrumentationTestCase2<SeniorMedicineActivity> {

    private SeniorMedicineActivity seniorMedicineActivity;

    public SeniorMedicineActivityTest() {
        super(SeniorMedicineActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        seniorMedicineActivity = getActivity();
    }

    @Test
    public void shouldBeAbleToLaunchFragment() {

        //get the text which the fragment shows
        ViewInteraction fragmentText = onView(withId(R.id.fragment_heading));

        //check the fragment text does not exist on fresh activity start
        fragmentText.check(ViewAssertions.doesNotExist());

        //click the button to show the fragment
        onView((withId(R.id.add_btn))).perform(click());

        //check the fragments text is now visible in the activity
        fragmentText.check(matches(isDisplayed()));

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

        // getElement
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));

        // Check that the Information is in the system and displayed to the user
        onData(allOf(is(instanceOf(String.class)), is("Weekly")));
        onData(allOf(is(instanceOf(String.class)), is("Aspirin")));
        onData(allOf(is(instanceOf(String.class)), endsWith("Some long text for the medicine Info")));

        // Check Element in the Medicine List Array
        ArrayList<MedicineData> mMedicineList = seniorMedicineActivity.getMedicineList();

        assertEquals(mMedicineList.get(1).getName(), "Aspirin");
        assertEquals(mMedicineList.get(1).getInterval(), "Weekly");
        assertEquals(mMedicineList.get(1).getInfo(), "Some long text for the medicine Info");

    }

}
