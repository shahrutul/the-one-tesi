
package routing.m2mShare;

import java.util.Vector;

/**
 *
 * @author Armir Bujari
 */
public class IntervalMap{

    /**
     * Contains intervals in the range 0, endInterval
     */
    private Vector<Interval> intervals = null;
    /**
     * Max. size (bytes) being shipped out from this map
     */
    private int pcktSize;
    /**
     * Upper end of the the interval map
     */
    private int endInterval;
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
     * Min.number of bytes each interval should overlap,
     * if exeeded partitioners are resetted
     */
    private final int RESET_POINT = 1;//512;
    /**
     *
     */
    private MapEnumerator enumerator = null;

    /**
     * Costructor. Empty map with no interval.
     */
    public IntervalMap() {
        intervals = new Vector<Interval>();
    }

    /**
     * Constructor. Creates a map with one interval inside it.
     *
     * @param endInterval int: upperEnd of the interval
     * @param pcktSize int: Max. nr of bytes being shipped out
     * from this map
     */
    public IntervalMap(int endInterval, int pcktSize) {
        this.endInterval = endInterval;
        this.pcktSize = pcktSize;
        intervals = new Vector<Interval>();
        intervals.addElement(new Interval(0,endInterval));
    }

    public IntervalMap(int[] restOfMap) {
    	this.pcktSize = 1024;
    	intervals = new Vector<Interval>();
    	int lowerEnd, upperEnd;
    	try{
    		for(int i=1;i<restOfMap.length; i+=2){
    			lowerEnd = restOfMap[i];
                upperEnd = restOfMap[i+1];
                orderedInsert(new Interval(lowerEnd, upperEnd));
    		}    		
    		this.endInterval = intervals.lastElement().getUpperEnd();
    	}catch(Exception e){}
	}

	/**
     *
     */
    public synchronized int[] cut(boolean delegation) throws Exception {

        if(intervals.size() == 0) throw new Exception("Map is empty");

        /** Base case. */
        if(sliceSize==0) {
            sliceSize = 1;
            return mapOut(0);
        }
        /**
         * Compute raw pointer, doesn't consider marked intervals
         */
        cumputeRawPointer();
        /**
         * Compute pointer skipping junk intervals
         */
        int[] pointerAndInterval = skipFull(pointer);
        int actualPointer = pointerAndInterval[0];//pointer
        int intervalIndex = pointerAndInterval[1];//pointer.interval
        /**
         * There is no interval map to assign with current configuration,
         * reset current configuration and re-compute !
         */
        if(actualPointer==-1) {
            sliceSize *= 2;
            slicePos = 0;
            return cut(delegation);
        /**
         * rawPointer matches actualPointer, meaning pointer is in
         * free data zone
         */
        } else if(pointer == actualPointer) {

            slicePos++;

        }else {
            /**
             * There are some intervals to serve, before check if any slice
             * might be skipped (already downloaded)
             */
            int sSlices = skippedSlices(actualPointer);
            slicePos += sSlices +1;//+1-> increment for next
            pointer = actualPointer;
        }

        /** Reset partitioners */
        if(slicePos >= sliceSize) {
            slicePos = 0;
            sliceSize *= 2;
            if(Math.floor(((endInterval/sliceSize))+0.5f)
                    <= RESET_POINT) {
                sliceSize=0;
            }
        }
        if(delegation) {
            return mapOut(intervalIndex);//mapDelegation(intervalIndex);
        }else {
            return mapLocal(intervalIndex);
        }
    }

    /**
     * OSSSSSSSSSS.
     *
     * The communication protocol is like this:
     *  - Initiator.A previously issued a download request to Source.B
     *      - request has interval to be downloaded (lower, upper):
     *          Data_Request(direct.source) || DTNPendingDownload(dtn)
     * - SourceB(dtn or not) when initiated download communicates first interval
     *   to upload:Data_Response
     *      - Initiator.A marksBusy: A has free sub.intervals inside
     *        upper interval !!
     *      - for each sub.interval A informs B to prepare upload:Data_Request !!
     */

    /**
     * Possible cases:
     *      Case1. bi = ei = -1 all interval was downloaded
     *      Case1.1 b1 > e
     *      Case2. bi = ei != -1:
     *          Case2.1. b=b' and e=e' [b,e] to mark
     *          Case2.2 b!=b' and e=e' [b', e] to mark
     *      Case3. bi != ei:
     *          Case3.1 ei=-1 [b',bi.end] ... interval.size() to mark
     *          Case3.2 ei!=-1 []:
     *              Case3.2.1 e=e': [b', bi.end] ... [ei.init, e] to mark (different intervals to mark) !
     *              Case3.2.2 e!=e': [b', bi.end] to mark !!
     *
     */
    public synchronized Interval[] update(int b, int e) throws Exception {

        if(intervals.size() == 0) return null;
        /** b' and his interval (bi) */
        int[] iAb1 = skipFull(b);
        int b1 = iAb1[0];
        int bi = iAb1[1];
        /** e' and his interval (ei) */
        int[] iAe1 = skipFull(e);
        int e1 = iAe1[0];
        int ei = iAe1[1];

        Vector<Interval> marked = new Vector<Interval>();
        /**
         * Interval already downloaded
         */
        if(bi == ei && ei == -1) return null;//Case1
        if(b1 > e) return null;//Case1.1

        Interval current = (Interval)intervals.elementAt(bi);

        if(b==b1 && b==e) {

            return new Interval[]{markInterval(current,
                                                bi,
                                                b,
                                                b)};
        }
        /**
         * Pointing on the same interval
         */
        if(bi == ei && ei != -1) {//Case2
            if(b==b1 && e==e1) {//Case2.1

                marked.addElement(markInterval(current,
                                                bi,
                                                b-1,
                                                e+1));

            }else if(b!=b1 && e==e1) {//Case2.2

                marked.addElement(markInterval(current,
                                                bi,
                                                b1-1,
                                                e+1));

            }
        /**
         * Pointing on different intervals
         */
        }else if(bi != ei) {//Case3

            if(ei==-1) {//Case3.1

                /**
                 * mark interval
                 */
                marked.addElement(markInterval(current,
                                                bi,
                                                b1-1,
                                                current.getUpperEnd()+1));

                /**
                 * Copy marked elements.
                 * Note: we cannot copy and delete simultaneously
                 * because removeElementAt changes vector indexes.
                 */
                for(int i=bi; i<intervals.size(); i++)
                    marked.addElement(intervals.elementAt(i));
                /**
                 * Now we can delete them
                 */
                for(int i=bi; i<intervals.size(); i++) {
                    intervals.removeElementAt((i==bi)? i:i-1);
                }
            }else {//Case3.2
                if(e==e1) {//Case3.2.1

                    /**
                     * split first interval, mark second downloaded
                     */
                    Interval[] a = current.split(b1-1);

                    intervals.removeElementAt(bi);

                    if(a!=null && a[0]!=null)//current.size() == 1
                        intervals.insertElementAt(a[0], bi);

                    if(a!=null && a[1]!=null) {

                        a[1].setLowerEnd(a[1].getLowerEnd()+1);

                        marked.addElement(a[1]);

                    }else if(a==null) {

                        marked.addElement(current);
                    }
                    /**
                     * split second interval, mark first downloaded.
                     * since removeElementAt if done before changes
                     * indexes inside the vector, recalculate index.
                     */
                    int ei1 = (a!=null && a[0]!=null)? ei:ei-1;

                    Interval current_ = (Interval)intervals.elementAt(ei1);

                    Interval[] c = current_.split(e1+1);

                    intervals.removeElementAt(ei1);

                    if(c!=null && c[1]!=null)
                        intervals.insertElementAt(c[1], ei1);

                    if(c!=null && c[0]!=null) {

                        c[0].setUpperEnd(c[0].getUpperEnd()-1);

                        marked.addElement(c[0]);

                    }else if(c==null) {

                        marked.addElement(current_);
                    }
                    /**
                     * mark in between intervals
                     */
                    for(int i=bi+1; i<ei; i++)
                        marked.addElement(intervals.elementAt(i));

                    for(int i=bi+1; i<ei; i++)
                        intervals.removeElementAt((i==bi+1)? i:i-1);

                }else {//Case3.2.2
                    marked.addElement(markInterval(current,
                                                    bi,
                                                    b1-1,
                                                    current.getUpperEnd()+1));
                }
            }
        }
        if(marked.size()==0) return null;

        Interval[] i = new Interval[marked.size()];
        marked.copyInto(i);
        marked.removeAllElements();
        mergesort(i, 0, i.length);
        return i;
    }

    //precondition: intervals never overlap
    public void orderedInsert(Interval i) {

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

    public void insert(Interval i) {
        intervals.addElement(i);
    }

    /**
     * Return the number of intervals contained by the map
     * @return
     */
    public int mapSize() {return intervals.size();}

    private Interval markInterval(Interval i, int bi, int b, int e) {


        if(b==e) return markPoint(i, bi, b, e);

        intervals.removeElementAt(bi);

        Interval[] a = i.split(b);
        if(a==null)
            a = new Interval[]{null,i};

        Interval[] c = a[1].split(e);

        if(c==null)
            c = new Interval[]{i, null};
        /**
         * Entire interval marked, dont add anything
         */
        if(a[0]==null && c[1]==null)
            return i;

        if(a[0]!=null)
            intervals.insertElementAt(a[0], bi);

        if(c[1]!=null)
            intervals.insertElementAt(c[1], (a[0]!=null)?(bi+1):bi);

        if(c[0]!=null)
            return new Interval(b+1, e-1);

        return null;
    }

    private Interval markPoint(Interval i, int bi, int b, int e) {

        intervals.removeElementAt(bi);

        Interval[] a = i.split(b-1);
        if(a==null)
            a = new Interval[]{null,i};

        Interval[] c = a[1].split(b+1);
        if(c==null)
            c = new Interval[]{i, null};

        if(a[0]==null && c[1]==null)
            return i;

        if(a[0]!=null)
            intervals.insertElementAt(a[0], bi);

        if(c[1]!=null)
            intervals.insertElementAt(c[1], (a[0]!=null)?(bi+1):bi);

        return new Interval(b,b);
    }

    private int[] mapLocal(int intervalIndex) {

       Vector<Integer> tmp = new Vector<Integer>();

       tmp.addElement(new Integer(pointer));

        for(int i=intervalIndex; i<intervals.size(); i++) {
            Interval current = (Interval)intervals.elementAt(0);
            tmp.addElement(new Integer(current.getLowerEnd()));
            tmp.addElement(new Integer(current.getUpperEnd()));
        }
        int[] content = new int[tmp.size()];

        for(int i=0; i<tmp.size(); i++)
            content[i] = ((Integer)tmp.elementAt(i)).intValue();

        tmp.removeAllElements();

        return content;
    }

    public synchronized int[] assignRestofMap() {

        if(intervals.size() < 1) return null;

        Vector<Integer> tmp = new Vector<Integer>();

        Interval firstInterval = (Interval)intervals.elementAt(0);
        tmp.addElement(new Integer(0));
        tmp.addElement(new Integer(firstInterval.getLowerEnd()));
        tmp.addElement(new Integer(firstInterval.getUpperEnd()));

        for(int i=1; i < intervals.size(); i++) {

            Interval current = (Interval)intervals.elementAt(i);
            tmp.addElement(new Integer(current.getLowerEnd()));
            tmp.addElement(new Integer(current.getUpperEnd()));
        }

        int[] content = new int[tmp.size()];

        for(int i=0; i<tmp.size(); i++)
            content[i] = ((Integer)tmp.elementAt(i)).intValue();

        tmp.removeAllElements();

        return content;
    }

    private int[] mapOut(int intervalIndex) {
    	
        Vector<Integer> tmp = new Vector<Integer>();
        int cSize = 0;
        int mapInit = (withinConstraints())? 0:intervalIndex;

        tmp.addElement(new Integer(pointer));
        cSize = 4;
        for(int i=mapInit; i<intervals.size() && cSize < pcktSize; i++) {
            Interval current = (Interval)intervals.elementAt(0);
            tmp.addElement(new Integer(current.getLowerEnd()));
            tmp.addElement(new Integer(current.getUpperEnd()));
            cSize+=2*4;
        }
        int[] content = new int[tmp.size()];

        for(int i=0; i<tmp.size(); i++)
            content[i] = ((Integer)tmp.elementAt(i)).intValue();

        tmp.removeAllElements();

        return content;
    }

    /** 
     * Return the starting point and the starting interval
     * Precondition, there is at least 1 interval 
     * @param position current position of the pointer
     * @return [starting_point , starting_interval_index]
     */
    private int[] skipFull(int position) {

        Interval current = null;
        int positioning = -1;
        if(intervals.size() == 1) {
            current = (Interval)
                        intervals.elementAt(0);
            positioning =
                    current.intervalPosition(position);
            return new int[]{positioning,(
                          positioning!=-1)?0:-1};
        }
        for(int i=0; i< intervals.size(); i++) {
            current = (Interval)
                    intervals.elementAt(i);
            positioning =
                    current.intervalPosition(position);
            if(positioning!=-1)
                return new int[] {positioning,i};
        }
        return new int[]{-1,-1};
    }

    //p = [(pointer/l)*2n]-1
    //mod(1+2p)
    private int skippedSlices(int pointer) {
        int actualSlice = (int)Math.floor(
                ((((pointer*2*sliceSize)
                /endInterval)-1)/2)+0.5f);

        return actualSlice - slicePos;
    }

    private boolean withinConstraints() {
        //calc number of intervals whithin
        int intervalSize = intervals.size();
        //calc number of bytes required for transmission
        int byteSize = intervalSize*2*4;
        return (byteSize <= pcktSize);
    }

    /**
     * [(1+2p)/2n]*l
     */
    public int cumputeRawPointer() {

        pointer = (int)Math.floor(
                    ((1+2*slicePos)*
                    endInterval)/
                    (2*sliceSize)+0.5f);
        return pointer;

    }
    
    private void mergesort(Object[] data, int first, int n)
    {
       int n1; // Size of the first half of the array
       int n2; // Size of the second half of the array
       if (n > 1)
       {
          // Compute sizes of the two halves
          n1 = n / 2;
          n2 = n - n1;

          mergesort(data, first, n1);      // Sort data[first] through data[first+n1-1]
          mergesort(data, first + n1, n2); // Sort data[first+n1] to the end

          // Merge the two sorted halves.
          if(data instanceof Interval[])
               merge((Interval[])data, first, n1, n2);
       }
    }
    
    private void merge(Interval[] data, int first, int n1, int n2)
    // Precondition: data has at least n1 + n2 components starting at data[first]. The first
    // n1 elements (from data[first] to data[first + n1 – 1] are sorted from smallest
    // to largest, and the last n2 (from data[first + n1] to data[first + n1 + n2 - 1]) are also
    // sorted from smallest to largest.
    // Postcondition: Starting at data[first], n1 + n2 elements of data
    // have been rearranged to be sorted from smallest to largest.
    // Note: An OutOfMemoryError can be thrown if there is insufficient
    // memory for an array of n1+n2 ints.
    {
       Interval[ ] temp = new Interval[n1+n2]; // Allocate the temporary array
       int copied  = 0; // Number of elements copied from data to temp
       int copied1 = 0; // Number copied from the first half of data
       int copied2 = 0; // Number copied from the second half of data
       int i;           // Array index to copy from temp back into data

       // Merge elements, copying from two halves of data to the temporary array.
       while ((copied1 < n1) && (copied2 < n2))
       {
          if (data[first + copied1].getUpperEnd() < data[first + n1 + copied2].getLowerEnd())
             temp[copied++] = data[first + (copied1++)];
          else
             temp[copied++] = data[first + n1 + (copied2++)];
       }

       // Copy any remaining entries in the left and right subarrays.
       while (copied1 < n1)
          temp[copied++] = data[first + (copied1++)];
       while (copied2 < n2)
          temp[copied++] = data[first + n1 + (copied2++)];

       // Copy from temp back to the data array.
       for (i = 0; i < n1+n2; i++)
          data[first + i] = temp[i];
    }

    public MapEnumerator getEnumerator() {
        if(enumerator!=null)
            enumerator.reset();
        else
            enumerator = new MapEnumerator(intervals);
        return enumerator;
    }

    public void setEndInterval(int endInterval) {
        this.endInterval = endInterval;
    }

    public void setPcktSize(int pcktSize) {
        this.pcktSize = pcktSize;
    }
    
    /*
    public static int mapOutToLength(int[] mapOut){
    	try{
        	int startPoint = mapOut[0];
        	int length = s
        	for(int )
    	}catch (Exception e){
    		return 0;
    	}
    }*/
    /*
    public static int mapSize(int[] downloadMap) {
        if(downloadMap.length==0) return 0;
        int valuePos;
        int sumInterval=0;
        int[][] mapApos = elaborateMap(downloadMap);
        valuePos = mapApos[0][0];
        downloadMap = mapApos[1];
        for(int i:downloadMap){
			System.err.print(i+" ");
		}
		System.err.println();
            for(int i=valuePos; i<downloadMap.length; i+=2) {
                sumInterval+=(downloadMap[i+1]-downloadMap[i])+1;
            }       
        return sumInterval;
    }
    
    public static int[][] elaborateMap(int[] map) {

        int pointerPos = binarySearch(map, map[0], 1 , map.length);        
        if(pointerPos!=-1) return new int[][]{new int[]{pointerPos}, map};
        int[] newMap = new int[map.length + 2];//new interval to be added
        int newMapPos=0, lower, upper;
        int pointerValue = map[0];
        newMap[newMapPos++] = map[0];

        for(int i=1; i < map.length; i+=2) {
            lower = map[i];
            upper = map[i+1];
            if(lower < pointerValue && upper > pointerValue) {
                newMap[newMapPos++] = lower;
                newMap[newMapPos++] = pointerValue-1;
                newMap[newMapPos++] = pointerValue;
                pointerPos = newMapPos-1;
                newMap[newMapPos++] = upper;
            }else {
                newMap[newMapPos++] = lower;
                newMap[newMapPos++] = upper;
            }
        }
        return new int[][]{new int[]{pointerPos},newMap};
    }*/
    
    public static int mapSize(int[] downloadMap) {
    	return interestingIntervalsSize(interestingIntervals(downloadMap));
    }
    
    public static int interestingIntervalsSize(int[] intervals){
    	int total=0;
    	for(int i=0; i < intervals.length; i+=2) {
    		total += (intervals[i+1] - intervals[i] + 1);
    	}
    	return total;
    }
    
    public static int[] interestingIntervals(int[] map){        
        Vector<Integer> newVector = new Vector<Integer>();        
        int lower, upper;
        int pointerValue = map[0];

        for(int i=1; i < map.length; i+=2) {
            lower = map[i];
            upper = map[i+1];
            if(lower < pointerValue && upper > pointerValue) {
            	newVector.add(pointerValue);
            	newVector.add(upper);
            }
            else if(lower >= pointerValue){
            	newVector.add(lower);
            	newVector.add(upper);
            }
        }
        int[] newMap = new int[newVector.size()];
        for(int i=0; i<newVector.size(); i++){
        	newMap[i] = newVector.get(i);
        }
        return newMap;
    }
    
    public static int[] updateIntervals(int[] intervals, int byteTransferred) {
    	Vector<Integer> newVector = new Vector<Integer>();        
        int lower, upper, intervalSize;
        int remainingData = byteTransferred;

        for(int i=0; i < intervals.length && remainingData>0; i+=2) {
            lower = intervals[i];
            upper = intervals[i+1];
            intervalSize = upper - lower +1;
            if(intervalSize <= remainingData) {
            	newVector.add(lower);
            	newVector.add(upper);            	
            }
            else{
            	newVector.add(lower);
            	newVector.add(lower+remainingData);
            }
            remainingData -= intervalSize;
        }
        int[] newMap = new int[newVector.size()];
        for(int i=0; i<newVector.size(); i++){
        	newMap[i] = newVector.get(i);
        }
        return newMap;
	}
/*
    private static int binarySearch(int[] values, int value, int low, int high) {
        if (high < low)
            return -1;
        int mid = low + ((high - low) / 2) ;
        if (values[mid] > value)
            return binarySearch(values, value, low, mid-1);
        else if (values[mid] < value)
            return binarySearch(values, value, mid+1, high);
        else
            return mid;
   }*/

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

