package org.iii.wheelpiedemo.training;
import android.util.Log;
import java.util.Observable;
import java.util.Observer;

public class ObserverHeartRateChanged {

    // Get HeartRate rules ( To know the Valid HeartRate Range for each time frame. )

    public Observer HeartRateChanged = new Observer() {
        @Override
        public void update(Observable o, Object newValue) {
            // Get current time
            // Check if current HeartRate is valid ( by comparing with the rules )
            // Take actions depending on comparing result.
            Log.d("HeartRateSupervisor", "HeartRate has changed, new value:"+ (int) newValue);
        }
    };
}
