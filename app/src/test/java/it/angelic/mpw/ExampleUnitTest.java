package it.angelic.mpw;


import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testUtil1getTimeAgo() {
        String timeAgo = Utils.getTimeAgo(new Date());
        assertNotNull(timeAgo);
        assertTrue(timeAgo.contains("ago"));
    }

    @Test
    public void testScaledTime() {
        String timeAgo = Utils.getScaledTime(3600);
        assertNotNull(timeAgo);
        assertTrue(timeAgo.contains("min"));

        timeAgo = Utils.getScaledTime(36000);
        assertNotNull(timeAgo);
        assertTrue(timeAgo.contains("hr."));
    }

    @Test
    public void testFormatBigNumber() {
        String timeAgo = Utils.formatBigNumber(30000000L);
        assertNotNull(timeAgo);
        assertTrue(timeAgo.contains("M"));

        timeAgo = Utils.formatBigNumber(30000000000L);
        assertTrue(timeAgo.contains("G"));
    }

    @Test
    public void testCondenser() {
        float timeAgo = Utils.condenseHashRate(30000000L);
        assertNotNull(timeAgo);
    }
}