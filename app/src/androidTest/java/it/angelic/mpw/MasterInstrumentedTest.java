package it.angelic.mpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.angelic.mpw.model.db.PoolDbHelper;
import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;
import it.angelic.mpw.model.jsonpojos.blocks.Block;
import it.angelic.mpw.model.jsonpojos.home.HomeStats;
import it.angelic.mpw.model.jsonpojos.miners.Miner;
import it.angelic.mpw.model.jsonpojos.miners.MinerRoot;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Instrumentation test, which will execute on an Android device.
 * <p>
 * Copy this test to new pools
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(Parameterized.class)
public class MasterInstrumentedTest {
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
                {PoolEnum.NOOBPOOL, CurrencyEnum.ETH},
                {PoolEnum.CRYPTOPOOL, CurrencyEnum.ETC},
                {PoolEnum.CHILEMINERS, CurrencyEnum.CLO},
                {PoolEnum.MAXHASH, CurrencyEnum.ETH},
                {PoolEnum.MAXHASH, CurrencyEnum.UBIQ}
        });
    }

    @Before
    public void useAppContext() {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();
        String fileName = "FILE_NAME";
        assertEquals("it.angelic.mpw", appContext.getPackageName());
        sharedPreferences = appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        toBeTested = mTestPool;
        Log.w("TEST", toBeTested.toString());
        editor.putString("poolEnum", toBeTested.name());
        editor.putString("curEnum", mTestCurrency.name());
        editor.commit();
        assertNotNull(sharedPreferences);
        assertTrue(toBeTested.getSupportedCurrencies().contains(mTestCurrency));
    }

    @Test
    public void testBaseExplorer() {
        List<CurrencyEnum> curs = toBeTested.getSupportedCurrencies();
        if (mTestCurrency.getScannerSite() != null) {
            try {
                TestUtils.testURL(mTestCurrency.getScannerSite().getBaseAddress().toString());
            } catch (Exception e) {
                fail("Blockchain explorer error for " + mTestCurrency.toString());
            }
        }
    }

    @Test
    public void TestJsonHomeStatsSyncReq() {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Utils.getHomeStatsURL(sharedPreferences), new JSONObject(), future, future);
        JSONClientSingleton.getInstance(appContext).addToRequestQueue(request);
        final Gson gson = TestUtils.getGsonFromBuilder();
        try {
            JSONObject response = future.get(); // this will block
            HomeStats retrievedHomeStats = gson.fromJson(response.toString(), HomeStats.class);
            assertNotNull(retrievedHomeStats);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void TestJsonBlockSyncReq() {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Utils.getBlocksURL(sharedPreferences), new JSONObject(), future, future);
        JSONClientSingleton.getInstance(appContext).addToRequestQueue(request);
        final Gson gson = TestUtils.getGsonFromBuilder();
        try {
            JSONObject response = future.get(); // this will block
            Block retrievedBlocks = gson.fromJson(response.toString(), Block.class);
            assertNotNull(retrievedBlocks);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void TestJsonMinerSyncReq() {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Utils.getMinersStatsUrl(sharedPreferences), new JSONObject(), future, future);
        JSONClientSingleton.getInstance(appContext).addToRequestQueue(request);
        Gson gson = TestUtils.getGsonFromBuilder();
        try {
            assertNotNull(getFirstMinerAddress(future, gson));
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }
    }

    private String getFirstMinerAddress(RequestFuture<JSONObject> future, Gson gson) throws ExecutionException, InterruptedException {
        JSONObject response = future.get(); // this will block
        MinerRoot retrieved = gson.fromJson(response.toString(), MinerRoot.class);
        assertNotNull(retrieved);
        HashMap<String, Miner> map = retrieved.getMiners();
        String[] minerArr = new String[map.size()];
        minerArr = map.keySet().toArray(minerArr);
        assertNotNull(minerArr);
        return minerArr[0];
        //assertNotNull(minerAddr);
    }


    @Test
    public void TestJsonWalletSyncReq() {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Utils.getMinersStatsUrl(sharedPreferences), new JSONObject(), future, future);
        JSONClientSingleton.getInstance(appContext).addToRequestQueue(request);
        Gson gson = TestUtils.getGsonFromBuilder();
        String addr = null;
        try {
            addr = getFirstMinerAddress(future, gson);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }

        assertNotNull(addr);

        RequestFuture<JSONObject> futuress = RequestFuture.newFuture();
        try {
            JsonObjectRequest requesst = new JsonObjectRequest(Request.Method.GET, Utils.getWalletStatsUrl(sharedPreferences) + addr, new JSONObject(), futuress, futuress);
            JSONClientSingleton.getInstance(appContext).addToRequestQueue(requesst);
            JSONObject response = futuress.get(); // this will block
            Wallet retrieved = gson.fromJson(response.toString(), Wallet.class);
            assertNotNull(retrieved);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testPersist() {
        // Context of the app under test.
        String mPool = sharedPreferences.getString("poolEnum", "");
        String mCur = sharedPreferences.getString("curEnum", "");
        PoolDbHelper db = PoolDbHelper.getInstance(appContext, PoolEnum.valueOf(mPool), CurrencyEnum.valueOf(mCur));
        db.logHomeStats(new HomeStats());
        assertNotNull(db.getLastHomeStats(1));
    }

    @Test
    public void testPersistClean() {
        // Context of the app under test.
        String mPool = sharedPreferences.getString("poolEnum", "");
        String mCur = sharedPreferences.getString("curEnum", "");
        PoolDbHelper db = PoolDbHelper.getInstance(appContext, PoolEnum.valueOf(mPool), CurrencyEnum.valueOf(mCur));
        Calendar oldDate = Calendar.getInstance();
        oldDate.add(Calendar.MONTH, -10);
        HomeStats oldData = new HomeStats();
        oldData.setNow(oldDate);
        db.logHomeStats(oldData);//persist stale data
        int prevHistorySize = db.getLastHomeStats(null).size();
        PoolDbHelper.cleanOldData(db.getWritableDatabase());
        assertNotEquals(prevHistorySize, db.getLastHomeStats(null).size());
    }

}
