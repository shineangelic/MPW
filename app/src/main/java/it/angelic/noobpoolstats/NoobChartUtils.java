package it.angelic.noobpoolstats;

import android.graphics.Color;
import android.widget.TextView;

import org.apache.commons.collections4.map.LinkedMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import im.dacer.androidcharts.LineView;
import it.angelic.noobpoolstats.model.jsonpojos.home.HomeStats;
import it.angelic.noobpoolstats.model.jsonpojos.wallet.Wallet;

/**
 * Created by shine@angelic.it on 11/09/2017.
 */

class NoobChartUtils {
    static void drawHashrateHistory(TextView hashText, LinkedMap<Date, HomeStats> storia, LineView chart) {
        ArrayList<Integer> dataList = new ArrayList<>();
        ArrayList<String> labelsArr = new ArrayList<>();
        Set<Date> dates = storia.keySet();
        HomeStats campione = storia.values().iterator().next();
        for (Date date2 : dates) {
            labelsArr.add(MainActivity.yearFormat.format(date2));
            dataList.add(Utils.condenseHashRate(storia.get(date2).getHashrate()));
        }
        hashText.setText("Pool Hashrate History (now: " + Utils.formatHashrate(campione.getHashrate()) + ")");
        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
        chart.setDrawDotLine(false); //optional
        chart.setBottomTextList(labelsArr);
        chart.setColorArray(new int[]{Color.BLACK, Color.GREEN, Color.GRAY, Color.CYAN});
        chart.setDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)
    }

    static void drawDifficultyHistory(TextView textViewNetDiffTitle, LinkedMap<Date, HomeStats> storia, LineView chart) {
        ArrayList<Integer> dataList = new ArrayList<>();
        ArrayList<String> labelsArr = new ArrayList<>();
        Set<Date> dates = storia.keySet();
        String nodeN = "";
        for (Date date2 : dates) {
            labelsArr.add(MainActivity.yearFormat.format(date2));
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
        chart.setBottomTextList(labelsArr);
        chart.setColorArray(new int[]{Color.BLACK, Color.GREEN, Color.GRAY, Color.CYAN});
        chart.setDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)
    }

    static void drawWorkersHistory(LineView chart, LinkedMap<Date, Wallet> storia) {
        ArrayList<Integer> dataList = new ArrayList<>();
        ArrayList<String> labelsArr = new ArrayList<>();
        Set<Date> dates = storia.keySet();
        String nodeN = "";
        for (Date date2 : dates) {
            dataList.add((storia.get(date2).getWorkersOnline()));
            labelsArr.add(MainActivity.yearFormat.format(date2));
        }

        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
        chart.setDrawDotLine(false); //optional
        chart.setBottomTextList(labelsArr);
        chart.setColorArray(new int[]{Color.BLACK, Color.GREEN, Color.GRAY, Color.CYAN});
        chart.setDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)
    }
}
