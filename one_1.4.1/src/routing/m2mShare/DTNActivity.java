package routing.m2mShare;

public abstract class DTNActivity {
	
	public static final int STATE_QUEUED = 0;
	public static final int STATE_ACTIVE = 1;
	public static final int STATE_COMPLETED = 2;
	public static final int STATE_INCOMPLETE = 3;
	
	public int state = 0;

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

	public abstract void addTransferredData(int[] intervals);

	public abstract int[] getRestOfMap();

	
}
