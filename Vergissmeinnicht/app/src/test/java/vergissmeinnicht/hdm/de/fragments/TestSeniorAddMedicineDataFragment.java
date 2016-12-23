package vergissmeinnicht.hdm.de.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import de.hdm.vergissmeinnicht.BuildConfig;
import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.activities.SeniorMedicineActivity;
import de.hdm.vergissmeinnicht.fragments.SeniorAddMedicineDataFragment;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18,manifest = "./../main/AndroidManifest.xml", resourceDir = "res", constants = BuildConfig.class )
public class TestSeniorAddMedicineDataFragment {
    private SeniorAddMedicineDataFragment fragment;
    private AutoCompleteTextView mEditName, mEditInfo;

    @Before
    public void setUp() {
        fragment = new SeniorAddMedicineDataFragment();
        startFragment(fragment);
        View view = fragment.getView();
        mEditName = (AutoCompleteTextView) view.findViewById(R.id.edit_medicine_name);
        mEditInfo = (AutoCompleteTextView) view.findViewById(R.id.edit_medicine_info);
        fragment.setInfoEdit(mEditInfo);
        fragment.setNameEdit(mEditName);
    }

    @Test
    public void testIsDataCorrect01() {
        mEditName.setText("");
        mEditInfo.setText("");
        assertFalse(fragment.isDataCorrect());
    }

    @Test
    public void testIsDataCorrect02() {
        mEditName.setText("test02");
        mEditInfo.setText("");
        assertFalse(fragment.isDataCorrect());
    }

    @Test
    public void testIsDataCorrect03() {
        mEditName.setText("");
        mEditInfo.setText("test03");
        assertFalse(fragment.isDataCorrect());
    }

    @Test
    public void testIsDataCorrect04() {
        mEditName.setText("test04");
        mEditInfo.setText("test04");
        assertTrue(fragment.isDataCorrect());
    }

    public static void startFragment( Fragment fragment ) {
        Activity activity = Robolectric.buildActivity(SeniorMedicineActivity.class).create().start().resume().get();
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();
    }

}
