package org.iii.wheelpiedemo.training;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

public class ObserverSpeechChanged {
        //private waitTime --> waitTime between two speech or TtsSpans ????
    private TextToSpeech tts;

    public ObserverSpeechChanged(TextToSpeech tts){
        this.tts = tts;
    }

    public Observer SpeechChanged = new Observer() {
        @Override
        public void update(Observable o, Object newValue) {
            //push newValue into speech Queue if speech queue is empty

            //1. check if speech queu is empty
            //2. if not empty --> do nothing
            //3. if empty, check waitTime
            //4. push newValue into speech Queue if waitTime passes, or do nothing

            tts.speak(newValue.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            Log.d("SpeechTTS", newValue.toString());
        }
    };
}