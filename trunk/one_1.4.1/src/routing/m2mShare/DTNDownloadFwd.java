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
		if(myRouter.getPresenceCollector().isHostInRange(requestor) && executor.moreCommunicatorsAvailable()){
			int[] missingRestOfMap = ((M2MShareRouter)requestor.getRouter()).getIntervalsForDownloadFwd(getID());
			if(missingRestOfMap == null){
				setCompleted();
				return;
			}
			System.err.println(myRouter.getHost() + " - in DTNDownloadFwd.execute comincio a trasferire a "+requestor);
			executor.addCommunicator(myRouter.getPresenceCollector().getConnectionFor(requestor), myRouter.getHost(), filehash, missingRestOfMap);
			
			/*
			manca da creare il file x trasferirlo
			DTNFile file = myRouter.getHost().getFileSystem().getFile(filehash);
			requestor.getFileSystem().addToFiles(file);			
			((M2MShareRouter)requestor.getRouter()).notifyQuerySatisfied(filehash, false);
			setCompleted();
			*/
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
		((M2MShareRouter)requestor.getRouter()).dataFromDownloadFwd(getID(), intervals, myRouter.getHost());		
		if(((M2MShareRouter)requestor.getRouter()).getIntervalsForDownloadFwd(getID()) == null){
			setCompleted();
		}
	}

	@Override
	public int[] getRestOfMap() {
		return ((M2MShareRouter)requestor.getRouter()).getIntervalsForDownloadFwd(getID());
	}

	public String getFileHash() {
		return filehash;
	}

	@Override
	public void setCompleted() {		
		super.setCompleted();
		myRouter.notifyDownloadFWDReturned(myRouter.getHost(), requestor, filehash);
		myRouter.notifyDataRedundancyUpdated(myRouter.getHost(), map.mapBytesSize() * -1);
		((M2MShareRouter)requestor.getRouter()).notifyDownloadFwdCompleted(myRouter.getHost());
	}
	
	
	
}