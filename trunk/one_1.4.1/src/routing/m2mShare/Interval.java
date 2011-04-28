/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routing.m2mShare;


public class Interval {

    private int lowerEnd;
    private int upperEnd;
    private boolean freeSpace;

    public Interval(int lowerEnd, int upperEnd) {
    	this.freeSpace = true;
        this.lowerEnd = lowerEnd;
        this.upperEnd = upperEnd;
    }
    
    public Interval(int lowerEnd, int upperEnd, boolean freeSpace) {
    	this.freeSpace = freeSpace;
        this.lowerEnd = lowerEnd;
        this.upperEnd = upperEnd;
    }

    public int intervalPosition(int pointInterval) {
        if(has(pointInterval))
            return pointInterval;
        if(lowerEnd >= pointInterval)
            return lowerEnd;
        return -1;//interval exeeded!!
    }
    
    public boolean after(Interval i) {
        if(intervalPosition(i.getUpperEnd()) == lowerEnd)
            return true;
        return false;
    }

    public Interval[] split(int splitPoint) {
        if(!has(splitPoint)) return null;
        if(lowerEnd == splitPoint)
            return new Interval[]{null,this};
        if(upperEnd == splitPoint)
            return new Interval[]{this,null};
        Interval[] toReturn =
                new Interval[]{this,new
                Interval(splitPoint, upperEnd)};
        this.upperEnd = splitPoint;
        return toReturn;
    }

    public boolean equals(Object obj) {
        if(obj!=null && obj instanceof Interval) {
            Interval toMatch = (Interval)obj;
            return (toMatch.getLowerEnd() == lowerEnd) &&
                   (toMatch.getUpperEnd() == upperEnd);
        }
        return false;
    }

    public int getLowerEnd() {
        return lowerEnd;
    }

    public void setLowerEnd(int lowerEnd) {
        this.lowerEnd = lowerEnd;
    }

    public int getUpperEnd() {
        return upperEnd;
    }

    public void setUpperEnd(int upperEnd) {
        this.upperEnd = upperEnd;
    }

    public int size() {
        return upperEnd - lowerEnd + 1;
    }    

    public boolean has(int value) {
        return (value >= lowerEnd &&
                value <= upperEnd);
    }

	public boolean isFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(boolean freeSpace) {
		this.freeSpace = freeSpace;
	}

	@Override
	public String toString() {
		if(freeSpace){
			return "*" + lowerEnd + "," + upperEnd +"*";
		}
		return "[" + lowerEnd + "," + upperEnd +"]";
	}    
	
	
    
}
