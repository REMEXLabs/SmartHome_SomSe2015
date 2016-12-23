package vergissmeinnicht.hdm.de;

import android.test.InstrumentationTestCase;

/**
 * Created by Stefan on 7/20/2015.
 */
public class TestInstrumentationTests extends InstrumentationTestCase {
    public void testIntstrTests_01() throws Exception {
        final int expected = 1;
        final int reality = 1;
        assertEquals(expected, reality);
    }
}