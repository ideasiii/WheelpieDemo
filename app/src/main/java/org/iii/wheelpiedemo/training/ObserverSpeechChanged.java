package org.iii.wheelpiedemo.training;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

public class ObserverSpeechChanged {
    private TextToSpeech tts;
    private Integer restingInterval = 0;
    private Boolean isSpeaking = false;
    private Boolean isResting = false;

    public ObserverSpeechChanged(TextToSpeech tts){
        this.tts = tts;
    }

    public void setSpeakingStatus(Boolean status){
        isSpeaking = status;

        // To rest after speaking is stopped.
        if(status == false){
            this.rest();
        }
    }

    public void setRestingInterval(Integer restingSec){
        restingInterval = restingSec;
    }

    public void rest(){
        isResting = true;

        // Set the resting timer based on the `restingInterval` property.
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    isResting = false;
                }
            },
            this.restingInterval
        );
    }

    public Observer SpeechChanged = new Observer() {
        @Override
        public void update(Observable o, Object newValue) {
            Log.d("SpeechTTS", "isSpeakingStatus is : "+ isSpeaking);
            if(!isSpeaking && !isResting){
                tts.speak(newValue.toString(), TextToSpeech.QUEUE_FLUSH, null, "MyID");
                Log.d("SpeechTTS", newValue.toString());
            }
        }
    };
}