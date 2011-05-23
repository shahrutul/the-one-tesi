package report;

import core.DTNHost;
import core.FileEventListener;
import core.SimScenario;

public class FileGatheringLog extends Report implements FileEventListener{

	
	/**
	 * Constructor.
	 */
	public FileGatheringLog() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	
	@Override
	public void done() {
		super.done();
	}

	@Override
	public void newVirtualFile(DTNHost where, String filehash) {
		if (!isWarmup()) {
			write(format(getSimTime()) + " VirtualFile created in " + where + " (" + filehash + ")");
		}
	}

	@Override
	public void fileRequestDelegated(DTNHost from, DTNHost to, String filehash) {
		if (!isWarmup()) {
			write(format(getSimTime()) + " PendingDownload " + from + " to " + to);
		}
	}

	@Override
	public void pendingDownloadExpired(DTNHost where, DTNHost requestor,
			String filehash) {
		write(format(getSimTime()) + " PendingDownload expired in " + 
				where + " (" + filehash + " requested by "+requestor + ")");					
	}

	@Override
	public void fileRequestSatisfied(DTNHost where, String filehash,
			boolean selfSatisfied) {
		if (!isWarmup()) {
			if(selfSatisfied){
				write(format(getSimTime()) + " VirtualFile satisfied in " + 
						where + " without delegation (" + filehash + ")");	
				SimScenario.getInstance().getWorld().cancelSim();
			}
			else{
				write(format(getSimTime()) + " VirtualFile satisfied in " + 
						where + " using delegation  (" + filehash + ")");
			}
			
		}
	}

	@Override
	public void downloadFWDExpired(DTNHost where, DTNHost requestor,
			String filehash) {
		write(format(getSimTime()) + " DownloadFWD expired in " + 
				where + " (" + filehash + " requested by "+requestor + ")");	
	}

	@Override
	public void dataTransferred(DTNHost from, DTNHost to, int bytes) {
	}

	@Override
	public void downloadFWDReturned(DTNHost from, DTNHost requestor,
			String filehash) {
		write(format(getSimTime()) + " DownloadFWD returned from " + 
				from + " to " + requestor + " (" + filehash + ")");	
	}

	@Override
	public void pendingDownloadCompleted(DTNHost where, DTNHost requestor,
			String filehash) {
		write(format(getSimTime()) + " PendingDownload completed in " + 
				where + " (" + filehash + " requested by "+requestor + ")");	
	}
	
}
