package org.iii.wheelpiedemo.training;
import java.util.Observable;

public class ObservableSpeech extends Observable{

    private String speech;

    public String getValue() {
        return speech;
    }

    public void setValue(String speech) {
        this.speech = speech;
        this.setChanged();
        this.notifyObservers(speech);
    }

}
