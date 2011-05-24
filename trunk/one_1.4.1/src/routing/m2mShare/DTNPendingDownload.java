package routing.m2mShare;

import java.util.HashMap;
import java.util.Vector;

import routing.M2MShareRouter;
import routing.m2mShare.BroadcastModule.Pair;

import core.Connection;
import core.DTNHost;
import core.SimClock;

public class DTNPendingDownload extends DTNActivity {

	private DTNHost requestor;
	private String filehash;
	private IntervalMap map;
	private double maxEndTime;
	private M2MShareRouter myRouter;
	//private int hop;
	private Vector<DTNHost> delegationChain;
	private int originalActivityId;
	
	public DTNPendingDownload(DTNHost requestor, String filehash,
			IntervalMap map, long ttl, Vector<DTNHost> delegationChain, int originalActivityID, M2MShareRouter m2mShareRouter) {
		this.requestor = requestor;
		this.filehash = filehash;
		this.map = map;
		this.maxEndTime = SimClock.getTime()+ttl;
		this.myRouter = m2mShareRouter;
		setID(myRouter.getNextId());
		//this.hop = hop;
		this.delegationChain = delegationChain;
		this.originalActivityId = originalActivityID;
	}


	@Override
	public void execute(Executor executor) {
		setActive();
		int communicatorActivated = 0;
		
		if(myRouter.useBroadcastModule()){			
			Vector<Pair<DTNHost, Connection>> servers = myRouter.broadcastQuery(filehash);
			if(servers != null && servers.size()!=0){
				System.err.println(myRouter.getHost() + " - in pendingDownload.Execute trovato file "+servers.size()+" volte");
				for(Pair<DTNHost, Connection> server:servers){
					try {
						executor.addCommunicator(server.getSecond(), server.getFirst(), filehash, map.cut(myRouter.getFileDivisionStrategyType()));
						communicatorActivated++;
					} catch (Exception e) {
						//nothing to download
					}
				}
			}
		}
		else{
			HashMap<DTNHost, Connection> neighbours = (HashMap<DTNHost, Connection>) myRouter.getPresenceCollector().getHostsInRange();		
			for(DTNHost host: neighbours.keySet()){
				
				boolean hasFile = host.getFileSystem().hasFile(filehash);
				if(hasFile && executor.moreCommunicatorsAvailable()){
					//DTNFile file = host.getFileSystem().getFile(filehash);
					//myRouter.getHost().getFileSystem().addToFiles(file);
					System.err.println(myRouter.getHost() + " - in pendingDownload.Execute trovato file");

					try {
						executor.addCommunicator(neighbours.get(host), host, filehash, map.cut(myRouter.getFileDivisionStrategyType()));
						communicatorActivated++;
					} catch (Exception e) {
						//nothing to download
					}

				}
			}
			
			
		}
		if(communicatorActivated == 0){
			setIncomplete();
		}
		// else still running for transfer
		// status == Active
	}


	public DTNHost getRequestor() {
		return requestor;
	}


	public String getFilehash() {
		return filehash;
	}


	public IntervalMap getMap() {
		return map;
	}


	public double getMaxEndTime() {
		return maxEndTime;
	}
	
	
	public int getHop(){
		//return hop;
		return delegationChain.size();
	}
	
	public Vector<DTNHost> getDelegationChain(){
		return delegationChain;
	}
	
	
	@Override
	public void setCompleted() {
		super.setCompleted();
		myRouter.notifyPendingDownloadCompleted(myRouter.getHost(), requestor, filehash);
		DTNDownloadFwd newActivity = new DTNDownloadFwd(requestor, map, filehash, maxEndTime, originalActivityId, myRouter);
		myRouter.addDownloadFwd(newActivity);		
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DTNPendingDownload)){
			return false;
		}
		return ((DTNPendingDownload)obj).filehash.equals(this.filehash) &&
			((DTNPendingDownload)obj).requestor.equals(this.requestor);
			//((DTNPendingDownload)obj).map.equals(this.map) &&;
	}

	@Override
	public void addTransferredData(int[] intervals, DTNHost from) {
		int bytesTrasferred = 0;
		try {
			for(int i=0; i<intervals.length-1; i+=2){
				map.update(intervals[i], intervals[i+1]);
				bytesTrasferred += intervals[i+1]-intervals[i]+1;
			}			
			System.err.println(SimClock.getTime() + " - "+ myRouter.getHost() + "Mappa aggiornata PENDING: "+map);
						
		} catch (Exception e) {
		}
		myRouter.notifyDataTransferred(from, myRouter.getHost(), bytesTrasferred);
		if(map.mapSize() == 0){
			setCompleted();
			System.err.println(SimClock.getTime() + " - "+ myRouter.getHost() + "PENDING completa");
		}
	}


	@Override
	public int[] getRestOfMap() {
		return map.assignRestofMap();
	}


	@Override
	public String toString() {
		return requestor +"->"+ myRouter.getHost()+" map:"+map;
	}


	public int[] getMapForDelegation(int fileDivisionStrategyType) {
		try {
			return map.cut(fileDivisionStrategyType);
		} catch (Exception e) {
			return null;
		}
	}
	
	

}
