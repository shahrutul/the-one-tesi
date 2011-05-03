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
	private double endTime;
	/**
	 * The activity to execute
	 */
	private DTNActivity activityToExecute;
	/**
	 * The connection used by this Communicator
	 */
	private Connection connection;
	private int[] mapOut;
	private Executor executor;
	
	
	public Communicator(DTNActivity currentActivity, Connection conn, int[] mapOut, Executor executor) {
		this.startTime = SimClock.getTime();
		this.connection = conn;
		this.activityToExecute = currentActivity;
		this.mapOut = mapOut;
		int dataToTransfer = IntervalMap.mapSize(mapOut);
		this.endTime = startTime + (dataToTransfer / conn.getSpeed());
		this.executor = executor;
		
		System.err.print("communicator: "+ conn + "(");
		for(int i:mapOut){
			System.err.print(i+" ");
		}
		System.err.println(") - "+ dataToTransfer+"(end: "+endTime+")");
	}

	/**
	 * Stop this Communicator because the connection is down
	 * @return true if the Activity associated is complete, false otherwise
	 */
	public void stop(){
		//avvisa l'activity che hai finito di scaricare
		int byteTransferred = (int) ((SimClock.getTime() - startTime) * connection.getSpeed());
		System.err.println(SimClock.getTime() +"-"+startTime+ " - Communicator stopped, "+ (SimClock.getTime() - startTime)+" sec - bytes: "+byteTransferred);
		activityToExecute.addTransferredData(byteTransferred, mapOut[0]);		
	}	
	
	/**
	 * This communicator has finished his current Task.
	 * It ask for the new downloadMap
	 * @return true if the activity is complete, false otherwise
	 */
	public boolean finish() {
		int byteTransferred = (int) ((SimClock.getTime() - startTime) * connection.getSpeed());
		System.err.println(SimClock.getTime() +"-"+startTime+ " - Communicator finished, "+ (SimClock.getTime() - startTime)+" sec - bytes: "+byteTransferred);
		activityToExecute.addTransferredData(byteTransferred, mapOut[0]);
		if(activityToExecute.getState() == DTNActivity.STATE_COMPLETED){
			return true;
		}
		else{
			try{
				int[] map = activityToExecute.getRestOfMap();
				int dataToTransfer = map[2]-map[1];
				startTime = SimClock.getTime();
				endTime = startTime + (dataToTransfer / connection.getSpeed());
			}catch(Exception e){}			
			return false;
		}
	}

	public double getEndTime() {
		return endTime;
	}

	public void start() {
		System.err.println("comincio a scaricare..");
	}

	public Executor getExecutor() {
		return executor;
	}

	public Connection getConnection() {
		return connection;
	}

	
	

}
