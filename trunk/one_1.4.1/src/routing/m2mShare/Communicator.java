package routing.m2mShare;

import core.Connection;
import core.SimClock;

public class Communicator {

	/**
	 * when the communicator is created and started
	 */
	private double startTime;
	/**
	 * The activity to execute
	 */
	private DTNActivity activityToExecute;
	/**
	 * The connection used by this Communicator
	 */
	private Connection connection;
	
	public Communicator(double startTime, DTNActivity activityToExecute, Connection conn) {
		super();
		this.startTime = startTime;
		this.activityToExecute = activityToExecute;
		this.connection = conn;
	}

	/**
	 * Stop this Communicator because the connection is down
	 * @return true if the Activity associated is complete, false otherwise
	 */
	public boolean stop(){
		//avvisa l'activity che hai finito di scaricare
		System.err.println(SimClock.getTime() +"-"+startTime+ " - Communicator stopped, "+ (SimClock.getTime() - startTime)+" sec");
		int byteTransferred = (int) ((SimClock.getTime() - startTime) * connection.getSpeed());
		if(activityToExecute.isComplete(byteTransferred)){
			activityToExecute.setCompleted();
			return true;
		}
		return false;
	}


	public void start() {
		System.err.println("comincio a scaricare..");
	}
	
}
