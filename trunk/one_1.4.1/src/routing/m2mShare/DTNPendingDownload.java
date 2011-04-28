package routing.m2mShare;

import java.util.HashMap;

import routing.M2MShareRouter;

import core.Connection;
import core.DTNFile;
import core.DTNHost;

public class DTNPendingDownload extends DTNActivity {

	private DTNHost requestor;
	private String filehash;
	private IntervalMap map;
	private long ttl;
	private M2MShareRouter myRouter;
	
	public DTNPendingDownload(DTNHost requestor, String filehash,
			IntervalMap map, long ttl, M2MShareRouter m2mShareRouter) {
		super();
		this.requestor = requestor;
		this.filehash = filehash;
		this.map = map;
		this.ttl = ttl;
		this.myRouter = m2mShareRouter;
	}


	@Override
	public void execute(Executor executor) {
		setActive();
		HashMap<DTNHost, Connection> neighbours = (HashMap<DTNHost, Connection>) myRouter.getPresenceCollector().getHostsInRange();		
		if(neighbours.size() == 0){
			setIncomplete();
		}
		
		for(DTNHost host: neighbours.keySet()){
			if(!myRouter.getPresenceCollector().isHostInRange(host)){
				continue;
			}
			boolean hasFile = host.getFileSystem().hasFile(filehash);
			if(hasFile){
				DTNFile file = host.getFileSystem().getFile(filehash);
				myRouter.getHost().getFileSystem().addToFiles(file);
				System.err.println(myRouter.getHost() + " - in pendingDownload.Execute trovato file");
				
				DTNDownloadFwd newActivity = new DTNDownloadFwd(requestor, map, filehash, myRouter);
				myRouter.addDownloadFwd(newActivity);
				
				setCompleted();
			}
		}
		setIncomplete();
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


	public long getTtl() {
		return ttl;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DTNPendingDownload)){
			return false;
		}
		return ((DTNPendingDownload)obj).filehash.equals(this.filehash) &&
			((DTNPendingDownload)obj).requestor.equals(this.requestor) &&
			//((DTNPendingDownload)obj).map.equals(this.map) &&
			((DTNPendingDownload)obj).ttl == this.ttl;
	}


	@Override
	public boolean isComplete(int byteTransferred) {
		System.err.println("DTNpendingDownload isComplete");
		return false;
	}

}
