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
        intervals.addElement(new Interval(0,length-1));
    }
    
  //precondition: intervals never overlap
    public void orderedInsert(Interval i) {
    	if(i==null){
    		System.err.println("insert NULL");   
    		return;
    	}
    	System.err.println("insert "+ i);   
    	
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
		System.err.println("INSERT_DATA -> "+size);
		int remainingData = size;
		int newIntervalStart = startingPoint;
		
		while(remainingData > 0){
			System.err.println("remaining data "+remainingData);
			int indexInterval = getContainingIndex(newIntervalStart);
			Interval intervalToDivide = intervals.remove(indexInterval);
			Interval[] newIntervals = intervalToDivide.split(newIntervalStart);
			//insert Interval before startingPoint
			orderedInsert(newIntervals[0]);
			
			//divide the remaining space of the Interval			
			if(newIntervals[1] == null){
				System.err.println("newIntervals[1] == null");
				mergeIntervals();
				return;
			}
			if(remainingData == newIntervals[1].size()){
				System.err.println("remainingData == newIntervals[1].size()");
				newIntervals[1].setFreeSpace(false);
				orderedInsert(newIntervals[1]);
				mergeIntervals();
				return;
			} 
			else if(remainingData < newIntervals[1].size()){
				System.err.println("remainingData < newIntervals[1].size()");
				int splitPoint = newIntervals[1].getLowerEnd()+remainingData;
				Interval[] newIntervalsAfter = newIntervals[1].split(splitPoint);
				if(newIntervalsAfter[0] != null){
					System.err.println("newIntervalsAfter[0] == null");
					newIntervalsAfter[0].setFreeSpace(false);
				}
				else{
					System.err.println("newIntervalsAfter[1] == null");
					newIntervalsAfter[1].setFreeSpace(false);
				}				
				orderedInsert(newIntervalsAfter[0]);
				orderedInsert(newIntervalsAfter[1]);
				mergeIntervals();
				return;
			}
			else if(remainingData > newIntervals[1].size()){
				System.err.println("remainingData > newIntervals[1].size()");
				newIntervals[1].setFreeSpace(false);
				orderedInsert(newIntervals[1]);
				remainingData -= newIntervals[1].size();
				
				newIntervalStart = newIntervals[1].getUpperEnd() +1;
				if(newIntervalStart >= this.length){
					newIntervalStart = 0;
				}	
			}
		}
		
	}
	
	private void mergeIntervals(){
		if(intervals.size() <= 1){
			return;
		}
		int i=0;
		while(i<intervals.size()-1){
			while(intervals.get(i).isFreeSpace() == intervals.get(i+1).isFreeSpace()){
				Interval newInterval = Interval.merge(intervals.remove(i), intervals.remove(i));
				orderedInsert(newInterval);
			}
			i++;
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

	@Override
	public String toString() {
		String s = "";
		for(int i=0; i<intervals.size(); i++){
			Interval temp = intervals.get(i);
			s+=temp;
		}
		return s;
	}
	
}
