package routing.m2mShare;

import core.Connection;
import core.SimClock;

public class Communicator {

	/**
	 * when the communicator is created and started
	 */
	private double startTime;
	/**
	 * the minimum asked connection time
	 */
	private double minEndTime;
	/**
	 * the time at when the communicator will have downloaded all requested data
	 */
	private double maxEndTime;
	/**
	 * The activity to execute
	 */
	private DTNActivity activityToExecute;
	/**
	 * The connection used by this Communicator
	 */
	private Connection connection;
	
	public Communicator(DTNActivity activityToExecute, Connection conn, int minData, int totData) {
		this.startTime = SimClock.getTime();
		this.connection = conn;
		this.minEndTime = SimClock.getTime() + (minData / connection.getSpeed());
		this.maxEndTime = SimClock.getTime() + (totData / connection.getSpeed());
		this.activityToExecute = activityToExecute;		
		System.err.println("communicator: "+ conn + " - "+ minData+"(" +minEndTime+ ") - "+ totData+"("+maxEndTime+")");
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
