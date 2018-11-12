package org.iii.wheelpiedemo.training;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.common.Logs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;
import java.lang.IndexOutOfBoundsException;
import org.iii.wheelpiedemo.common.RestApiHeaderClient;
import org.json.JSONObject;


public class ObserverHeartRateChanged {
    private Integer restHeartRate;
    private Integer maxHeartRate;
    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private static String physicalInfoAPIURL = "https://dsicoach.win/api/user/physicalInfo";
    private String userToken;
    private Long startTime = null;
    private ArrayList<HashMap<Long, String>> rules;
    private HashMap<String, HashMap<String, Integer>> hrZoneRange;
    private Integer currRuleIdx = 0;

    private ObservableSpeech speechContentObservable;
    private Context mContext;

//    public ObserverHeartRateChanged(Context mContext, String courseType, ObservableSpeech speechContentObservable) {
    public ObserverHeartRateChanged(Integer restHeartRate, Integer maxHeartRate, String courseType, ObservableSpeech speechContentObservable) {
        this.restHeartRate = restHeartRate;
        this.maxHeartRate = maxHeartRate;
//        this.mContext = mContext;
        this.rules = getRules(courseType);
        this.speechContentObservable = speechContentObservable;
//        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
//        this.userToken = sharedPref.getString("userToken", null);
        this.hrZoneRange = getHeartRateZoneRange(restHeartRate, maxHeartRate);
    }

    // Get HeartRate Zone
    private HashMap<String, HashMap<String, Integer>> getHeartRateZoneRange(int restHeartRate, int maxHeartRate ){
//        return {
//            'E': {'min': 60, 'max': 80},
//            'M': {'min': 60, 'max': 80},
//            'A': {'min': 60, 'max': 80},
//            'T': {'min': 60, 'max': 80},
//            'I': {'min': 60, 'max': 80},
//            'R': {'min': 60, 'max': 80},
//        }
//        HashMap<String, Integer> eRangeMap = new HashMap();
//        eRangeMap.put("min", 90);
//        eRangeMap.put("max", 120);
//        HashMap<String, Integer> mRangeMap = new HashMap();
//        mRangeMap.put("min", 50);
//        mRangeMap.put("max", 60);
//        HashMap<String, Integer> aRangeMap = new HashMap();
//        aRangeMap.put("min", 65);
//        aRangeMap.put("max", 85);
//
//        HashMap<String, HashMap<String, Integer>> hrZoneMap = new HashMap();
//        hrZoneMap.put("E", eRangeMap);
//        hrZoneMap.put("M", mRangeMap);
//        hrZoneMap.put("A", aRangeMap);
        Integer HRRmin = null;
        Integer HRRmax = null;
        String[] zoneArray = {"E", "M", "T", "A", "I"};
        HashMap<String, HashMap<String, Integer>> hrZoneMap = new HashMap();
        for (String zone : zoneArray) {
            HashMap<String, Integer> rangeMap = new HashMap();
            switch (zone) {
                case "E":
                    HRRmin = 59;
                    HRRmax = 74;
                    break;
                case "M":
                    HRRmin = 74;
                    HRRmax = 84;
                    break;
                case "T":
                    HRRmin = 84;
                    HRRmax = 88;
                    break;
                case "A":
                    HRRmin = 88;
                    HRRmax = 95;
                    break;
                case "I":
                    HRRmin = 95;
                    HRRmax = 100;
            }
            rangeMap.put("min", (maxHeartRate-restHeartRate)*HRRmin/100+restHeartRate);
            rangeMap.put("max", (maxHeartRate-restHeartRate)*HRRmax/100+restHeartRate);
            hrZoneMap.put(zone, rangeMap);
            Log.d("HeartRateRules", zone+HRRmin+HRRmax);
        }
        return hrZoneMap;
    }

    // Get HeartRate rules ( To know the HeartRate Zone for each time frame. )
    private ArrayList<HashMap<Long, String>> getRules(String courseType){
//        ArrayList<HashMap<Long, String>> rules = new ArrayList();
//        HashMap<Long, String> rule1 = new HashMap();
//        rule1.put(TimeUnit.MINUTES.toMillis(1), "E");
//        rules.add(rule1);
//        HashMap<Long, String> rule2 = new HashMap();
//        rule2.put(TimeUnit.MINUTES.toMillis(2), "M");
//        rules.add(rule2);
//        HashMap<Long, String> rule3 = new HashMap();
//        rule3.put(TimeUnit.MINUTES.toMillis(3), "A");
//        rules.add(rule3);
//        return rules;
        ArrayList<HashMap<Long, String>> rules = new ArrayList();
        String[] zoneTimePairs = courseType.split("\\+");
        int time = 0;
        for (String zoneTimePair : zoneTimePairs){
            HashMap<Long, String> rule = new HashMap();
            String zone = zoneTimePair.substring(0,1);
            time += Integer.parseInt(zoneTimePair.substring(1));
            rule.put(TimeUnit.MINUTES.toMillis(time), zone);
            rules.add(rule);
        }
        Log.d("HeartRateRules", Arrays.toString(rules.toArray()));
        return rules;
    }

    public Observer HeartRateChanged = new Observer() {
        @Override
        public void update(Observable o, Object newValue) {
            // Get current time
            Long currentTime = Calendar.getInstance().getTime().getTime();
            if(startTime == null){
                startTime = currentTime;
            }
            Long diff = currentTime - startTime;

            // Check if current HeartRate is valid ( by comparing with the rules )
            try {
                HashMap<Long, String> rule = rules.get(currRuleIdx);
                Long timeBefore = rule.entrySet().iterator().next().getKey();
                String hrZone = rule.entrySet().iterator().next().getValue();
                if(diff < timeBefore){
                    HashMap<String, Integer> range = hrZoneRange.get(hrZone);
                    Integer rangeMin = range.get("min");
                    Integer rangeMax = range.get("max");
                    Log.d("HeartRateSupervisor", "HeartRate is: "+ newValue + " at time(millis) : " + diff + " should be in range : " + rangeMin + " ~ " + rangeMax);
                    if((int)newValue < rangeMin){
                        speechContentObservable.setValue("心綠低於最小值"+Integer.toString(rangeMin)+"，跑快一點");
                        Log.d("HeartRateSupervisor", "跑快一點");
                    }else if((int)newValue > rangeMax){
                        speechContentObservable.setValue("心綠超過最大值"+Integer.toString(rangeMax)+"，跑慢一點");
                        Log.d("HeartRateSupervisor", "跑慢一點");
                    }else{
                        speechContentObservable.setValue("心綠落在標準範圍內，繼續保持");
                        Log.d("HeartRateSupervisor", "心率落在標準範圍內，繼續保持");
                    }
                }else{
                    currRuleIdx++;
                }
            }catch(IndexOutOfBoundsException e){
                Log.d("HeartRateSupervisor", "HeartRate is: "+ newValue + " at time(millis) : " + diff + " should be in range : None");
            }
        }
    };
}
