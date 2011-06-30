package routing.m2mShare;

import core.DTNHost;

public abstract class DTNActivity {
	
	public static final int STATE_QUEUED = 0;
	public static final int STATE_ACTIVE = 1;
	public static final int STATE_COMPLETED = 2;
	public static final int STATE_INCOMPLETE = 3;
	
	private int state = 0;
	private String id;

	/**
	 * The Activity execution function
	 * @param executor 
	 * @return true if the activity has ended, false otherwise
	 */
	public abstract void execute(Executor executor);
	
	public int getState(){
		return state;
	}
	
	public void setActive(){
		state = STATE_ACTIVE;
	}
	
	public void setCompleted(){
		state = STATE_COMPLETED;
	}
	
	public void setIncomplete(){
		state = STATE_INCOMPLETE;
	}

	public abstract void addTransferredData(int[] intervals, DTNHost from);

	public abstract int[] getRestOfMap();

	public void setID(String id){
		this.id = id;
	}
	
	public String getID() {		
		return id;
	}


	
}
