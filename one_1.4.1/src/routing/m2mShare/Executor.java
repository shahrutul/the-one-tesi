package routing.m2mShare;

import java.util.HashMap;
import java.util.Map;

import core.Connection;
import core.DTNHost;
import core.SimClock;

public class Executor {
	
	
	/**
	 * the scheduler activating this Communicator
	 */
	private DTNScheduler scheduler;
	
	/**
	 * The activity to execute
	 */
	private DTNActivity currentActivity;
	/**
	 * where to requeue the activity if not ended when stop
	 */
	private int currentActivityQueue;

	private boolean ready;
	
	private int id;
	
	private int activeCommunicators;
	
	public Executor(DTNScheduler dtnScheduler, int id) {
		super();
		this.id = id;
		this.scheduler = dtnScheduler;
		this.ready = true;
		activeCommunicators = 0;
	}

	public void runActivity(DTNActivity activityToExecute, int activityQueue){
		this.ready = false;
		this.currentActivity = activityToExecute;
		this.currentActivityQueue = activityQueue;
		currentActivity.execute(this);
		switch (currentActivity.getState()) {
		
		case DTNActivity.STATE_ACTIVE:
			//System.err.println(SimClock.getTime()+" attiva");
			return;
			
		case DTNActivity.STATE_COMPLETED:
			//System.err.println(SimClock.getTime()+" completa");
			this.ready = true;
			return;

		case DTNActivity.STATE_INCOMPLETE:
			//System.err.println(SimClock.getTime()+" incompleta");
			this.ready = true;
			scheduler.reQueue(currentActivity, activityQueue);
			return;
		}
	}
	

	/**
	 * Add a new Communicator for the running Activity
	 * @param connection the connection associated at the transfer
	 * @param mapOut
	 */
	public void addCommunicator(Connection connection, int[] mapOut) {
		//System.err.println(SimClock.getTime()+" aggiunto communicator");
		Communicator newComm = new Communicator(currentActivity, connection, mapOut, this);		
		scheduler.addCommunicator(newComm);
		activeCommunicators++;
		newComm.start();		
	}
	

	public void communicatorRemoved() {
		if(currentActivity.getState() == DTNActivity.STATE_COMPLETED){
			activeCommunicators = 0;
			ready = true;
			//System.err.println("Activity completed in exec");
			return;
		}
		activeCommunicators--;
		
		if(activeCommunicators == 0){
			this.ready = true;
			//no more downloads: set the activity as complete or incomplete
			switch (currentActivity.getState()) {
			
			case DTNActivity.STATE_ACTIVE:
				//System.err.println("Activity ancora attiva ma senza communicators in exec");
				currentActivity.setIncomplete();
				scheduler.reQueue(currentActivity, currentActivityQueue);
				//System.err.println("Activity incompleta -> riaccodata in exec");
				return;
				
			case DTNActivity.STATE_COMPLETED:
				//System.err.println("Activity finita! in exec");
				return;

			case DTNActivity.STATE_INCOMPLETE:
				scheduler.reQueue(currentActivity, currentActivityQueue);
				//System.err.println("Activity incompleta -> riaccodata in exec");
				return;
			}
		}
	}
	
	
	public boolean isReady(){
		return ready;
	}

	public int getAvailableCommunicators() {
		return scheduler.getAvailableCommunicators();
	}

	public boolean moreCommunicatorsAvailable() {		
		return scheduler.moreCommunicatorsAvailable();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Executor) && ((Executor)obj).id == this.id;
	}
	
	
	
}
