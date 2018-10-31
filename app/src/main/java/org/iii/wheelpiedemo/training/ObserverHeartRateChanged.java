package org.iii.wheelpiedemo.training;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.lang.IndexOutOfBoundsException;

public class ObserverHeartRateChanged {
    // private getHeartRateRecord; (from api call)
    // private getCourseType; (from api call)
    private Long startTime = null;
    private ArrayList<HashMap<Long, String>> rules;
    private HashMap<String, HashMap<String, Integer>> hrZoneRange;
    private Integer currRuleIdx = 0;

    public ObserverHeartRateChanged(String courseType) {
        rules = getRules(courseType);
        hrZoneRange = getHeartRateZoneRange(123,456);
    }

    // Get HeartRate Zone
    private HashMap<String, HashMap<String, Integer>> getHeartRateZoneRange(int restingHeartRate, int maxHeartRate ){
//        return {
//            'E': {'min': 60, 'max': 80},
//            'M': {'min': 60, 'max': 80},
//            'A': {'min': 60, 'max': 80},
//            'T': {'min': 60, 'max': 80},
//            'I': {'min': 60, 'max': 80},
//            'R': {'min': 60, 'max': 80},
//        }
        HashMap<String, Integer> eRangeMap = new HashMap();
        eRangeMap.put("min", 90);
        eRangeMap.put("max", 120);
        HashMap<String, Integer> mRangeMap = new HashMap();
        mRangeMap.put("min", 50);
        mRangeMap.put("max", 60);
        HashMap<String, Integer> aRangeMap = new HashMap();
        aRangeMap.put("min", 65);
        aRangeMap.put("max", 85);

        HashMap<String, HashMap<String, Integer>> hrZoneMap = new HashMap();
        hrZoneMap.put("E", eRangeMap);
        hrZoneMap.put("M", mRangeMap);
        hrZoneMap.put("A", aRangeMap);

        return hrZoneMap;
    }

    // Get HeartRate rules ( To know the HeartRate Zone for each time frame. )
    private ArrayList<HashMap<Long, String>> getRules(String courseType){
        ArrayList<HashMap<Long, String>> rules = new ArrayList();
        HashMap<Long, String> rule1 = new HashMap();
        rule1.put(TimeUnit.MINUTES.toMillis(1), "E");
        rules.add(rule1);
        HashMap<Long, String> rule2 = new HashMap();
        rule2.put(TimeUnit.MINUTES.toMillis(2), "M");
        rules.add(rule2);
        HashMap<Long, String> rule3 = new HashMap();
        rule3.put(TimeUnit.MINUTES.toMillis(3), "A");
        rules.add(rule3);
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
                        Log.d("HeartRateSupervisor", "跑快一點");
                    }else if((int)newValue > rangeMax){
                        Log.d("HeartRateSupervisor", "跑慢一點");
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
