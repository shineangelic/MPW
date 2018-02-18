package it.angelic.mpw.model.db;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.Calendar;
import java.util.Date;

import it.angelic.mpw.R;
import it.angelic.mpw.model.jsonpojos.home.HomeStats;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

/**
 * Created by shine@angelic.it on 07/09/2017.
 */

public class PoolQueryGrouper {


    public static LinkedMap<Date, HomeStats> groupAvgQueryResult(LinkedMap<Date, HomeStats> queryResult, GranularityEnum radioCheckedId) {

        if (queryResult.isEmpty())
            return queryResult;

        LinkedMap<Date, HomeStats> ret = new LinkedMap<>();
        Calendar firstDate = Calendar.getInstance();
        firstDate.setTime(queryResult.keySet().iterator().next());
        Calendar firstDateOut = Calendar.getInstance();
        firstDateOut.setTime(firstDate.getTime());
        int calendarGranularity = Calendar.DATE;
        switch (radioCheckedId) {
            case DAY:
                break;
            case HOUR:
                calendarGranularity = Calendar.HOUR;
                break;
            case MINUTE:
                calendarGranularity = Calendar.MINUTE;
                break;
        }
        firstDateOut.add(calendarGranularity, 1);
        int divideCnt = 0;
        int totCnt = 0;
        HomeStats avgSet = new HomeStats();
        for (HomeStats current : queryResult.values()) {
            divideCnt++;
            totCnt++;
            //aggiorna medie
            avgSet.setHashrate(avgSet.getHashrate() + current.getHashrate());
            avgSet.setCandidatesTotal(avgSet.getCandidatesTotal() + current.getCandidatesTotal());
            avgSet.setImmatureTotal(avgSet.getImmatureTotal() + current.getImmatureTotal());
            avgSet.setMaturedTotal(avgSet.getMaturedTotal() + current.getMaturedTotal());
            long curDif = avgSet.getNodes().get(0).getDifficulty() == null ? 0L : Long.parseLong(avgSet.getNodes().get(0).getDifficulty());
            avgSet.getNodes().get(0).setDifficulty("" + (curDif + Long.parseLong(current.getNodes().get(0).getDifficulty())));
            avgSet.getNodes().get(0).setName(current.getNodes().get(0).getName());//sovrascrive e amen

            Calendar cursorDate = Calendar.getInstance();
            cursorDate.setTime(current.getNow().getTime());
            //Log.d(TAG, "firstDateOut" + firstDateOut.getTime());
            //Log.d(TAG, "cursorDate" + cursorDate.getTime());
            if (cursorDate.after(firstDateOut) || totCnt == queryResult.values().size()) {
                //Log.d(TAG, " calcola medie " + ret.keySet().size());
                //fase finita, calcola medie e vai
                avgSet.setHashrate(avgSet.getHashrate() / divideCnt);
                avgSet.setCandidatesTotal(avgSet.getCandidatesTotal() / divideCnt);
                avgSet.setImmatureTotal(avgSet.getImmatureTotal() / divideCnt);
                avgSet.setMaturedTotal(avgSet.getMaturedTotal() / divideCnt);
                avgSet.getNodes().get(0).setDifficulty("" + Long.parseLong(avgSet.getNodes().get(0).getDifficulty()) / divideCnt);

                divideCnt = 0;
                ret.put(firstDate.getTime(), avgSet);

                avgSet = new HomeStats();
                firstDate.setTime(cursorDate.getTime());
                firstDateOut.setTime(firstDate.getTime());
                firstDateOut.add(calendarGranularity, 1);
            }
        }

        return ret;
    }

    public static LinkedMap<Date, Wallet> groupAvgWalletQueryResult(LinkedMap<Date, Wallet> queryResult, GranularityEnum radioCheckedId) {
        if (queryResult.isEmpty())
            return queryResult;

        LinkedMap<Date, Wallet> ret = new LinkedMap<>();
        Calendar firstDate = Calendar.getInstance();
        firstDate.setTime(queryResult.keySet().iterator().next());
        Calendar firstDateOut = Calendar.getInstance();
        firstDateOut.setTime(firstDate.getTime());
        int calendarGranularity = Calendar.DATE;
        switch (radioCheckedId) {
            case DAY:
                break;
            case HOUR:
                calendarGranularity = Calendar.HOUR;
                break;
            case MINUTE:
                calendarGranularity = Calendar.MINUTE;
                break;
        }
        firstDateOut.add(calendarGranularity, 1);
        int divideCnt = 0;
        int totCnt = 0;
        Wallet avgSet = new Wallet();
        for ( Date cursorDates : queryResult.keySet()) {
            Wallet current = queryResult.get(cursorDates);
            divideCnt++;
            totCnt++;
            //aggiorna medie
            avgSet.setHashrate(avgSet.getHashrate() + current.getHashrate());
            avgSet.setWorkersOnline(avgSet.getWorkersOnline() + current.getWorkersOnline());
            avgSet.getStats().setBalance(avgSet.getStats().getBalance() + current.getStats().getBalance());

            Calendar cursorDate = Calendar.getInstance();
            cursorDate.setTime(cursorDates);
            //Log.d(TAG, "firstDateOut" + firstDateOut.getTime());
            //Log.d(TAG, "cursorDate" + cursorDate.getTime());
            if (cursorDate.after(firstDateOut) || totCnt == queryResult.values().size()) {
                //Log.d(TAG, " calcola medie " + ret.keySet().size());
                //fase finita, calcola medie e vai
                avgSet.setHashrate(avgSet.getHashrate() / divideCnt);
                avgSet.setWorkersOnline(avgSet.getWorkersOnline()/ divideCnt);
                avgSet.getStats().setBalance(avgSet.getStats().getBalance() / divideCnt);
                divideCnt = 0;
                ret.put(firstDate.getTime(), avgSet);

                avgSet = new Wallet();
                firstDate.setTime(cursorDate.getTime());
                firstDateOut.setTime(firstDate.getTime());
                firstDateOut.add(calendarGranularity, 1);
            }
        }

        return ret;
    }
}
