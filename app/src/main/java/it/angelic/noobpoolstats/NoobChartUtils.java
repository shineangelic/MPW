package it.angelic.noobpoolstats;

import android.graphics.Color;
import android.widget.TextView;

import org.apache.commons.collections4.map.LinkedMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import im.dacer.androidcharts.LineView;
import it.angelic.noobpoolstats.model.jsonpojos.home.HomeStats;
import it.angelic.noobpoolstats.model.jsonpojos.wallet.Payment;
import it.angelic.noobpoolstats.model.jsonpojos.wallet.Wallet;

/**
 * Created by shine@angelic.it on 11/09/2017.
 */

class NoobChartUtils {
    static void drawHashrateHistory(TextView titleTextView, LinkedMap<Date, HomeStats> storia, LineView chart, int grane) {
        ArrayList<Integer> dataList = new ArrayList<>();
        ArrayList<String> labelsArr = new ArrayList<>();
        Set<Date> dates = storia.keySet();
        HomeStats campione = storia.values().iterator().next();
        for (Date date2 : dates) {
            labelsArr.add(getLabelFormat(grane, date2));
            dataList.add(Utils.condenseHashRate(storia.get(date2).getHashrate()));
        }
        titleTextView.setText("Pool Hashrate History (now: " + Utils.formatHashrate(campione.getHashrate()) + ")");
        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
        chart.setShowPopup(LineView.SHOW_POPUPS_All);
        chart.setDrawDotLine(false); //optional
        chart.setBottomTextList(labelsArr);
        chart.setColorArray(new int[]{Color.DKGRAY, Color.CYAN});
        chart.setDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)
    }

    public static void drawWalletHashRateHistory(TextView titleTextView, LineView chart, LinkedMap<Date, Wallet> dateWalletLinkedMap, int grane) {
        ArrayList<Integer> dataList = new ArrayList<>();
        ArrayList<String> labelsArr = new ArrayList<>();
        Set<Date> dates = dateWalletLinkedMap.keySet();
        Wallet campione = dateWalletLinkedMap.values().iterator().next();
        for (Date date2 : dates) {
            labelsArr.add(getLabelFormat(grane, date2));
            dataList.add(Utils.condenseHashRate(dateWalletLinkedMap.get(date2).getHashrate()));
        }
        titleTextView.setText("Wallet Hashrate History (now: " + Utils.formatHashrate(campione.getHashrate()) + ")");
        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
        chart.setShowPopup(LineView.SHOW_POPUPS_All);
        chart.setDrawDotLine(false); //optional
        chart.setBottomTextList(labelsArr);
        chart.setColorArray(new int[]{Color.DKGRAY, Color.CYAN});
        chart.setDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)
    }

    private static String getLabelFormat(int radioId, Date date2) {
        switch (radioId) {
            case R.id.radioButtonMinutes:
                return MainActivity.yearFormat.format(date2);
            case R.id.radioButtonHours:
                return MainActivity.hourFormat.format(date2);
            case R.id.radioButtonDay:
                return MainActivity.dayFormat.format(date2);
        }
        return date2.toString();
    }

    static void drawDifficultyHistory(TextView textViewNetDiffTitle, LinkedMap<Date, HomeStats> storia, LineView chart, int grane) {
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

    static void drawWorkersHistory(LineView chart, LinkedMap<Date, Wallet> storia, int checkedRadioButtonId) {
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
        for (final Payment thispay :paymnts) {
            accumulator+=thispay.getAmount() / 1000000000F;
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
