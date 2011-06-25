package report;

import core.DTNHost;
import core.FileEventListener;

public class DataRedundancyLog extends Report implements FileEventListener {
	
	
	
	/**
	 * Constructor.
	 */
	public DataRedundancyLog() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void done() {
		write("");		
		super.done();
	}	


	@Override
	public void dataRedundancyUpdated(DTNHost where, int bytes) {
		write(format(getSimTime()) + "\t" + where + "\t" + bytes);
	}

	@Override
	public void dataTransferred(DTNHost from, DTNHost to, int bytes) {}

	@Override
	public void newVirtualFile(DTNHost where, String filehash) {}

	@Override
	public void fileRequestDelegated(DTNHost from, DTNHost to, String filehash) {}

	@Override
	public void pendingDownloadExpired(DTNHost where, DTNHost requestor,
			String filehash) {}

	@Override
	public void downloadFWDExpired(DTNHost where, DTNHost requestor,
			String filehash) {}

	@Override
	public void fileRequestSatisfied(DTNHost where, String filehash,
			boolean selfSatisfied) {}

	@Override
	public void downloadFWDReturned(DTNHost from, DTNHost requestor,
			String filehash) {}

	@Override
	public void pendingDownloadCompleted(DTNHost where, DTNHost requestor,
			String filehash) {}

}
