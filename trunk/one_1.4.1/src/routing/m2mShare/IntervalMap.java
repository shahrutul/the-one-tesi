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
        intervals.addElement(new Interval(0,length-1, true));
    }
    
  
    /**
     * Insert a new interval into the IntervalMap.
     * Precondition: intervals never overlap
     * @param i the interval to insert
     */
    public void orderedInsert(Interval i) {
    	if(i==null){
    		//System.err.println("insert NULL");   
    		return;
    	}
    	//System.err.println("insert "+ i);   
    	
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
	
	/**
	 * Insert some new data (not free space) in the IntervalMap
	 * @param startingPoint the point where to start the insertion
	 * @param size how much data to insert
	 */
	public void insertData(int startingPoint, int size){
		//System.err.println("INSERT_DATA -> "+size);
		int remainingData = size;
		int newIntervalStart = startingPoint;
		
		while(remainingData > 0){
			//System.err.println("remaining data "+remainingData);
			int indexInterval = getContainingIndex(newIntervalStart);
			Interval intervalToDivide = intervals.remove(indexInterval);
			Interval[] newIntervals = intervalToDivide.split(newIntervalStart);
			//insert Interval before startingPoint
			orderedInsert(newIntervals[0]);
			
			//divide the remaining space of the Interval			
			if(newIntervals[1] == null){
				//System.err.println("newIntervals[1] == null");
				mergeIntervals();
				return;
			}
			if(remainingData == newIntervals[1].size()){
				//System.err.println("remainingData == newIntervals[1].size()");
				newIntervals[1].setFreeSpace(false);
				orderedInsert(newIntervals[1]);
				mergeIntervals();
				return;
			} 
			else if(remainingData < newIntervals[1].size()){
				//System.err.println("remainingData < newIntervals[1].size()");
				int splitPoint = newIntervals[1].getLowerEnd()+remainingData;
				Interval[] newIntervalsAfter = newIntervals[1].split(splitPoint);
				if(newIntervalsAfter[0] != null){
					//System.err.println("newIntervalsAfter[0] == null");
					newIntervalsAfter[0].setFreeSpace(false);
				}
				else{
					//System.err.println("newIntervalsAfter[1] == null");
					newIntervalsAfter[1].setFreeSpace(false);
				}				
				orderedInsert(newIntervalsAfter[0]);
				orderedInsert(newIntervalsAfter[1]);
				mergeIntervals();
				return;
			}
			else if(remainingData > newIntervals[1].size()){
				//System.err.println("remainingData > newIntervals[1].size()");
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
	
	/**
	 * Merge all the near intervals with same characteristics (free or not free space)
	 * into a single interval. Does it for all the IntervalMap
	 */
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

	/**
	 * Get the index of the Interval containing point passed
	 * @param point the point to search for
	 * @return the index if the Interval containing the point
	 */
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
	
	/**
	 * Return the sum of free space from a startingPoint to the end of the IntervalMap
	 * @param startPoint the point (included) from which to start counting the free space
	 * @return
	 */
	public int getMinFreeSpace(int startPoint){		
		int i = getContainingIndex(startPoint);
		//no starting interval found
		if(i<0){
			return 0;
		}
		int freeSpace=0;
		//for the starting interval add only 
		//remaining space from the startingPoint
		if(intervals.get(i).isFreeSpace()){
			freeSpace += (intervals.get(i).getUpperEnd() - startPoint +1);
		}
		i++;
		for(;i<intervals.size(); i++){			
			if(intervals.get(i).isFreeSpace()){
				freeSpace += intervals.get(i).size();
			}
		}
		return freeSpace;
	}
	
	/** 
	 * @return the sum of free bytes in this IntervalMap
	 */
	public int getTotalFreeSpace(){
		int freeSpace=0;
		for(Interval i: intervals){
			if(i.isFreeSpace()){
				freeSpace += i.size();
			}
		}
		return freeSpace;
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

	public int[] getStartingPoints(int n) {		
		int[] points = new int[n];
		if(n==1){
			points[0]=0;
		}
		else{
			for(int p=0; p<n-1; p++){
				int d = (1+2*p)/(2*(n-1))*(length-1);
				points[p] = d;
				System.err.println(d);
			}
		}
		
		return points;
	}
	
}
