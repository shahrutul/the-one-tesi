package routing.m2mShare;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import core.Connection;
import core.DTNHost;
import core.SimClock;
import routing.M2MShareRouter;
import routing.m2mShare.QueuingCentral.NoActivityInQueueException;

public class DTNScheduler {
	
	private static final int MAX_COMMUNICATORS = 10;

	private QueuingCentral queuingCentral;
	private DTNPresenceCollector presenceCollector;
	private boolean virtualFileTurn;
	private M2MShareRouter myRouter;
	private int taskTurn;
	
	private Executor[] executors;
	private Vector<Communicator> activeCommunicators;
	
	
	
	public DTNScheduler(DTNPresenceCollector presenceCollector, QueuingCentral queuingCentral, M2MShareRouter myRouter) {
		this.presenceCollector = presenceCollector;
		this.queuingCentral = queuingCentral;
		this.myRouter = myRouter;
		this.executors = new Executor[4];
		for(int i=0; i<executors.length; i++){
			this.executors[i] = new Executor(this, i);
		}
		activeCommunicators = new Vector<Communicator>();
		virtualFileTurn = true;
		taskTurn = 0;
	}

	public void runUpdate(){
		/*
		presenceCollector.update();
		
		updateLocalActivity();
		
		updatePendingActivity();
		
		updateUploadActivity();*/
		
		/*int nrOfActivities = queuingCentral.size();
		for(int i=0; i< nrOfActivities; i++){*/
		
		switch (taskTurn) {
		case 0:
			//the Presence Collector will run the Triggered Activity Flow 
			//when it updates hosts presence informations
			presenceCollector.update();
			break;
			
		case 1:
			//LOCAL_ACTIVITY_FLOW
			updateLocalActivity();
			break;
			
		case 2:
			//PENDING_ACTIVITY_FLOW
			updatePendingActivity();
			break;
			
		case 3:
			//PENDING_UPLOAD_FLOW
			updateUploadActivity();
			break;

		}
		
		taskTurn++;
		if(taskTurn > 3){
			taskTurn = 0;
		}	
		
		checkCommunicators();
		/*
		}*/
		
	}

	//TRIGGERED_ACTIVITY_FLOW
	void runTriggeredActivityFlow(){
		try {
			DTNActivity activity = queuingCentral.pop(QueuingCentral.DTN_PENDING_UPLOAD_ID);
			//execute(activity, QueuingCentral.DTN_PENDING_UPLOAD_ID);
			if(executors[0].isReady()){			
				executors[0].runActivity(activity, QueuingCentral.DTN_PENDING_UPLOAD_ID);
			}
		} catch (NoActivityInQueueException e) {
			return;
		}
	}
	
	private void updateUploadActivity() {
		try {
			DTNActivity activity = queuingCentral.pop(QueuingCentral.UPLOAD_QUEUE_ID);
			//execute(activity, QueuingCentral.UPLOAD_QUEUE_ID);
			if(executors[3].isReady()){			
				executors[3].runActivity(activity, QueuingCentral.UPLOAD_QUEUE_ID);
			}
		} catch (NoActivityInQueueException e) {
			return;
		}
	}

	private void updatePendingActivity() {
		try {
			DTNActivity activity = queuingCentral.pop(QueuingCentral.DTN_PENDING_ID);
			//execute(activity, QueuingCentral.DTN_PENDING_ID);
			if(executors[2].isReady()){			
				executors[2].runActivity(activity, QueuingCentral.DTN_PENDING_ID);
			}
		} catch (NoActivityInQueueException e) {
			return;
		}
	}

	private void updateLocalActivity() {
		int queueToPop = -1;
		DTNActivity activityToExecute = null;
		if(queuingCentral.getQueueSize(QueuingCentral.DTN_DOWNLOAD_QUEUE_ID) != 0){
			queueToPop = QueuingCentral.DTN_DOWNLOAD_QUEUE_ID;
		}
		else{
			if(queuingCentral.getQueueSize(QueuingCentral.VIRTUAL_FILE_QUEUE_ID) == 0 &&
					queuingCentral.getQueueSize(QueuingCentral.QUERY_QUEUE_ID) == 0){				
				return;
			}
			
			
			if(virtualFileTurn){
				if(queuingCentral.getQueueSize(QueuingCentral.VIRTUAL_FILE_QUEUE_ID) != 0){
					queueToPop = QueuingCentral.VIRTUAL_FILE_QUEUE_ID;
				}
				else{
					queueToPop = QueuingCentral.QUERY_QUEUE_ID;
				}
			}
			else{
				if(queuingCentral.getQueueSize(QueuingCentral.QUERY_QUEUE_ID) != 0){
					queueToPop = QueuingCentral.QUERY_QUEUE_ID;
				}
				else{
					queueToPop = QueuingCentral.VIRTUAL_FILE_QUEUE_ID;
				}				
			}
			virtualFileTurn = !virtualFileTurn;
		}		
		
		try{
			activityToExecute = queuingCentral.pop(queueToPop);
		}
		catch(NoActivityInQueueException ex){
			return;
		}
		
		//executeCommunicator(activityToExecute, queueToPop);
		if(executors[1].isReady()){			
			executors[1].runActivity(activityToExecute, queueToPop);
		}
	}

	/*
	private void executeCommunicator(DTNActivity activityToExecute,
			int queueIndex) {
		if(activityToExecute == null){
			return;
		}
		if(communicators.size() >= MAX_COMMUNICATORS){
			return;
		}
		Executor comm = new Executor(SimClock.getTime(), activityToExecute, queueIndex, this);
		communicators.add(comm);
		comm.runActivity();
		
	}

	private void execute(DTNActivity activityToExecute, int queueIndex) {
		if(activityToExecute == null){
			return;
		}
		activityToExecute.execute();
		
		if(activityToExecute.getState() == DTNActivity.STATE_INCOMPLETE){
			queuingCentral.push(activityToExecute, queueIndex);
		}
		
	}*/

	public void delegate(DTNHost otherHost) {
		M2MShareRouter otherRouter = (M2MShareRouter) otherHost.getRouter();
		for(int i=0; i < queuingCentral.getQueueSize(QueuingCentral.VIRTUAL_FILE_QUEUE_ID); i++){
			try{
				VirtualFile virtualFile = (VirtualFile) queuingCentral.pop(QueuingCentral.VIRTUAL_FILE_QUEUE_ID);
				//System.err.println(SimClock.getTime()+ " - "+ myRouter.getHost()+" delega virtuaFile a "+otherHost);
				DTNActivity newActivity = new DTNPendingDownload(
						myRouter.getHost(), 
						virtualFile.getFileHash(),
						new IntervalMap(virtualFile.getRestOfMap()),
						10000,
						otherRouter
				);

				otherRouter.addPendingDownload(newActivity);
				queuingCentral.push(virtualFile, QueuingCentral.VIRTUAL_FILE_QUEUE_ID);

			}
			catch(NoActivityInQueueException e){}
		}
	}


	/**
	 * push in the queue specified the Activity
	 * @param activity 
	 * @param activityQueue
	 */
	public void reQueue(DTNActivity activity, int activityQueue) {
		queuingCentral.push(activity, activityQueue);
	}
	
	public boolean moreCommunicatorsAvailable() {
		return activeCommunicators.size() < MAX_COMMUNICATORS;
	}

	/**
	 * Remove the selected communicator
	 * @param connection used by the communicator to be removed
	 */
	public void removeCommunicator(Connection con) {
		for(int i=activeCommunicators.size()-1; i>=0; i--){
			Communicator comm = activeCommunicators.get(i);
			if(comm.getConnection().equals(con)){
				comm.stop();
				comm.getExecutor().communicatorRemoved();
				activeCommunicators.remove(i);
			}
		}
	}

	public void communicatorRemoved(Communicator comm) {
		activeCommunicators.remove(comm);
	}

	public int getAvailableCommunicators() {
		return MAX_COMMUNICATORS - activeCommunicators.size();
	}

	public void addCommunicator(Communicator comm) {
		int i=0;
		while(i<activeCommunicators.size() && 
				activeCommunicators.get(i).getEndTime() < comm.getEndTime()){
			i++;
		}
		activeCommunicators.insertElementAt(comm, i);
	}
	

	private void checkCommunicators() {		
		double simTime = SimClock.getTime();
		try{
			while(activeCommunicators.firstElement().getEndTime() <= simTime){
				stopCommunicator(activeCommunicators.remove(0));
			}
		}catch(Exception e){}		
	}

	private void stopCommunicator(Communicator removed) {
		if(removed.finish()){
			Executor ex = removed.getExecutor();
			for(int i=activeCommunicators.size()-1; i>=0; i--){
				Communicator comm = activeCommunicators.get(i);
				if(comm.getExecutor().equals(ex)){
					activeCommunicators.remove(i);
				}
			}
			ex.communicatorRemoved();
		}
		else{
			addCommunicator(removed);
		}		
	}

	
}
