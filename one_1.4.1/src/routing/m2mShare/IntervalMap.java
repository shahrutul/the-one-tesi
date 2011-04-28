package routing.m2mShare;

import java.util.Vector;

public class IntervalMap{

	/**
     * Contains intervals in the range 0, endInterval
     */
    private Vector<Interval> intervals = null;
    /**
     * Upper end of the the interval map
     */
    private int length;
    /**
     * IntervalMap partitions till now
     */
    private int sliceSize;
    /**
     * Marks next interval to be served in the actual partitioning
     */
    private int slicePos;
    /**
     * Pointer(sliceSize, slicePos),
     * Begin point of the current interval being served.
     */
    private int pointer;

    /**
     * Costructor. Empty map with no interval.
     */
    public IntervalMap() {
        intervals = new Vector<Interval>();
    }

    /**
     * Constructor. Creates a map with one interval inside it.
     *
     * @param length int: upperEnd of the interval
     * from this map
     */
    public IntervalMap(int length) {
        this.length = length;
        intervals = new Vector<Interval>();
        intervals.addElement(new Interval(0,length));
    }
    
  //precondition: intervals never overlap
    public void orderedInsert(Interval i) {
    	if(i==null){
    		return;
    	}
    	
        if(intervals.size() == 0) {
            intervals.addElement(i);
            return;
        }
        int mapPos=0, end = intervals.size();
        boolean goOn = true;

        while(mapPos < end && goOn) {
            Interval current = (Interval)intervals.elementAt(mapPos);
            goOn = i.after(current);
            mapPos++;
        }
        int pos = ((mapPos==end) && goOn)? mapPos:mapPos-1;
        intervals.insertElementAt(i, pos);
    }

	public int mapSize() {		
		return length;
	}
	
	public void insertData(int startingPoint, int size){
		int remainingData = size;
		int newIntervalStart = startingPoint;
		while(remainingData > 0){
			int indexInterval = getContainingIndex(newIntervalStart);
			Interval intervalToDivide = intervals.remove(indexInterval);
			Interval[] newIntervals = intervalToDivide.split(newIntervalStart);
			orderedInsert(newIntervals[0]);
			
			
			orderedInsert(newIntervals[1]);			
		}
		
		
	}

	private int getContainingIndex(int point) {
		if(intervals.size() == 0){
			return -1;
		}
		for(int i=0; i< intervals.size(); i++){
			if(intervals.get(i).has(point)){
				return i;
			}
		}
		return -1;
	}

}
