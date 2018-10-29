package org.iii.wheelpiedemo.training;
import java.util.Observable;

public class ObservableHeartRate extends Observable {

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        this.setChanged();
        this.notifyObservers(value);
    }

}
