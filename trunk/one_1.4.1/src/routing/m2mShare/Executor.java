package routing.m2mShare;

import java.util.HashMap;
import java.util.Map;

import core.Connection;
import core.SimClock;

public class Executor {
	
	
	/**
	 * the scheduler activating this Communicator
	 */
	private DTNScheduler scheduler;
	
	private Map<Connection, Communicator> communicators;
	/**
	 * The activity to execute
	 */
	private DTNActivity currentActivity;
	/**
	 * where to requeue the activity if not ended when stop
	 */
	private int currentActivityQueue;

	private boolean ready;
	
	
	public Executor(DTNScheduler dtnScheduler) {
		super();
		this.scheduler = dtnScheduler;
		this.communicators = new HashMap<Connection, Communicator>();
		this.ready = true;
	}

	public void runActivity(DTNActivity activityToExecute, int activityQueue){
		System.err.println(SimClock.getTime()+" c'è qualcosa da fare");
		this.ready = false;
		this.currentActivity = activityToExecute;
		this.currentActivityQueue = activityQueue;
		currentActivity.execute(this);
		switch (currentActivity.getState()) {
		
		case DTNActivity.STATE_ACTIVE:
			System.err.println(SimClock.getTime()+" attiva");
			return;
			
		case DTNActivity.STATE_COMPLETED:
			System.err.println(SimClock.getTime()+" completa");
			this.ready = true;
			return;

		case DTNActivity.STATE_INCOMPLETE:
			System.err.println(SimClock.getTime()+" incompleta");
			this.ready = true;
			scheduler.reQueue(currentActivity, activityQueue);
			return;
		}
	}
	
	/**
	 * Add a new Communicator for the running Activity
	 * @param connection the connection associated at the transfer
	 * @param totData 
	 * @param minData 
	 * @param startingPoint 
	 * @return true if a new Communicator has been added, false otherwise
	 */
	public boolean addCommunicator(Connection connection, int startingPoint, int minData, int totData) {
		System.err.println(SimClock.getTime()+" aggiunto communicator");
		Communicator newComm = new Communicator(currentActivity, connection, minData, totData);
		communicators.put(connection, newComm);		
		newComm.start();
		scheduler.communicatorAdded();
		return true;
	}

	/**
	 * Remove the selected communicator (if associated with this Executor)
	 * @param con the Connection now closed
	 */
	public void removeCommunicator(Connection con) {
		Communicator comm = communicators.get(con);
		if(comm == null){
			return;
		}
		// Stop the Communicator and checks if the Activity is completed
		if(comm.stop()){
			System.err.println("Activity completata");
			scheduler.communicatorRemoved(communicators.size());
			communicators.clear();
			ready = true;
			return;
		}
		communicators.remove(con);
		scheduler.communicatorRemoved(1);
		if(communicators.size() == 0){
			this.ready = true;
			//no more downloads: set the activity as complete or incomplete
			switch (currentActivity.getState()) {
			
			case DTNActivity.STATE_ACTIVE:
				return;
				
			case DTNActivity.STATE_COMPLETED:
				this.ready = true;
				return;

			case DTNActivity.STATE_INCOMPLETE:
				this.ready = true;
				scheduler.reQueue(currentActivity, currentActivityQueue);
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
	
}
