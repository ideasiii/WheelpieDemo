package org.iii.wheelpiedemo.training;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ObserverHeartRateChanged {
    // private getHeartRateRecord; (from api call)
    // private getCourseType; (from api call)
    private Long startTime = null;
    private LinkedHashMap<Long,String> ruleMap;

    public ObserverHeartRateChanged(String courseType) {
        ruleMap = getRules(courseType);
    }

//    // Get HeartRate Zone
//    private HashMap<String, HashMap<String, Integer>> getHeartRateZone(int restingHeartRate, int maxHeartRate ){
////        return {
////            'E': {'min': 60, 'max': 80},
////            'M': {'min': 60, 'max': 80},
////            'A': {'min': 60, 'max': 80},
////            'T': {'min': 60, 'max': 80},
////            'I': {'min': 60, 'max': 80},
////            'R': {'min': 60, 'max': 80},
////        }
//        HashMap<String, Integer> eRangeMap = new HashMap();
//        eRangeMap.put("min", 60);
//        eRangeMap.put("max", 80);
//        HashMap<String, Integer> mRangeMap = new HashMap();
//        mRangeMap.put("min", 90);
//        mRangeMap.put("max", 120);
//
//        HashMap<String, HashMap<String, Integer>> hrZoneMap = new HashMap();
//        hrZoneMap.put("E", eRangeMap);
//        hrZoneMap.put("M", mRangeMap);
//
//        return hrZoneMap;
//    }

    // Get HeartRate rules ( To know the HeartRate Zone for each time frame. )
    private LinkedHashMap<Long,String> getRules(String courseType){
        LinkedHashMap<Long, String> ruleMap = new LinkedHashMap();
        ruleMap.put(TimeUnit.MINUTES.toMillis(1), "E");
        ruleMap.put(TimeUnit.MINUTES.toMillis(2), "M");
        ruleMap.put(TimeUnit.MINUTES.toMillis(3), "A");
        return ruleMap;
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
            for (Map.Entry<Long,String> entry : ruleMap.entrySet()) {
                Long key = entry.getKey();
                String value = entry.getValue();
                if(diff < key){
                    Log.d("HeartRateSupervisor", "HeartRate is: "+ newValue + " at time(millis) : " + diff + " should be in range : " + value);
                    break;
                }
            }

            // Take actions depending on comparing result.
        }
    };
}
