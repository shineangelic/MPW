package it.angelic.mpw;


import org.junit.Test;

import it.angelic.mpw.model.jsonpojos.home.HomeStats;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PojoUnitTest {


    @Test
    public void testPojo1() {
        HomeStats sta = new HomeStats();
        sta.setCandidatesTotal(23);
        assertNotNull(sta.getNow());
    }

    @Test
    public void testPojoWallet() {
        Wallet sta = new Wallet();
        sta.setHashrate(76237623787283723L);
        assertNotNull(sta.getWorkers());
        assertNotNull(sta.getPayments());
        assertNotNull(sta.getHashrate());
    }
}