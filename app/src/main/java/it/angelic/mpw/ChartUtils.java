package it.angelic.mpw;

import android.graphics.Color;
import android.widget.TextView;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import im.dacer.androidcharts.LineView;
import it.angelic.mpw.model.HomeStatsChartData;
import it.angelic.mpw.model.db.GranularityEnum;
import it.angelic.mpw.model.jsonpojos.home.HomeStats;
import it.angelic.mpw.model.jsonpojos.wallet.Payment;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

/**
 * Utils class to fill charts backing objects
 * <p>
 * Created by shine@angelic.it on 11/09/2017.
 */

class ChartUtils {
    static void drawHashrateHistory(TextView titleTextView, LinkedMap<Date, HomeStatsChartData> storia, LineView chart, GranularityEnum grane) {
        SummaryStatistics stats = new SummaryStatistics();
        ArrayList<Float> dataList = new ArrayList<>();
        ArrayList<Float> dataListMax = new ArrayList<>();
        ArrayList<Float> dataListMin = new ArrayList<>();
        ArrayList<String> labelsArr = new ArrayList<>();
        List<Date> dates = storia.asList();
       // HomeStats campione = storia.values().iterator().next();
        for (Date date2 : dates) {
            labelsArr.add(getLabelFormat(grane, date2));
            dataList.add(Utils.condenseHashRate(storia.get(date2).getHashrate()));
            dataListMax.add(Utils.condenseHashRate(storia.get(date2).getHashrateMax()));
            dataListMin.add(Utils.condenseHashRate(storia.get(date2).getHashrateMin()));
            stats.addValue(storia.get(date2).getHashrate());
        }

            titleTextView.setText("Hashrate History chart "
                    + "(avg: " + Utils.formatHashrate((long) stats.getMean())
                    + ", max: " + Utils.formatHashrate((long) stats.getMax())
                    + ", min: " + Utils.formatHashrate((long) stats.getMin())
                    + ", now: " + Utils.formatHashrate(storia.get(dates.get(dataList.size()-1)).getHashrate())
                    + ", std dev: " + Utils.formatHashrate((long) stats.getStandardDeviation())
                    + ")");

        ArrayList<ArrayList<Float>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
        dataLists.add(dataListMax);
        dataLists.add(dataListMin);
        chart.setShowPopup(LineView.SHOW_POPUPS_All);
        chart.setDrawDotLine(false); //optional
        chart.setBottomTextList(labelsArr);
        chart.setColorArray(new int[]{Color.DKGRAY, Color.BLUE, Color.RED});
        chart.setFloatDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)
    }

    public static void drawWalletHashRateHistory(TextView titleTextView, LineView chart, LinkedMap<Date, Wallet> dateWalletLinkedMap, GranularityEnum grane) {
        SummaryStatistics stats = new SummaryStatistics();
        ArrayList<Float> dataList = new ArrayList<>();
        ArrayList<String> labelsArr = new ArrayList<>();
        List<Date> dates = dateWalletLinkedMap.asList();
        Wallet campione = dateWalletLinkedMap.values().iterator().next();
        for (Date date2 : dates) {
            labelsArr.add(getLabelFormat(grane, date2));
            dataList.add(Utils.condenseHashRate(dateWalletLinkedMap.get(date2).getHashrate()));
            stats.addValue(dateWalletLinkedMap.get(date2).getHashrate());
        }

        titleTextView.setText("Wallet Hashrate History "
                + "(avg: " + Utils.formatHashrate((long) stats.getMean())
                + ", max: " + Utils.formatHashrate((long) stats.getMax())
                + ", min: " + Utils.formatHashrate((long) stats.getMin())
                + ", now: " + Utils.formatHashrate(dateWalletLinkedMap.get(dates.get(dataList.size()-1)).getHashrate())
                + ", std dev: " + Utils.formatHashrate((long) stats.getStandardDeviation())
                + ")");

        ArrayList<ArrayList<Float>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
        chart.setShowPopup(LineView.SHOW_POPUPS_All);
        chart.setDrawDotLine(false); //optional
        chart.setBottomTextList(labelsArr);
        chart.setColorArray(new int[]{Color.DKGRAY, Color.CYAN});
        chart.setFloatDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)
    }

    private static String getLabelFormat(GranularityEnum radioId, Date date2) {
        switch (radioId) {
            case DAY:
                return MainActivity.dayFormat.format(date2);
            case HOUR:
                return MainActivity.hourFormat.format(date2);
            case MINUTE:
                return MainActivity.yearFormat.format(date2);
        }
        return date2.toString();
    }

    static void drawDifficultyHistory(TextView textViewNetDiffTitle, LinkedMap<Date, HomeStatsChartData> storia, LineView chart, GranularityEnum grane) {
        ArrayList<Integer> dataList = new ArrayList<>();
        ArrayList<String> labelsArr = new ArrayList<>();
        Set<Date> dates = storia.keySet();
        String nodeN = "";
        for (Date date2 : dates) {
            labelsArr.add(getLabelFormat(grane, date2));
            nodeN = storia.get(date2).getNodes().get(0).getName();
            BigInteger diff = BigInteger.valueOf(Long.valueOf(storia.get(date2).getNodes().get(0).getDifficulty()));
            float kilo = diff.longValue() / 1024f;
            float mega = kilo / 1024f;
            float giga = mega / 1024f;
            float tera = giga / 1024f;
            float peta = tera / 1024f;
            dataList.add(((int) tera));
        }

        textViewNetDiffTitle.setText(String.format("Difficulty History (TeraH - node:%s)", nodeN));
        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
        chart.setDrawDotLine(false); //optional
        chart.setShowPopup(LineView.SHOW_POPUPS_All);
        chart.setBottomTextList(labelsArr);
        chart.setColorArray(new int[]{Color.DKGRAY, Color.CYAN});
        chart.setDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)
    }

    static void drawWorkersHistory(LineView chart, LinkedMap<Date, Wallet> storia, GranularityEnum checkedRadioButtonId) {
        ArrayList<Integer> dataList = new ArrayList<>();
        ArrayList<String> labelsArr = new ArrayList<>();
        Set<Date> dates = storia.keySet();
        //String nodeN = "";
        for (Date date2 : dates) {
            dataList.add((storia.get(date2).getWorkersOnline()));
            labelsArr.add(getLabelFormat(checkedRadioButtonId, date2));
        }

        chart.setShowPopup(LineView.SHOW_POPUPS_All);
        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
        chart.setDrawDotLine(false); //optional
        chart.setBottomTextList(labelsArr);
        chart.setColorArray(new int[]{Color.DKGRAY, Color.CYAN});
        chart.setDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)
    }


    public static void drawPaymentsHistory(LineView chart, Wallet retrieved) {
        ArrayList<Float> dataList = new ArrayList<>();
        ArrayList<String> labelsArr = new ArrayList<>();
        float accumulator = 0;
        List<Payment> paymnts = retrieved.getPayments();
        Collections.reverse(paymnts);//mostro in ordine
        for (final Payment thispay : paymnts) {
            accumulator += thispay.getAmount() / 1000000000F;
            dataList.add(accumulator);
            labelsArr.add(MainActivity.dayFormat.format(thispay.getTimestamp()));
        }
        chart.setDrawDotLine(false); //optional
        chart.setBottomTextList(labelsArr);

        chart.setColorArray(new int[]{Color.DKGRAY, Color.CYAN});
        ArrayList<ArrayList<Float>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
        chart.setFloatDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)
    }
}
