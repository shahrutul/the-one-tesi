package report;

import core.DTNHost;
import core.FileEventListener;

public class FileGatheringReport extends Report implements FileEventListener{

	public static String HEADER="# time  event";
	private int created;
	private int satisfied;
	private int delegated;
	private int expired;
	private double timeSatisfied;
	
	/**
	 * Constructor.
	 */
	public FileGatheringReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
		created = 0;
		satisfied = 0;
		delegated = 0;
		timeSatisfied = 0;
		expired = 0;
		write(HEADER);
	}
	
	
	@Override
	public void done() {
		write("");
		write(created + " file requests created");
		write(delegated + " times a file requests has been delegated");
		write(satisfied + " file requests satisfied (in "+ format(timeSatisfied) + " s)");
		super.done();
	}

	@Override
	public void newVirtualFile(DTNHost where, String filehash) {
		if (!isWarmup()) {
			created++;
			write(format(getSimTime()) + " - query created in " + where + " (" + filehash + ")");
		}
	}

	@Override
	public void fileRequestDelegated(DTNHost from, DTNHost to, String filehash) {
		if (!isWarmup()) {
			delegated++;
			write(format(getSimTime()) + " PendingDownload " + from + " to " + to);
		}
	}

	@Override
	public void pendingDownloadExpired(DTNHost where, DTNHost requestor,
			String filehash) {
		write(format(getSimTime()) + " - PendingDownload expired in " + 
				where + " (" + filehash + " requested by "+requestor + ")");					
	}

	@Override
	public void fileRequestSatisfied(DTNHost where, String filehash,
			boolean selfSatisfied) {
		if (!isWarmup()) {
			satisfied++;
			timeSatisfied = getSimTime();
			if(selfSatisfied){
				write(format(getSimTime()) + " - file request satisfied in " + 
						where + " without delegation (" + filehash + ")");					
			}
			else{
				write(format(getSimTime()) + " - file request satisfied in " + 
						where + " using delegation  (" + filehash + ")");
			}
			
		}
	}

	@Override
	public void downloadFWDExpired(DTNHost where, DTNHost requestor,
			String filehash) {
		
		write(format(getSimTime()) + " - DownloadFWD expired in " + 
				where + " (" + filehash + " requested by "+requestor + ")");	
	}

	@Override
	public void dataTransferred(DTNHost from, DTNHost to, int bytes) {
		// TODO Auto-generated method stub
		
	}
	
}
