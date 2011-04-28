package routing.m2mShare;


import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

public class MapEnumerator implements Enumeration {

    private Vector interval = null;
    private int currentPosition=0;

    public MapEnumerator(Vector interval) {
        this.interval = interval;
    }

    public boolean hasMoreElements() {
        return currentPosition < interval.size();
    }

    public Object nextElement() throws NoSuchElementException {
        if(currentPosition >= interval.size()) throw new NoSuchElementException();
        return interval.elementAt(currentPosition++);
    }

    public void reset() {
        currentPosition = 0;
    }
}
