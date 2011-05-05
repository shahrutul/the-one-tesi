package report;

import core.DTNHost;
import core.FileEventListener;
import core.SimClock;
import core.SimScenario;

public class FileGatheringReport extends Report implements FileEventListener{

	public static String HEADER="# time  event";
	private int created;
	private int satisfied;
	private int delegated;
	private int downExpired;
	private int downCompleted;
	private int fwdExpired;
	private double timeSatisfied;
	private int fwdReturned;
	private long startTime;
	
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
		downExpired = 0;
		fwdExpired = 0;
		fwdReturned = 0;
		downCompleted = 0;
		startTime = System.currentTimeMillis();
		write(HEADER);
	}
	
	
	@Override
	public void done() {
		write("");
		write(created + " VirtualFile created");
		write(delegated + " times a task has been delegated");
		write(downCompleted + " PendingDownloads completed");
		write(downExpired + " PendingDownloads expired");
		write(fwdExpired + " DownloadFWDs expired");
		write(fwdReturned + " DownloadFWDs returned succesfully");		
		write(satisfied + " file requests satisfied");
		write(timeSatisfied + " : time first VirtualFile satisfied (seconds)");
		write(SimClock.getIntTime() + " : simulated time (seconds)");	
		double duration = (System.currentTimeMillis() - startTime)/1000.0;
		write(String.format("%.2f", duration) + " : simulation duration (seconds)");
		super.done();
	}

	@Override
	public void newVirtualFile(DTNHost where, String filehash) {
		if (!isWarmup()) {
			created++;
			write(format(getSimTime()) + " VirtualFile created in " + where + " (" + filehash + ")");
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
		downExpired++;
		write(format(getSimTime()) + " PendingDownload expired in " + 
				where + " (" + filehash + " requested by "+requestor + ")");					
	}

	@Override
	public void fileRequestSatisfied(DTNHost where, String filehash,
			boolean selfSatisfied) {
		if (!isWarmup()) {
			satisfied++;
			timeSatisfied = getSimTime();
			if(selfSatisfied){
				write(format(getSimTime()) + " VirtualFile satisfied in " + 
						where + " without delegation (" + filehash + ")");					
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
		fwdExpired++;
		write(format(getSimTime()) + " DownloadFWD expired in " + 
				where + " (" + filehash + " requested by "+requestor + ")");	
	}

	@Override
	public void dataTransferred(DTNHost from, DTNHost to, int bytes) {}

	@Override
	public void downloadFWDReturned(DTNHost from, DTNHost requestor,
			String filehash) {
		fwdReturned++;
		write(format(getSimTime()) + " DownloadFWD returned from " + 
				from + " to " + requestor + " (" + filehash + ")");	
	}

	@Override
	public void pendingDownloadCompleted(DTNHost where, DTNHost requestor,
			String filehash) {
		downCompleted++;
		write(format(getSimTime()) + " PendingDownload completed in " + 
				where + " (" + filehash + " requested by "+requestor + ")");	
	}
	
}
