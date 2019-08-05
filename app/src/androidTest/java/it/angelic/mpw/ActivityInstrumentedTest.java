package it.angelic.mpw;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 * <p>
 * Copy this test to new pools
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(Parameterized.class)
public class ActivityInstrumentedTest {
    @Parameterized.Parameter()
    public PoolEnum mTestPool;
    @Parameterized.Parameter(value = 1)
    public CurrencyEnum mTestCurrency;

    private PoolEnum toBeTested;
    private Context appContext;
    private SharedPreferences sharedPreferences;

    @Parameterized.Parameters
    public static Collection<Object[]> initParameters() {
        return Arrays.asList(new Object[][]{
                {PoolEnum.NOOBPOOL, CurrencyEnum.ETH}
        });
    }

    @Before
    public void useAppContext() {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();
        String fileName = "MPW_TESTS";
        assertEquals("it.angelic.mpw", appContext.getPackageName());
        sharedPreferences = appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        toBeTested = mTestPool;
        Log.w("TEST", toBeTested.toString());
        editor.putString("poolEnum", toBeTested.name());
        editor.putString("curEnum", mTestCurrency.name());
        editor.commit();
        assertNotNull(sharedPreferences);
        //test with valid parameters
        assertTrue(toBeTested.getSupportedCurrencies().contains(mTestCurrency));
    }

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init(){
        activityActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void TestFab(){
        //onView(withId(R.id.textViewWalletTitle)).check(matches((isDisplayed())));
        //onView(withId(R.id.fab)).perform(click());

       // onView(allOf(withId( R.id.textViewWalletTitle), withText(mTestPool.toString())))
        //        .check(matches(isDisplayed()));
    }

}
