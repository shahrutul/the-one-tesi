package routing.m2mShare;

import java.util.Vector;

import routing.M2MShareRouter;

import core.DTNHost;

public class DTNDownloadFwd extends DTNActivity {
	
	private DTNHost requestor;
	private IntervalMap map;
	private String filehash;
	private M2MShareRouter myRouter;
	private double maxEndTime;
	private Vector<DTNHost> delegationChain;
	
	private DTNHost currentRequestor;

	public DTNDownloadFwd(DTNHost requestor, IntervalMap map, String filehash, double maxEndTime, M2MShareRouter myRouter, String taskID, Vector<DTNHost> delegationChain) {
		this.requestor = requestor;
		this.map = map;
		this.filehash = filehash;
		this.myRouter = myRouter;
		this.maxEndTime = maxEndTime;
		setID(taskID);
		this.delegationChain = delegationChain;
	}

	@Override
	public void execute(Executor executor) {
		setActive();
		currentRequestor = null;
		/* Check if there is a valid host to which return the task output */
		for(int i=0; i<delegationChain.size() && currentRequestor == null; i++){
			if(myRouter.getPresenceCollector().isHostInRange(delegationChain.get(i))){
				currentRequestor = delegationChain.get(i);
			}
		}
		
		if(currentRequestor != null && executor.moreCommunicatorsAvailable()){
			int[] missingRestOfMap = ((M2MShareRouter)currentRequestor.getRouter()).getIntervalsForDownloadFwd(getID());
			if(missingRestOfMap == null){
				setCompleted();
				return;
			}
			System.err.println(myRouter.getHost() + " - in DTNDownloadFwd.execute comincio a trasferire a "+currentRequestor);
			executor.addCommunicator(myRouter.getPresenceCollector().getConnectionFor(currentRequestor), myRouter.getHost(), filehash, missingRestOfMap);
			/* keep state = ACTIVE */
			return;
			
		}		
		//no communicator started
		setIncomplete();		
	}

	public DTNHost getRequestor() {
		return requestor;
	}

	public IntervalMap getMap() {
		return map;
	}
	
	public double getMaxEndTime(){
		return maxEndTime;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DTNDownloadFwd)){
			return false;
		}
		return ((DTNDownloadFwd)obj).filehash.equals(this.filehash) &&
			//((DTNDownloadFwd)obj).map.equals(this.map) &&
			((DTNDownloadFwd)obj).requestor.equals(this.requestor);
	}
	
	@Override
	public void addTransferredData(int[] intervals, DTNHost from) {
		((M2MShareRouter)currentRequestor.getRouter()).dataFromDownloadFwd(getID(), intervals, myRouter.getHost());		
		if(((M2MShareRouter)currentRequestor.getRouter()).getIntervalsForDownloadFwd(getID()) == null){
			setCompleted();
		}
	}

	@Override
	public int[] getRestOfMap() {
		return ((M2MShareRouter)currentRequestor.getRouter()).getIntervalsForDownloadFwd(getID());
	}

	public String getFileHash() {
		return filehash;
	}

	@Override
	public void setCompleted() {		
		super.setCompleted();
		myRouter.notifyDownloadFWDReturned(myRouter.getHost(), currentRequestor, filehash);
		myRouter.notifyDataRedundancyUpdated(myRouter.getHost(), map.mapBytesSize() * -1);
		//((M2MShareRouter)requestor.getRouter()).notifyDownloadFwdCompleted(myRouter.getHost());
	}
	
	
	
}
