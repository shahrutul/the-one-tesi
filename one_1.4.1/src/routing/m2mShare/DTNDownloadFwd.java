package routing.m2mShare;


import routing.M2MShareRouter;

import core.DTNFile;
import core.DTNHost;

public class DTNDownloadFwd extends DTNActivity {
	
	private DTNHost requestor;
	private IntervalMap map;
	private String filehash;
	private M2MShareRouter myRouter;

	public DTNDownloadFwd(DTNHost requestor, IntervalMap map, String filehash, M2MShareRouter m2mShareRouter) {
		super();
		this.requestor = requestor;
		this.map = map;
		this.filehash = filehash;
		this.myRouter = m2mShareRouter;
	}

	@Override
	public void execute(Executor executor) {
		setActive();
		if(myRouter.getPresenceCollector().isHostInRange(requestor)){
			manca da creare il file x trasferirlo
			DTNFile file = myRouter.getHost().getFileSystem().getFile(filehash);
			requestor.getFileSystem().addToFiles(file);
			System.err.println(myRouter.getHost() + " - in DTNDownloadFwd.execute ritornato file a "+requestor);
			((M2MShareRouter)requestor.getRouter()).notifyQuerySatisfied(filehash, false);
			setCompleted();
		}		
		setIncomplete();		
	}

	public DTNHost getRequestor() {
		return requestor;
	}

	public IntervalMap getMap() {
		return map;
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
	public void addTransferredData(int[] intervals) {
		System.err.println("downloadfwd unComplete");
	}

	@Override
	public int[] getRestOfMap() {
		return null;
	}
	
	

}
